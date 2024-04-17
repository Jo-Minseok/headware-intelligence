package com.headmetal.headwareintelligence

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material.AlertDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.alpha

@Preview(showBackground = true)
@Composable

fun Helmet() {
    var helmetid by remember {
        mutableStateOf("")
    }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "안전모 등록",
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                Spacer(modifier = Modifier.width(125.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp, vertical = 15.5.dp)
            ) {
                Box(

                ) {
                    Column(
                    ) {

                        Row {

                            Text(
                                text = "작업자 정보",
                                color = Color.Black,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row {

                            Text(
                                text = "작업자 ID : ",
                                color = Color.Black,
                                fontSize = 16.sp
                            )

                            Text(// 로그인 정보 연동 작업자 ID 출력
                                text = "gildong123",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Row {

                            Text(
                                text = "작업자 이름 : ",
                                color = Color.Black,
                                fontSize = 16.sp
                            )

                            Text(// 로그인 정보 연동 작업자 이름 출력
                                text = "홍길동",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        Column(
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "블루투스 연결",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        }
                        
                        Row {
                            Button(
                                onClick = {},
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    )  {
                                        Text(
                                            text = "연결하기",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        Row {
                            Button(
                                onClick = {},
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    )  {
                                        Text(
                                            text = "연결해제",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Column(
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "안전모 번호",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                            TextField(
                                value = helmetid,
                                onValueChange = { helmetid = it },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .width(350.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }

                        Row {
                            Button(
                                onClick = {},
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    )  {
                                        Text(
                                            text = "등록하기",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                       Row {
                           Button(
                               onClick = {},
                               elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                               colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                               shape = RoundedCornerShape(8.dp)
                           ) {
                               Row(
                                   verticalAlignment = Alignment.CenterVertically,
                                   horizontalArrangement = Arrangement.SpaceBetween
                               ) {
                                   Box(
                                       modifier = Modifier.weight(1f),
                                       contentAlignment = Alignment.Center
                                   )  {
                                       Text(
                                           text = "반납하기",
                                           fontWeight = FontWeight.Bold,
                                           color = Color.Black,
                                           fontSize = 16.sp
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
}