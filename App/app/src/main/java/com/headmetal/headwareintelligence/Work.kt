package com.headmetal.headwareintelligence

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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

@Composable
fun Work(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var workshopName by remember { mutableStateOf("") }
    var workshopId by remember { mutableStateOf("") }
    var workshopStartDate by remember { mutableStateOf("") }
    var workshopEndDate by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false)}
    var workerId by remember{ mutableStateOf("")}
    var showWorkerDialog by remember { mutableStateOf(false)}
    var workerName by remember { mutableStateOf("")}
    var workerPhone by remember { mutableStateOf("")}
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
                text = "작업장 이름",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 10.dp)
            )
            Row {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 5.dp)
                        .clickable { showDialog = true }
                )
                Text(
                    text = "작업장 수정",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(start = 5.dp, top = 5.dp)
                        .clickable { showDialog = true }
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.RestoreFromTrash,
                    contentDescription = "Trash",
                    tint = Color.Red,
                    modifier = Modifier.clickable {showDeleteDialog = true }
                )
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(text = "작업장 수정")
                        },
                        text = {
                            Column {
                                Text("작업장 이름", modifier = Modifier.padding(bottom = 4.dp))
                                TextField(
                                    value = workshopId,
                                    onValueChange = { workshopId = it },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor =  Color(255, 150, 0, 80),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("담당 회사", modifier = Modifier.padding(bottom = 4.dp))
                                TextField(
                                    value = workshopName,
                                    onValueChange = { workshopName = it },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor =  Color(255, 150, 0, 80),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("시작일", modifier = Modifier.padding(bottom = 4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 5.dp)
                                            .clickable {
                                                // 달력 아이콘 클릭 시 처리할 로직 추가
                                            }
                                    )
                                    TextField(
                                        value = workshopStartDate,
                                        onValueChange = { workshopStartDate = it },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor =  Color(255, 150, 0, 80),
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("종료일", modifier = Modifier.padding(bottom = 4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 5.dp)
                                            .clickable {
                                                // 달력 아이콘 클릭 시 처리할 로직 추가
                                            }
                                    )
                                    TextField(
                                        value = workshopEndDate,
                                        onValueChange = { workshopEndDate = it },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor =  Color(255, 150, 0, 80),
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(Color.Transparent)
                            ) {
                                Text("등록",color = Color.Black, fontWeight=FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(Color.Transparent)
                            ) {
                                Text("취소",color = Color.Black, fontWeight=FontWeight.Bold)
                            }
                        }
                    )
                }
                Text(
                    text = "작업장 삭제",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(start = 5.dp, top = 5.dp, end = 20.dp)
                        .clickable { showDeleteDialog = true }
                )
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = "작업장 삭제") },
                    text = { Text("작업장을 삭제하시겠습니까?\n" +
                            "※ 한 번 삭제하면 되돌릴 수 없습니다.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("예",color = Color.Black, fontWeight=FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDeleteDialog = false },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("아니오",color = Color.Black, fontWeight=FontWeight.Bold)
                        }
                    }
                )
            }
            Text(
                text = "+ 작업자 등록",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF388E3C),
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(top = 30.dp)
                    .clickable { showAddDialog = true }
            )
            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
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
                                    backgroundColor =  Color(255, 150, 0, 80),
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
                                showAddDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("등록",color = Color.Black, fontWeight=FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("취소",color = Color.Black, fontWeight=FontWeight.Bold)
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 5.dp)
            ) {
                Button(
                    onClick = {showWorkerDialog=true},
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
                        // 아이콘
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 작업장 아이디
                                Text(
                                    text = "작업자 아이디 : xxx",
                                    color = Color.Black,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 작업장 이름 (중앙 정렬)
                            Text(
                                text = "작업자 이름",
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                        }
                    }
                }
            }
            if (showWorkerDialog) {
                AlertDialog(
                    onDismissRequest = { showWorkerDialog = false },
                    title = {
                        Text(text = "작업자 관리")
                    },
                    text = {
                        Column {
                            // 아이콘과 텍스트 필드를 수직 중앙 정렬
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
                                    // 이름 텍스트와 필드
                                    Text("이름", modifier = Modifier.padding(bottom = 4.dp))
                                    TextField(
                                        value = workerName,
                                        onValueChange = { workerName = it },
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
                                    // 전화번호 텍스트와 필드
                                    Text("전화번호", modifier = Modifier.padding(bottom = 4.dp))
                                    TextField(
                                        value = workerPhone,
                                        onValueChange = { workerPhone = it },
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
                            // 헬멧 등록 상태와 시작일 필드
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

                                        }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showWorkerDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("확인", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showWorkerDialog = false },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("해제", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}
