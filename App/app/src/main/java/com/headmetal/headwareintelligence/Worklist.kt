package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

@Composable
fun Worklist(navController: NavController) {
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
                    println("서버 통신 실패: ${t.message}")
                }
            })
        }
    }

    if (showWorkDataInputDialog) {
        InputWorkDataDialog(onDismissRequest = { showWorkDataInputDialog = false })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() }
            )
            Text(
                text = "작업장 관리",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 10.dp)
            )
            Text(
                text = "+ 작업장 생성",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 10.dp)
                    .clickable { showWorkDataInputDialog = true }
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 16.dp)
            ) {
                for (i in workshopId.indices) {
                    Button(
                        onClick = { navController.navigate("workScreen") },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            Color(255, 150, 0, 80)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Construction,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "작업장 아이디 : ${workshopId[i]}",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = workshopCompany[i],
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.End
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = workshopName[i],
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "시작일 : ${workshopStartDate[i].substring(2)}",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "종료일 : ${workshopEndDate[i]?.substring(2)}",
                                        color = Color.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputWorkDataDialog(
    onDismissRequest: () -> Unit
) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", null)

    var selectableCompany by remember { mutableStateOf(listOf<String>()) }
    var inputWorkName by remember { mutableStateOf("") }
    var inputWorkCompany by remember { mutableStateOf("") }
    var inputWorkStartDate by remember { mutableStateOf("") }
    var inputWorkEndDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
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
            Text(text = "작업장 생성")
        },
        text = {
            Column {
                Text("작업장 이름", modifier = Modifier.padding(bottom = 4.dp))
                TextField(
                    value = inputWorkName,
                    onValueChange = { inputWorkName = it },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(255, 150, 0, 80),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("담당 회사", modifier = Modifier.padding(bottom = 4.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = inputWorkCompany,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.menuAnchor(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(255, 150, 0, 80),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        selectableCompany.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    expanded = false
                                    inputWorkCompany = item
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("시작일", modifier = Modifier.padding(bottom = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp, end = 5.dp)
                    )
                    TextField(
                        value = inputWorkStartDate,
                        onValueChange = { inputWorkStartDate = it },
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(255, 150, 0, 80),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("종료일", modifier = Modifier.padding(bottom = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp, end = 5.dp)
                    )
                    TextField(
                        value = inputWorkEndDate,
                        onValueChange = { inputWorkEndDate = it },
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(255, 150, 0, 80),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    LoadingState.show()
                    if (userId != null) {
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
                                val dialog = builder.create()
                                dialog.show()
                                LoadingState.hide()
                            }

                            override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
                                Log.e("HEAD METAL", t.message.toString())
                                LoadingState.hide()
                            }
                        })
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("등록", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("취소", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun InputWorkDataDialogPreview() {
    InputWorkDataDialog(onDismissRequest = {})
}
