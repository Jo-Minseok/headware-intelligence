package com.headmetal.headwareintelligence

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun Countermeasures(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() }
            )
            Text(
                text = "안전 행동 요령",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = Color(0xFF008000),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
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
                                    "3. 안전대 부착설비 확인",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 6.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = Color(0xFFFF6600),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
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
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 6.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = Color(0xFFCC9900),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
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
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
