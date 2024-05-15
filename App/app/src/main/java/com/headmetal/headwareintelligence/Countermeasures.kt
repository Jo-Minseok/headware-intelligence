package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun Countermeasures(navController: NavController) {
    val auto: SharedPreferences = LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val username = auto.getString("name", null)
    BackOnPressed()
    var current by remember {
        mutableStateOf(Calendar.getInstance().time)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        LaunchedEffect(true) {
            while (true) {
                delay(1000)
                current = Calendar.getInstance().time
            }
        }

        Column(modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
                    .clickable {navController.navigateUp() }
            )

            Box(
                modifier = Modifier.padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column (verticalArrangement = Arrangement.Center){

                        Text(
                            text = "안전 행동 요령",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(260.dp))
                }
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .background(color = Color.White)
                    .border(
                        width = 3.dp,
                        color = Color(0xFF008000),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {
                Column {

                    Column {

                        Row {
                            Text(
                                text = "평상시 행동 요령",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )

                        }
                        Row {
                            Text(
                                text = "1. 안전난간 & 작업발판 설치 확인\n" +
                                "2. 추락 방호망 설치 확인\n" +
                                "3. 안전대 부착설비 확인\n",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .background(color = Color.White)
                    .border(
                        width = 3.dp,
                        color = Color(0xFFFF6600),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {
                Column {

                    Column {

                        Row {
                            Text(
                                text = "특보시 행동 요령",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )

                        }
                        Row {
                            Text(
                                text = "날씨별 특보 예보에 관한 안전 수칙 내용.",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .background(color = Color.White)
                    .border(
                        width = 3.dp,
                        color = Color(0xFFCC9900),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {
                Column {

                    Column {

                        Row {
                            Text(
                                text = "00 중 행동 요령",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )

                        }
                        Row {
                            Text(
                                text = "00날씨 중에 관한 안전 수칙",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier.height(5.dp)
            )
        }
    }
}

