package com.headmetal.headwareintelligence

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Worker(
    val workerId: List<String>,
    val name: List<String>
)

data class WorkerStatus(
    val name: String,
    val phoneNo: String
)

/**
 * 작업장 메인 화면 프리뷰
 */
@Preview(showBackground = true)
@Composable
fun WorkPreview() {
    Work(navController = rememberNavController(), workshopName = "작업장 이름", workId = 1)
}

/**
 * 작업장 수정 버튼
 */
@Preview(showBackground = true)
@Composable
fun RefixWorkshopPreview() {
    IconWithLabel(
        icon = Icons.Filled.Settings,
        iconColor = Color.Black,
        textColor = Color.Black,
        text = "작업장 수정",
        onClick = { })
}

/**
 * 작업장 삭제 버튼
 */
@Preview(showBackground = true)
@Composable
fun RemoveWorkshopPreview() {
    IconWithLabel(
        icon = Icons.Default.RestoreFromTrash,
        iconColor = Color.Red,
        textColor = Color.Red,
        text = "작업장 삭제",
        onClick = { })
}

/**
 * 작업자 등록 버튼
 */
@Preview(showBackground = true)
@Composable
fun AddWorkerPreview() {
    IconWithLabel(
        icon = Icons.Filled.Add,
        iconColor = Color(0xFF388E3C),
        textColor = Color(0xFF388E3C),
        text = "작업자 등록",
        onClick = {}
    )
}

/**
 * 작업자 아이템 UI
 */
@Preview(showBackground = true)
@Composable
fun WorkerCardPreview() {
    WorkerCard(
        workerId = "testId",
        workerName = "testName",
        selectedWorkerId = remember { mutableStateOf("1") },
        showWorkerManageDialog = remember { mutableStateOf(false) }
    )
}

/**
 * 작업장 수정 UI
 */
@Preview(showBackground = true)
@Composable
fun WorkUpdateDialogPreview() {
    WorkUpdateDialog(
        onDismissRequest = {},
        workId = 0,
        navController = rememberNavController()
    )
}

/**
 * 작업장 삭제 다이얼로그
 */
@Preview(showBackground = true)
@Composable
fun WorkDeleteDialogPreview() {
    WorkDeleteDialog(
        onDismissRequest = {},
        navController = rememberNavController(),
        workId = 0
    )
}

/**
 * 작업자 등록 다이얼로그
 */
@Preview(showBackground = true)
@Composable
fun WorkerAddDialogPreview() {
    WorkerAddDialog(
        onDismissRequest = {},
        workId = 0,
        navController = rememberNavController()
    )
}

/**
 * 작업자 관리 UI
 */
@Preview(showBackground = true)
@Composable
fun WorkerManageDialogPreview() {
    WorkerManageDialog(
        onDismissRequest = {},
        workerId = "0",
        navController = rememberNavController()
    )
}

/**
 * 작업장 메인 화면
 */
@Composable
fun Work(workId: Int, workshopName: String, navController: NavController) {
    // UI 변수 초기화
    var showWorkDataInputDialog by remember { mutableStateOf(false) }
    var showWorkDeleteDialog by remember { mutableStateOf(false) }
    var showWorkerAddDialog by remember { mutableStateOf(false) }
    var workerId by remember { mutableStateOf(listOf<String>()) }
    var workerName by remember { mutableStateOf(listOf<String>()) }
    val selectedWorkerId: MutableState<String> = remember { mutableStateOf("") }
    val showWorkerManageDialog: MutableState<Boolean> = remember { mutableStateOf(false) }

    // 작업장 수정 다이얼 로그
    if (showWorkDataInputDialog) {
        WorkUpdateDialog(
            onDismissRequest = { showWorkDataInputDialog = false },
            workId = workId,
            navController = navController
        )
    }

    // 작업장 삭제 다이얼 로그
    if (showWorkDeleteDialog) {
        WorkDeleteDialog(
            onDismissRequest = { showWorkDeleteDialog = false },
            workId = workId,
            navController = navController
        )
    }

    // 작업자 등록 다이얼 로그
    if (showWorkerAddDialog) {
        WorkerAddDialog(
            onDismissRequest = { showWorkerAddDialog = false },
            workId = workId,
            navController = navController
        )
    }

    // 작업자 관리 다이얼 로그
    if (showWorkerManageDialog.value) {
        WorkerManageDialog(
            onDismissRequest = { showWorkerManageDialog.value = false },
            workerId = selectedWorkerId.value,
            navController = rememberNavController()
        )
    }

    LaunchedEffect(Unit) {
        LoadingState.show()
        RetrofitInstance.apiService.searchWorker(workId).enqueue(object : Callback<Worker> {
            override fun onResponse(
                call: Call<Worker>,
                response: Response<Worker>
            ) {
                if (response.isSuccessful) {
                    val worker: Worker? = response.body()
                    worker?.let {
                        workerId = it.workerId
                        workerName = it.name
                    }
                } else {
                    errorBackApp(
                        navController = navController,
                        error = response.message(),
                        title = "작업장 검색 오류",
                        message = "작업장에 대한 정보를 찾을 수 없습니다."
                    )
                }
                LoadingState.hide()
            }

            override fun onFailure(call: Call<Worker>, t: Throwable) {
                errorBackApp(
                    navController = navController,
                    error = t.toString(),
                    title = "작업장 검색 오류",
                    message = "네트워크 문제로 작업장에 대한 정보를 찾을 수 없습니다."
                )
            }
        })
    }

    // 뒤로 가기 버튼 + 화면
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            // 제목
            ScreenTitleText(text = workshopName)

            Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                // 작업장 수정, 삭제 부분
                Row {
                    IconWithLabel(
                        icon = Icons.Filled.Settings,
                        iconColor = Color.Black,
                        textColor = Color.Black,
                        text = "작업장 수정",
                        onClick = { showWorkDataInputDialog = true }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconWithLabel(icon = Icons.Default.RestoreFromTrash,
                        iconColor = Color.Red,
                        textColor = Color.Red,
                        text = "작업장 삭제",
                        onClick = { showWorkDeleteDialog = true })
                }
                Row {
                    IconWithLabel(
                        icon = Icons.Filled.Add,
                        iconColor = Color(0xFF388E3C),
                        textColor = Color(0xFF388E3C),
                        text = "작업자 등록",
                        onClick = { showWorkerAddDialog = true }
                    )
                }
            }

            // 작업자 목록
            LazyColumn(
                modifier = Modifier.fillMaxHeight(), // fillMaxSize()를 사용해 LazyColumn이 가능한 공간을 모두 차지하도록 설정
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 작업자 카드 등록
                items(workerId.size) { i ->
                    WorkerCard(
                        workerId = workerId[i],
                        workerName = workerName[i],
                        selectedWorkerId = selectedWorkerId,
                        showWorkerManageDialog = showWorkerManageDialog
                    )
                }
            }
        }
    )
}

@Composable
fun WorkUpdateDialog(
    onDismissRequest: () -> Unit,
    workId: Int,
    navController: NavController
) {
    // UI 변수 초기화
    var selectableCompanyList by remember { mutableStateOf(listOf<String>()) }
    val inputWorkName: MutableState<String> = remember { mutableStateOf("") }
    val inputWorkCompany: MutableState<String> = remember { mutableStateOf("") }
    val inputWorkStartDate: MutableState<String> = remember { mutableStateOf("") }
    val inputWorkEndDate: MutableState<String> = remember { mutableStateOf("") }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    val context: Context = LocalContext.current
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    // 다이얼 로그 시작
    LaunchedEffect(Unit) {
        LoadingState.show()
        // 담당회사 API
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val companyList: CompanyList? = response.body()
                    companyList?.let {
                        selectableCompanyList = it.companies
                    }
                } else {
                    errorBackApp(
                        navController = navController,
                        error = response.message(),
                        title = "회사 목록 로드 오류",
                        message = "회사 목록을 로드 할 수 없습니다.",
                        action = onDismissRequest
                    )
                }
                LoadingState.hide()
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                errorBackApp(
                    navController = navController,
                    error = t.toString(),
                    title = "회사 목록 로드 오류",
                    message = "회사 목록을 로드 할 수 없습니다.",
                    action = onDismissRequest
                )
            }
        })
    }

    // 다이얼 로그 띄우기
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            AlertTitleText(text = "작업장 수정")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LabelAndInputComposable(
                    labelText = "작업장", inputText = inputWorkName, colors = TextFieldDefaults.colors(
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
                    selectableItems = selectableCompanyList,
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
            TextButton(onClick = {
                updateWorkshopVerify(
                    workId = workId,
                    inputWorkName = inputWorkName.value,
                    inputWorkCompany = inputWorkCompany.value,
                    inputWorkStartDate = inputWorkStartDate.value,
                    inputWorkEndDate = inputWorkEndDate.value,
                    builder = builder,
                    onDismissRequest = onDismissRequest,
                    navController = navController
                )
            },
                content = {
                    Text("수정", color = Color.Black, fontWeight = FontWeight.Bold)
                })
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = {
                    Text(text = "취소", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            )
        }
    )
}

@Composable
fun WorkDeleteDialog(
    onDismissRequest: () -> Unit,
    workId: Int,
    navController: NavController
) {
    val context: Context = LocalContext.current
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "작업장 삭제") },
        text = {
            Column {
                Text("작업장을 삭제하시겠습니까?")
                Text("※ 한 번 삭제하면 되돌릴 수 없습니다.", color = Color.Red)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    removeWorkshop(
                        workId = workId,
                        builder = builder,
                        navController = navController,
                        onDismissRequest = onDismissRequest
                    )
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("예", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("아니오", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun WorkerAddDialog(
    onDismissRequest: () -> Unit,
    workId: Int,
    navController: NavController
) {
    val workerId: MutableState<String> = remember { mutableStateOf("") }
    val context: Context = LocalContext.current
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "작업자 등록")
        },
        text = {
            LabelAndInputComposable(
                labelText = "아이디", inputText = workerId, colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(255, 150, 0, 80),
                    unfocusedContainerColor = Color(255, 150, 0, 80),
                    disabledContainerColor = Color(255, 150, 0, 80),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                addWorker(
                    workId = workId,
                    workerId = workerId.value,
                    builder = builder,
                    navController = navController,
                    onDismissRequest = onDismissRequest
                )
            },
                content = {
                    Text("등록", color = Color.Black, fontWeight = FontWeight.Bold)
                })
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest,
                content = {
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold)
                })
        }
    )
}

@Composable
fun WorkerManageDialog(
    onDismissRequest: () -> Unit,
    workerId: String,
    navController: NavController
) {
    val workerName: MutableState<String> = remember { mutableStateOf("") }
    val workerPhone: MutableState<String> = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        LoadingState.show()
        RetrofitInstance.apiService.searchWorkerStatus(workerId)
            .enqueue(object : Callback<WorkerStatus> {
                override fun onResponse(
                    call: Call<WorkerStatus>,
                    response: Response<WorkerStatus>
                ) {
                    if (response.isSuccessful) {
                        val workerStatus: WorkerStatus? = response.body()
                        workerStatus?.let {
                            workerName.value = it.name
                            workerPhone.value = it.phoneNo
                        }
                    } else {
                        errorBackApp(
                            navController = navController,
                            error = response.message(),
                            title = "작업자 검색 오류",
                            message = "작업자에 대한 정보를 찾을 수 없습니다.",
                            action = onDismissRequest
                        )
                    }
                    LoadingState.hide()
                }

                override fun onFailure(call: Call<WorkerStatus>, t: Throwable) {
                    errorBackApp(
                        navController = navController,
                        error = t.toString(),
                        title = "작업자 검색 오류",
                        message = "네트워크 문제로 인해 작업자에 대한 정보를 찾을 수 없습니다."
                    )
                }
            })
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "작업자 관리")
        },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Engineering,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LabelAndInputComposable(
                            labelText = "이름",
                            inputText = workerName,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(121, 121, 121, 80),
                                unfocusedContainerColor = Color(121, 121, 121, 80),
                                disabledContainerColor = Color(121, 121, 121, 80),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            readOnly = true
                        )
                        LabelAndInputComposable(
                            labelText = "전화번호", inputText = workerPhone,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(121, 121, 121, 80),
                                unfocusedContainerColor = Color(121, 121, 121, 80),
                                disabledContainerColor = Color(121, 121, 121, 80),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            readOnly = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("헬멧 등록 상태 : ON", modifier = Modifier.padding(bottom = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VideoCameraFront,
                        contentDescription = null,
                        tint = Color(0xFFFF6600),
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 8.dp, end = 5.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color(0xFFFF6600),
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 8.dp, end = 5.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                content = { Text("확인", color = Color.Black, fontWeight = FontWeight.Bold) })
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = { Text("삭제", color = Color.Red, fontWeight = FontWeight.Bold) })
        }
    )
}

@Composable
fun WorkerCard(
    workerId: String,
    workerName: String,
    selectedWorkerId: MutableState<String>,
    showWorkerManageDialog: MutableState<Boolean>
) {
    Button(
        onClick = {
            selectedWorkerId.value = workerId
            showWorkerManageDialog.value = true
        },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            Color(0xFFFBDFBE)
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Engineering,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "작업자 아이디 : $workerId",
                    color = Color.Black,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workerName,
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun updateWorkshopVerify(
    workId: Int,
    inputWorkName: String,
    inputWorkCompany: String,
    inputWorkStartDate: String,
    inputWorkEndDate: String,
    builder: AlertDialog.Builder,
    onDismissRequest: () -> Unit,
    navController: NavController
) {
    if (isInvalidWorkName(inputWorkName)) {
        builder.setTitle("작업장 이름 길이 제한")
            .setMessage("작업장 이름은 최대 16자 입력 가능합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    } else if (isInvalidStartDate(inputWorkStartDate)) {
        builder.setTitle("시작 날짜 검증 실패")
            .setMessage("작업 시작 날짜는 yyyy-mm-dd 형식이어야 하며, 1970-01-01 이후여야 합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    } else if (isInvalidEndDate(inputWorkStartDate, inputWorkEndDate)) {
        builder.setTitle("날짜 검증 실패")
            .setMessage("작업 종료 날짜는 시작 날짜 이후여야 하며, yyyy-mm-dd 형식이어야 합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    } else {
        updateAction(
            workId = workId,
            inputWorkName = inputWorkName,
            inputWorkCompany = inputWorkCompany,
            inputWorkStartDate = inputWorkStartDate,
            inputWorkEndDate = inputWorkEndDate,
            builder = builder,
            onDismissRequest = onDismissRequest,
            navController = navController
        )
    }
}

fun updateAction(
    workId: Int,
    inputWorkName: String,
    inputWorkCompany: String,
    inputWorkStartDate: String,
    inputWorkEndDate: String,
    builder: AlertDialog.Builder,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.updateWork(
        workId,
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
                builder.setTitle("작업장 수정 성공")
                builder.setMessage("작업장 수정에 성공하였습니다.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    onDismissRequest()
                }
            } else {
                builder.setTitle("작업장 수정 실패")
                builder.setMessage("입력한 내용을 다시 한 번 확인해주세요.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create().show()
            LoadingState.hide()
        }

        override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 수정 오류",
                message = "네트워크 문제로 작업장 수정을 할 수 없습니다."
            )
        }
    })
}

fun removeWorkshop(
    workId: Int,
    builder: AlertDialog.Builder,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.deleteWork(
        workId
    ).enqueue(object : Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                builder.setTitle("작업장 삭제 성공")
                builder.setMessage("작업장 삭제에 성공하였습니다.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    onDismissRequest()
                }
                navController.navigateUp()
            } else {
                builder.setTitle("작업장 삭제 실패")
                builder.setMessage("작업장 삭제를 실패했습니다.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    onDismissRequest()
                }
            }
            builder.create().show()
            LoadingState.hide()
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 삭제 오류",
                message = "네트워크 문제로 인해 작업장에 대한 정보를 찾을 수 없습니다.",
                action = onDismissRequest
            )
        }
    })
}

fun addWorker(
    workId: Int,
    workerId: String,
    builder: AlertDialog.Builder,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.assignWork(
        workId,
        workerId
    ).enqueue(object : Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                builder.setTitle("작업자 등록 성공")
                builder.setMessage("작업자 등록에 성공하였습니다.")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    onDismissRequest()
                }
            } else {
                builder.setTitle("작업자 등록 실패")
                builder.setMessage(response.message())
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    onDismissRequest()
                }
            }
            builder.create().show()
            LoadingState.hide()
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업자 추가 오류",
                message = "네트워크 오류로 인해 작업자를 추가할 수 없습니다.",
                action = onDismissRequest
            )
        }
    })
}