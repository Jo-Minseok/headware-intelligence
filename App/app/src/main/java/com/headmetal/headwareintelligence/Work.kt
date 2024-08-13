package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
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

@Preview(showBackground = true)
@Composable
fun WorkPreview() {
    Work()
}

@Preview(showBackground = true)
@Composable
fun RefixWorkshopPreview(){
    RefixWorkshop()
}

@Preview(showBackground = true)
@Composable
fun RemoveWorkshopPreview(){
    RemoveWorkshop()
}

@Preview(showBackground = true)
@Composable
fun AddWorkerPreview(){
    AddWorker()
}

@Preview(showBackground = true)
@Composable
fun WorkerCardPreview(){
    WorkerCard()
}

@Preview(showBackground = true)
@Composable
fun InputWorkUpdateDialogPreview(){
    InputWorkUpdateDialog()
}

@Preview(showBackground = true)
@Composable
fun WorkDeleteDialogPreview(){
    WorkDeleteDialog()
}

@Preview(showBackground = true)
@Composable
fun WorkerAddDialogPreview(){
    WorkerAddDialog()
}

@Preview(showBackground = true)
@Composable
fun WorkerManageDialogPreview(){
    WorkerManageDialog()
}

@Composable
fun Work(workId: Int = 0, navController: NavController? = null) {
    // UI 변수 초기화
    var showWorkDataInputDialog by remember { mutableStateOf(false) }
    var showWorkDeleteDialog by remember { mutableStateOf(false) }
    var showWorkerAddDialog by remember { mutableStateOf(false) }
    var showWorkerManageDialog by remember { mutableStateOf(false) }
    var workerId by remember { mutableStateOf(listOf<String>()) }
    var workerName by remember { mutableStateOf(listOf<String>()) }
    val selectedWorkerId = remember { mutableStateOf("") }

    // 작업장 수정 다이얼 로그
    if (showWorkDataInputDialog) {
        InputWorkUpdateDialog(
            onDismissRequest = { showWorkDataInputDialog = false },
            workId = workId
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
        WorkerAddDialog(onDismissRequest = { showWorkerAddDialog = false }, workId = workId)
    }

    // 작업자 관리 다이얼 로그
    if (showWorkerManageDialog) {
        WorkerManageDialog(
            onDismissRequest = { showWorkerManageDialog = false },
            workerId = selectedWorkerId.value
        )
    }

    // UI 시작
    LaunchedEffect(showWorkerAddDialog) {
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
                }
            }

            override fun onFailure(call: Call<Worker>, t: Throwable) {
                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
            }
        })
    }

    // 뒤로 가기 버튼 + 화면
    Screen(content = {
        // 화면
        Column {
            // 제목
            ScreenTitleText(text = "작업장 이름")

            // 작업장 수정, 삭제 부분
            Row {
                RefixWorkshop { showWorkDataInputDialog = true }
                Spacer(modifier = Modifier.weight(1f))
                RemoveWorkshop { showWorkDeleteDialog = true }
            }

            // 작업장 수정 - 등록 사이 공백
            Spacer(modifier = Modifier.height(30.dp))

            // + 작업자 등록
            AddWorker { showWorkerAddDialog = true }

            // 작업자 목록
            Surface {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(vertical = 16.dp)
                ) {
                    // 작업자 카드 등록
                    for (i in workerId.indices) {
                        WorkerCard(workerId = workerId[i], workerName = workerName[i])
                    }
                }
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputWorkUpdateDialog(
    onDismissRequest: () -> Unit = {},
    workId: Int = 0
) {
    // UI 변수 초기화
    var selectableCompanyList by remember { mutableStateOf(listOf<String>()) }
    var inputWorkName by remember { mutableStateOf("") }
    var inputWorkCompany by remember { mutableStateOf("") }
    var inputWorkStartDate by remember { mutableStateOf("") }
    var inputWorkEndDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val builder = android.app.AlertDialog.Builder(context)

    // 다이얼 로그 시작
    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val companyList: CompanyList? = response.body()
                    companyList?.let {
                        selectableCompanyList = it.companies
                    }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                println("서버 통신 실패: ${t.message}")
            }
        })
    }

    // 다이얼 로그 띄우기
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text="작업장 수정", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("작업장", modifier = Modifier.padding(bottom = 4.dp))
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
                        selectableCompanyList.forEach { item ->
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
                            val dialog = builder.create()
                            dialog.show()
                            LoadingState.hide()
                        }

                        override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
                            Log.e("HEAD METAL", t.message.toString())
                            LoadingState.hide()
                        }
                    })
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

@Composable
fun WorkDeleteDialog(
    onDismissRequest: () -> Unit = {},
    workId: Int = 0,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val builder = android.app.AlertDialog.Builder(context)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "작업장 삭제") },
        text = { Text("작업장을 삭제하시겠습니까?\n※ 한 번 삭제하면 되돌릴 수 없습니다.") },
        confirmButton = {
            Button(
                onClick = {
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
                                navController!!.navigateUp()
                            } else {
                                builder.setTitle("작업장 삭제 실패")
                                builder.setMessage("입력한 내용을 다시 한 번 확인해주세요.")
                                builder.setPositiveButton("확인") { dialog, _ ->
                                    dialog.dismiss()
                                }
                            }
                            val dialog = builder.create()
                            dialog.show()
                            LoadingState.hide()
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("HEAD METAL", t.message.toString())
                            LoadingState.hide()
                        }
                    })
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
    onDismissRequest: () -> Unit = {},
    workId: Int = 0
) {
    var workerId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val builder = android.app.AlertDialog.Builder(context)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "작업자 등록")
        },
        text = {
            Column {
                Text("아이디", modifier = Modifier.padding(bottom = 4.dp))
                TextField(
                    value = workerId,
                    onValueChange = { workerId = it },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(255, 150, 0, 80),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
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
                                builder.setMessage("입력한 내용을 다시 한 번 확인해주세요.")
                                builder.setPositiveButton("확인") { dialog, _ ->
                                    dialog.dismiss()
                                }
                            }
                            val dialog = builder.create()
                            dialog.show()
                            LoadingState.hide()
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("HEAD METAL", t.message.toString())
                            LoadingState.hide()
                        }
                    })
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

@Composable
fun WorkerManageDialog(
    onDismissRequest: () -> Unit = {},
    workerId: String = "testId"
) {
    var workerName by remember { mutableStateOf("") }
    var workerPhone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.searchWorkerStatus(workerId)
            .enqueue(object : Callback<WorkerStatus> {
                override fun onResponse(
                    call: Call<WorkerStatus>,
                    response: Response<WorkerStatus>
                ) {
                    if (response.isSuccessful) {
                        val workerStatus: WorkerStatus? = response.body()
                        workerStatus?.let {
                            workerName = it.name
                            workerPhone = it.phoneNo
                            Log.d("HEAD METAL", workerName)
                            Log.d("HEAD METAL", workerPhone)
                        }
                    }
                }

                override fun onFailure(call: Call<WorkerStatus>, t: Throwable) {
                    Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Engineering,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("이름", modifier = Modifier.padding(bottom = 4.dp))
                        TextField(
                            value = workerName,
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(255, 150, 0, 80),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("전화번호", modifier = Modifier.padding(bottom = 4.dp))
                        TextField(
                            value = workerPhone,
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(255, 150, 0, 80),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
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
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("확인", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("해제", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun RefixWorkshop(showWorkDataInputDialog:() -> Unit = {}){
    Row(modifier = Modifier.clickable { showWorkDataInputDialog() }) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier
        )
        Text(
            text = "작업장 수정",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 5.dp, top = 3.dp)
        )
    }
}

@Composable
fun RemoveWorkshop(showDeleteDialog: () -> Unit = {}){
    Row(modifier = Modifier.clickable { showDeleteDialog() }) {
        Icon(
            imageVector = Icons.Default.RestoreFromTrash,
            contentDescription = "Trash",
            tint = Color.Red
        )
        Text(
            text = "작업장 삭제",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Red,
            modifier = Modifier
                .padding(start = 5.dp, top = 3.dp)
        )
    }
}

@Composable
fun AddWorker(showWorkerAddDialog: () -> Unit = {}){
    Text(
        text = "+ 작업자 등록",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color(0xFF388E3C),
        modifier = Modifier.clickable { showWorkerAddDialog() }
    )
}

@Composable
fun WorkerCard(workerId:String = "testId",
               workerName:String = "testName",
               selectedWorkerId:MutableState<String> = remember{mutableStateOf("")},
               showWorkerManageDialog:MutableState<Boolean> = remember{ mutableStateOf(false)}){
    Button(
        onClick = {
            selectedWorkerId.value = workerId
            showWorkerManageDialog.value = true
        },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            Color(255, 150, 0, 80)
        ),
        modifier = Modifier
            .fillMaxWidth()
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