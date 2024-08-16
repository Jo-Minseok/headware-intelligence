package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

data class WorkShopInputData(
    val name: String,
    val company: String,
    val startDate: String,
    val endDate: String?
)

data class WorkShopList(
    val workId: List<Int>,
    val name: List<String>,
    val company: List<String>,
    val startDate: List<String>,
    val endDate: List<String?>
)

/**
 * WorkList 전체 화면 프리뷰
 */
@Preview(showBackground = true)
@Composable
fun WorkListPreview() {
    WorkList(navController = rememberNavController())
}

/**
 * 작업장 생성 다이얼로그
 */
@Preview(showBackground = true)
@Composable
fun WorkCreateDialogPreview() {
    WorkCreateDialog(onDismissRequest = {})
}

/**
 * 작업장 아이템 프리뷰
 */
@Preview(showBackground = true)
@Composable
fun WorkItemPreview() {
    WorkItem(
        workshopId = 1,
        workshopCompany = "company",
        workshopName = "test",
        workshopStartDate = "2024-01-01",
        workshopEndDate = "2024-01-02",
        navController = rememberNavController()
    )
}

/**
 * WorkList 전체 화면
 */
@Composable
fun WorkList(navController: NavController) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", null)

    var showWorkDataInputDialog by remember { mutableStateOf(false) }
    var workshopId by remember { mutableStateOf(listOf<Int>()) }
    var workshopCompany by remember { mutableStateOf(listOf<String>()) }
    var workshopName by remember { mutableStateOf(listOf<String>()) }
    var workshopStartDate by remember { mutableStateOf(listOf<String>()) }
    var workshopEndDate by remember { mutableStateOf(listOf<String?>()) }

    LaunchedEffect(showWorkDataInputDialog) {
        if (userId != null) {
            RetrofitInstance.apiService.searchWork(userId).enqueue(object : Callback<WorkShopList> {
                override fun onResponse(
                    call: Call<WorkShopList>,
                    response: Response<WorkShopList>
                ) {
                    if (response.isSuccessful) {
                        val workShopList: WorkShopList? = response.body()
                        workShopList?.let {
                            workshopId = it.workId
                            workshopName = it.name
                            workshopCompany = it.company
                            workshopStartDate = it.startDate
                            workshopEndDate = it.endDate
                        }
                    }
                }

                override fun onFailure(call: Call<WorkShopList>, t: Throwable) {
                    Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
                }
            })
        }
    }

    /**
     * 작업장 생성 다이얼로그 띄우기
     */
    if (showWorkDataInputDialog) {
        WorkCreateDialog(onDismissRequest = { showWorkDataInputDialog = false })
    }

    /**
     * 메인 화면
     */
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            Column {
                ScreenTitleText(text = "작업장 관리")
                IconWithLabel(
                    icon = Icons.Filled.Add,
                    iconColor = Color(0xFF388E3C),
                    textColor = Color(0xFF388E3C),
                    text = "작업장 등록",
                    onClick = { showWorkDataInputDialog = true }
                )
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(vertical = 16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        for (i in workshopId.indices) {
                            WorkItem(
                                workshopId = workshopId[i],
                                workshopName = workshopName[i],
                                workshopCompany = workshopCompany[i],
                                workshopStartDate = workshopStartDate[i],
                                workshopEndDate = workshopEndDate[i],
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    )
}

/**
 * 작업장 생성 다이얼로그 띄우기
 */
@Composable
fun WorkCreateDialog(
    onDismissRequest: () -> Unit
) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", "") ?: ""

    var selectableCompany by remember { mutableStateOf(listOf<String>()) }
    val inputWorkName = remember { mutableStateOf("") }
    val inputWorkCompany = remember { mutableStateOf("") }
    val inputWorkStartDate = remember { mutableStateOf("") }
    val inputWorkEndDate = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val builder = AlertDialog.Builder(context)

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val companyList: CompanyList? = response.body()
                    companyList?.let {
                        selectableCompany = it.companies
                    }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                println("서버 통신 실패: ${t.message}")
            }
        })
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            AlertTitleText("작업장 생성")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelAndInputComposable(
                    labelText = "작업장 이름",
                    inputText = inputWorkName,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(255, 150, 0, 80),
                        unfocusedContainerColor = Color(255, 150, 0, 80),
                        disabledContainerColor = Color(255, 150, 0, 80),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
                LabelAndDropdownMenu(
                    fieldText = "담당 회사",
                    expanded = expanded,
                    selectedItem = inputWorkCompany,
                    selectableItems = selectableCompany,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(255, 150, 0, 80),
                        unfocusedContainerColor = Color(255, 150, 0, 80),
                        disabledContainerColor = Color(255, 150, 0, 80),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
                Date(labelText = "시작일", inputText = inputWorkStartDate)
                Date(labelText = "종료일", inputText = inputWorkEndDate)
            }
        },
        confirmButton = {
            TextButton(
                content = {
                    Text(text = "등록", color = Color.Black, fontWeight = FontWeight.Bold)
                },
                onClick = {
                    enrollWorkshopVerify(
                        userId = userId,
                        inputWorkName = inputWorkName.value,
                        inputWorkCompany = inputWorkCompany.value,
                        inputWorkStartDate = inputWorkStartDate.value,
                        inputWorkEndDate = inputWorkEndDate.value,
                        builder = builder,
                        onDismissRequest = onDismissRequest
                    )
                }
            )
        },
        dismissButton = {
            TextButton(
                content = {
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold)
                },
                onClick = onDismissRequest
            )
        }
    )
}

/**
 * 작업장 아이템 UI
 */
@Composable
fun WorkItem(
    workshopId: Int,
    workshopName: String,
    workshopCompany: String,
    workshopStartDate: String,
    workshopEndDate: String?,
    navController: NavController
) {
    Button(
        onClick = { navController.navigate("WorkScreen/${workshopId}/${workshopName}") },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            Color(255, 150, 0, 80)
        )
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "작업장 아이디 : $workshopId",
                        color = Color.Black,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = workshopCompany,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
                Text(
                    text = workshopName,
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "시작일 : ${workshopStartDate.substring(2)}",
                        color = Color.Black,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "종료일 : ${workshopEndDate?.substring(2)}",
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

fun enrollWorkshopVerify(
    userId: String,
    inputWorkName: String,
    inputWorkCompany: String,
    inputWorkStartDate: String,
    inputWorkEndDate: String,
    builder: AlertDialog.Builder,
    onDismissRequest: () -> Unit,
) {
    if (inputWorkName.length > 16) {
        builder.setTitle("작업장 이름 길이 제한")
        builder.setMessage("작업장 이름은 최대 16자 입력 가능합니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    } else if (isInvalidStartDate(inputWorkStartDate)) {
        builder.setTitle("시작 날짜 검증 실패")
        builder.setMessage("작업 시작 날짜는 yyyy-mm-dd 형식이어야 하며, 1970-01-01 이후여야 합니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    } else if (isInvalidEndDate(inputWorkStartDate, inputWorkEndDate)) {
        builder.setTitle("날짜 검증 실패")
        builder.setMessage("작업 종료 날짜는 시작 날짜 이후여야 하며, yyyy-mm-dd 형식이어야 합니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }else {
        enrollAction(
            userId = userId,
            inputWorkName = inputWorkName,
            inputWorkCompany = inputWorkCompany,
            inputWorkStartDate = inputWorkStartDate,
            inputWorkEndDate = inputWorkEndDate,
            builder = builder,
            onDismissRequest = onDismissRequest
        )
    }
}

fun isInvalidStartDate(inputWorkStartDate: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false // 날짜 엄격 검증

        val startDate = dateFormat.parse(inputWorkStartDate)
        val minDate = dateFormat.parse("1970-01-01")

        startDate == null || startDate.before(minDate)
    } catch (e: Exception) {
        true // 날짜 형식이 잘못된 경우
    }
}

fun isInvalidEndDate(inputWorkStartDate: String, inputWorkEndDate: String): Boolean {
    // 종료 날짜가 비어 있는 경우 검증을 하지 않음
    if (inputWorkEndDate.isEmpty()) {
        return false
    }

    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false // 날짜 엄격 검증

        val startDate = dateFormat.parse(inputWorkStartDate)
        val endDate = dateFormat.parse(inputWorkEndDate)

        endDate == null || endDate.before(startDate) // 종료 날짜가 시작 날짜보다 이전이면 오류
    } catch (e: Exception) {
        true // 날짜 형식이 잘못된 경우
    }
}

/**
 * 작업장 생성 등록 버튼 기능
 */
fun enrollAction(
    userId: String,
    inputWorkName: String,
    inputWorkCompany: String,
    inputWorkStartDate: String,
    inputWorkEndDate: String,
    builder: AlertDialog.Builder,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.createWork(
        userId,
        WorkShopInputData(
            inputWorkName,
            inputWorkCompany,
            inputWorkStartDate,
            inputWorkEndDate
        )
    ).enqueue(object : Callback<WorkShopInputData> {
        override fun onResponse(
            call: Call<WorkShopInputData>,
            response: Response<WorkShopInputData>
        ) {
            if (response.isSuccessful) {
                builder.setTitle("작업장 생성 성공")
                builder.setMessage("작업장 생성에 성공하였습니다.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    onDismissRequest()
                }
            } else {
                builder.setTitle("작업장 생성 실패")
                builder.setMessage("입력한 내용을 다시 한 번 확인해주세요.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create().show()
            LoadingState.hide()
        }

        override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
            Log.e("HEAD METAL", t.message.toString())
            LoadingState.hide()
        }
    })
}