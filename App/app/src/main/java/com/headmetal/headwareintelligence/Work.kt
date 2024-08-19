package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

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
    Work(
        workId = 1,
        workshopName = "작업장 이름",
        workshopCompany = "",
        workshopStartDate = "",
        workshopEndDate = "",
        navController = rememberNavController()
    )
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
        workshopName = "",
        workshopCompany = "",
        workshopStartDate = "",
        workshopEndDate = "",
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
        navController = rememberNavController(),
        workerName = remember {
            mutableStateOf(listOf())
        },
        workerIdList = remember {
            mutableStateOf(listOf())
        }
    )
}

/**
 * 작업자 관리 UI
 */
@Preview(showBackground = true)
@Composable
fun WorkerManageDialogPreview() {
    WorkerManageDialog(
        workId = 0,
        onDismissRequest = {},
        workerId = "0",
        navController = rememberNavController(),
        workerIdList = remember {
            mutableStateOf(listOf())
        },
        workerNameList = remember {
            mutableStateOf(listOf())
        }
    )
}

/**
 * 작업장 메인 화면
 */
@Composable
fun Work(
    workId: Int,
    workshopName: String,
    workshopCompany: String,
    workshopStartDate: String,
    workshopEndDate: String,
    navController: NavController
) {
    // UI 변수 초기화
    var showWorkDataInputDialog by remember { mutableStateOf(false) }
    var showWorkDeleteDialog by remember { mutableStateOf(false) }
    var showWorkerAddDialog by remember { mutableStateOf(false) }
    val workerId = remember { mutableStateOf(listOf<String>()) }
    val workerName = remember { mutableStateOf(listOf<String>()) }
    val selectedWorkerId: MutableState<String> = remember { mutableStateOf("") }
    val showWorkerManageDialog: MutableState<Boolean> = remember { mutableStateOf(false) }

    // 작업장 수정 다이얼 로그
    if (showWorkDataInputDialog) {
        WorkUpdateDialog(
            onDismissRequest = { showWorkDataInputDialog = false },
            workId = workId,
            workshopName = workshopName,
            workshopCompany = workshopCompany,
            workshopStartDate = workshopStartDate,
            workshopEndDate = workshopEndDate,
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
            navController = navController,
            workerIdList = workerId,
            workerName = workerName
        )
    }

    // 작업자 관리 다이얼 로그
    if (showWorkerManageDialog.value) {
        WorkerManageDialog(
            workId = workId,
            onDismissRequest = { showWorkerManageDialog.value = false },
            workerId = selectedWorkerId.value,
            navController = rememberNavController(),
            workerIdList = workerId,
            workerNameList = workerName
        )
    }

    LaunchedEffect(Unit) {
        workerListGET(
            workId = workId,
            navController = navController,
            workerId = workerId,
            workerName = workerName
        )
    }

    // 뒤로 가기 버튼 + 화면
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            // 제목
            ScreenTitleText(text = workshopName)

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
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
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 16.dp), // fillMaxSize()를 사용해 LazyColumn이 가능한 공간을 모두 차지하도록 설정
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 작업자 카드 등록
                items(workerId.value.size) { i ->
                    WorkerCard(
                        workerId = workerId.value[i],
                        workerName = workerName.value[i],
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
    workshopName: String,
    workshopCompany: String,
    workshopStartDate: String,
    workshopEndDate: String,
    navController: NavController
) {
    // UI 변수 초기화
    val selectableCompanyList = remember { mutableStateOf(listOf<String>()) }
    val inputWorkName: MutableState<String> = remember { mutableStateOf(workshopName) }
    val inputWorkCompany: MutableState<String> = remember { mutableStateOf(workshopCompany) }
    val inputWorkStartDate: MutableState<String> = remember { mutableStateOf(workshopStartDate) }
    val inputWorkEndDate: MutableState<String> = remember { mutableStateOf(workshopEndDate) }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }

    // 다이얼 로그 시작
    LaunchedEffect(Unit) {
        companyListGET(
            companyList = selectableCompanyList,
            navController = navController,
            onDismissRequest = onDismissRequest
        )
    }

    // 다이얼 로그 띄우기
    YesNoAlertDialog(
        title = "작업장 수정",
        yesButton = "수정",
        noButton = "취소",
        confirmButton = {
            updateWorkshopVerify(
                workId = workId,
                inputWorkName = inputWorkName.value,
                inputWorkCompany = inputWorkCompany.value,
                inputWorkStartDate = inputWorkStartDate.value,
                inputWorkEndDate = inputWorkEndDate.value,
                onDismissRequest = onDismissRequest,
                navController = navController
            )
        },
        dismissButton = onDismissRequest,
        textComposable = {
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
                    selectableItems = selectableCompanyList.value,
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
        }
    )
}

@Composable
fun WorkDeleteDialog(
    onDismissRequest: () -> Unit,
    workId: Int,
    navController: NavController
) {
    YesNoAlertDialog(
        title = "작업장 삭제",
        yesButton = "예",
        noButton = "아니오",
        confirmButton = {
            removeWorkshopDELETE(
                workId = workId,
                navController = navController,
                onDismissRequest = onDismissRequest
            )
        },
        dismissButton = onDismissRequest,
        textComposable = {
            Column {
                Text("작업장을 삭제하시겠습니까?")
                Text("※ 한 번 삭제하면 되돌릴 수 없습니다.", color = Color.Red)
            }
        }
    )
}

@Composable
fun WorkerAddDialog(
    onDismissRequest: () -> Unit,
    workId: Int,
    navController: NavController,
    workerIdList: MutableState<List<String>>,
    workerName: MutableState<List<String>>
) {
    val workerId: MutableState<String> = remember { mutableStateOf("") }
    YesNoAlertDialog(
        title = "작업자 등록",
        yesButton = "등록",
        noButton = "취소",
        textComposable = {
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
            addWorkerPOST(
                workId = workId,
                workerId = workerId.value,
                navController = navController,
                onDismissRequest = {
                    onDismissRequest()
                    workerListGET(
                        workId = workId,
                        workerId = workerIdList,
                        workerName = workerName,
                        navController = navController
                    )
                }
            )
        },
        dismissButton = onDismissRequest
    )
}

@Composable
fun WorkerManageDialog(
    workId: Int,
    onDismissRequest: () -> Unit,
    workerId: String,
    navController: NavController,
    workerIdList: MutableState<List<String>>,
    workerNameList: MutableState<List<String>>
) {
    val workerName: MutableState<String> = remember { mutableStateOf("") }
    val workerPhone: MutableState<String> = remember { mutableStateOf("") }

    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val imageUrl: MutableState<String?> = remember { mutableStateOf(null) }
    val webSocketSendData: MutableState<String?> = remember { mutableStateOf(null) }
    val imageDataReception: MutableState<Boolean> = remember { mutableStateOf(false) }
    val soundDataReception: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isWebSocketDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
    val webSocketMessage: MutableState<String> = remember { mutableStateOf("") }
    val client = remember { OkHttpClient() }
    val webSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("HEAD METAL", text)

            val messages = text.split(":")
            val manager = sharedAccount.getString("userid", null).toString()

            if (webSocketSendData.value == "카메라" && messages[1] == manager && messages[2] == "카메라완료") {
                imageUrl.value =
                    "http://minseok821lab.kro.kr:8000/accident/get_image/${workId}/${manager}"
                imageDataReception.value = true
            } else if (webSocketSendData.value == "소리" && messages[1] == manager && messages[2] == "소리완료") {
                LoadingState.hide()
                webSocketMessage.value = "소리 출력이 완료되었습니다."
                isWebSocketDialogVisible.value = true
                soundDataReception.value = true
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "사고 상황 업데이트 오류",
                message = "네트워크 문제로 인해 사고 상황 업데이트가 되지 않았습니다.",
            )
        }
    }

    imageUrl.value?.let { url ->
        Box(contentAlignment = Alignment.Center) {
            val painter =
                rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                    .data(url).build(),
                    onSuccess = { LoadingState.hide() })
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(0.6f)
            )
            Button(
                onClick = { imageUrl.value = null },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .zIndex(1f),
                colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        workerGET(
            workerId = workerId,
            onDismissRequest = onDismissRequest,
            navController = navController,
            workerName = workerName,
            workerPhone = workerPhone
        )
    }
    YesNoAlertDialog(
        title = "작업자 관리",
        yesButton = "확인",
        noButton = "삭제",
        textComposable = {
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
                            .clickable {
                                scope.launch(Dispatchers.IO) {
                                    LoadingState.show()
                                    val request = Request.Builder().url(
                                        "ws://minseok821lab.kro.kr:8000/accident/ws/${workId}/${
                                            sharedAccount.getString(
                                                "userid", null
                                            ).toString()
                                        }"
                                    ).build()
                                    val webSocket = client.newWebSocket(request, webSocketListener)
                                    val timeOut = withTimeoutOrNull(10000) {
                                        CoroutineScope(Dispatchers.IO).async {
                                            webSocketSendData.value = "카메라"
                                            webSocket.send("${workerId}:${webSocketSendData.value}")
                                            while (!imageDataReception.value) {
                                                //
                                            }
                                        }.await()
                                    }
                                    LoadingState.hide()

                                    if (timeOut == null) {
                                        Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                                        webSocketMessage.value = "서버에서 데이터를 불러오지 못하였습니다."
                                        isWebSocketDialogVisible.value = true
                                    } else {
                                        imageDataReception.value = false
                                    }

                                    webSocket.close(1000, "WebSocket Close")
                                }
                            }
                    )
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color(0xFFFF6600),
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 8.dp, end = 5.dp)
                            .clickable {
                                scope.launch(Dispatchers.IO) {
                                    LoadingState.show()
                                    val request = Request.Builder().url(
                                        "ws://minseok821lab.kro.kr:8000/accident/ws/${workId}/${
                                            sharedAccount.getString(
                                                "userid", null
                                            ).toString()
                                        }"
                                    ).build()
                                    val webSocket = client.newWebSocket(request, webSocketListener)
                                    val timeOut = withTimeoutOrNull(10000) {
                                        CoroutineScope(Dispatchers.IO).async {
                                            webSocketSendData.value = "소리"
                                            webSocket.send("${workerId}:${webSocketSendData.value}")
                                            while (!soundDataReception.value) {
                                                //
                                            }
                                        }.await()
                                    }
                                    LoadingState.hide()

                                    if (timeOut == null) {
                                        Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                                        webSocketMessage.value = "서버에서 데이터를 불러오지 못하였습니다."
                                        isWebSocketDialogVisible.value = true
                                    } else {
                                        soundDataReception.value = false
                                    }

                                    webSocket.close(1000, "WebSocket Close")
                                }
                            }
                    )
                }
            }
        },
        confirmButton = onDismissRequest,
        dismissButton = {
            removeWorkerDELETE(
                workId = workId,
                employeeId = workerId,
                navController = navController,
                onDismissRequest = {
                    onDismissRequest()
                    workerListGET(
                        workId = workId,
                        navController = navController,
                        workerId = workerIdList,
                        workerName = workerNameList
                    )
                }
            )
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
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
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
    onDismissRequest: () -> Unit,
    navController: NavController
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
        updateWorkshopPUT(
            workId = workId,
            inputWorkName = inputWorkName,
            inputWorkCompany = inputWorkCompany,
            inputWorkStartDate = inputWorkStartDate,
            inputWorkEndDate = inputWorkEndDate,
            onDismissRequest = onDismissRequest,
            navController = navController
        )
    }
}

