package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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
    val endDate: List<String>
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
    WorkCreateDialog(
        onDismissRequest = {},
        navController = rememberNavController(),
        workshopId = remember { mutableStateOf(listOf()) },
        workshopName = remember { mutableStateOf(listOf()) },
        userId = "",
        workshopCompany = remember {
            mutableStateOf(listOf())
        },
        workshopStartDate = remember {
            mutableStateOf(listOf())
        },
        workshopEndDate = remember {
            mutableStateOf(listOf())
        }
    )
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
    val userId: String = sharedAccount.getString("userid", "null") ?: "null"

    var showWorkDataInputDialog by remember { mutableStateOf(false) }
    val workshopId = remember { mutableStateOf(listOf<Int>()) }
    val workshopCompany = remember { mutableStateOf(listOf<String>()) }
    val workshopName = remember { mutableStateOf(listOf<String>()) }
    val workshopStartDate = remember { mutableStateOf(listOf<String>()) }
    val workshopEndDate = remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        if (userId != "null") {
            workshopListGET(
                workshopId = workshopId,
                userId = userId,
                workshopName = workshopName,
                navController = navController,
                workshopCompany = workshopCompany,
                workshopStartDate = workshopStartDate,
                workshopEndDate = workshopEndDate
            )
        } else {
            errorBackApp(
                navController = navController,
                error = "Not Id",
                title = "작업장 목록 오류",
                message = "작업장 목록을 불러올 수 없습니다.",
            )
        }
    }

    /**
     * 작업장 생성 다이얼로그 띄우기
     */
    if (showWorkDataInputDialog) {
        WorkCreateDialog(
            onDismissRequest = { showWorkDataInputDialog = false },
            navController = navController,
            userId = userId,
            workshopId = workshopId,
            workshopName = workshopName,
            workshopCompany = workshopCompany,
            workshopStartDate = workshopStartDate,
            workshopEndDate = workshopEndDate
        )
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
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(workshopId.value.size) { i ->
                        WorkItem(
                            workshopId = workshopId.value[i],
                            workshopName = workshopName.value[i],
                            workshopCompany = workshopCompany.value[i],
                            workshopStartDate = workshopStartDate.value[i],
                            workshopEndDate = workshopEndDate.value[i],
                            navController = navController
                        )
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
    onDismissRequest: () -> Unit,
    navController: NavController,
    userId: String,
    workshopId: MutableState<List<Int>>,
    workshopName: MutableState<List<String>>,
    workshopCompany: MutableState<List<String>>,
    workshopStartDate: MutableState<List<String>>,
    workshopEndDate: MutableState<List<String>>
) {
    val selectableCompany = remember { mutableStateOf(listOf<String>()) }
    val inputWorkName: MutableState<String> = remember { mutableStateOf("") }
    val inputWorkCompany: MutableState<String> = remember { mutableStateOf("") }
    val inputWorkStartDate: MutableState<String> = remember { mutableStateOf("") }
    val inputWorkEndDate: MutableState<String> = remember { mutableStateOf("") }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        companyListGET(
            companyList = selectableCompany,
            navController = navController,
            onDismissRequest = onDismissRequest
        )
    }
    YesNoAlertDialog(
        title = "작업장 생성", yesButton = "등록", noButton = "취소", textComposable = {
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
                    selectableItems = selectableCompany.value,
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
            enrollWorkshopVerify(
                userId = userId,
                inputWorkName = inputWorkName.value,
                inputWorkCompany = inputWorkCompany.value,
                inputWorkStartDate = inputWorkStartDate.value,
                inputWorkEndDate = inputWorkEndDate.value,
                navController = navController,
                onDismissRequest = {
                    onDismissRequest()
                    workshopListGET(
                        userId = userId,
                        navController = navController,
                        workshopId = workshopId,
                        workshopName = workshopName,
                        workshopCompany = workshopCompany,
                        workshopStartDate = workshopStartDate,
                        workshopEndDate = workshopEndDate
                    )
                }
            )
        },
        dismissButton = onDismissRequest
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
            Color(0xFFFFEDD9)
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterVertically),
                tint = Color.Black
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
    navController: NavController,
    onDismissRequest: () -> Unit,
) {
    if (isInvalidWorkName(inputWorkName)) {
        showAlertDialog(
            context = navController.context,
            title = "작업장 이름 길이 제한",
            message = "작업장 이름은 최대 16자 입력 가능합니다.",
            buttonText = "확인",
            onButtonClick = {}
        )
    } else if (isInvalidStartDate(inputWorkStartDate)) {
        showAlertDialog(
            context = navController.context,
            title = "시작 날짜 검증 실패",
            message = "작업 시작 날짜는 yyyy-mm-dd 형식이어야 하며, 1970-01-01 이후여야 합니다.",
            buttonText = "확인",
            onButtonClick = {}
        )
    } else if (isInvalidEndDate(inputWorkStartDate, inputWorkEndDate)) {
        showAlertDialog(
            context = navController.context,
            title = "날짜 검증 실패",
            message = "작업 종료 날짜는 yyyy-mm-dd 형식이어야 하며, 시작 날짜 이후여야 합니다.",
            buttonText = "확인",
            onButtonClick = {}
        )
    } else {
        workshopPOST(
            userId = userId,
            inputWorkName = inputWorkName,
            inputWorkCompany = inputWorkCompany,
            inputWorkStartDate = inputWorkStartDate,
            inputWorkEndDate = inputWorkEndDate,
            navController = navController,
            onDismissRequest = onDismissRequest
        )
    }
}