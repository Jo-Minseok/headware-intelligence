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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

@Composable
fun Worklist(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var workshopName by remember { mutableStateOf("") }
    var workshopId by remember { mutableStateOf("") }
    var workshopStartDate by remember { mutableStateOf("") }
    var workshopEndDate by remember { mutableStateOf("") }
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
                    .clickable { showDialog = true }
            )
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(text = "작업장 생성")
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = {},
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
                                // 작업장 아이디
                                Text(
                                    text = "작업장 아이디 : xxx",
                                    color = Color.Black,
                                    fontSize = 12.sp
                                )

                                // 건설 회사명 (오른쪽 상단)
                                Text(
                                    text = "건설 회사명",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.End
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 작업장 이름 (중앙 정렬)
                            Text(
                                text = "작업장 이름",
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 시작일과 종료일
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "시작일 :",
                                    color = Color.Black,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "종료일 :",
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
