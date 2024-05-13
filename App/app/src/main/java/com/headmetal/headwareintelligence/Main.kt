package com.headmetal.headwareintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun Main(navController: NavController, modifier: Modifier = Modifier,
    interest: Int = 10,
    windy: Int = 10, rainy: Int = 10, temp: Int = 10, /*dust:Int = 10*/) {
    val interestColor = when {
        interest < 10 -> Color.Red
        interest < 20 -> Color(0xFFFF6600)
        else -> Color.Green
    }
    val interestText = when {
        interest < 10 -> "매우 높음"
        interest < 20 -> "높음"
        else -> "보통"
    }
    val interestTextDetail = when {
        interest < 10 -> "각별한 안전 사고 주의가 필요해요"
        interest < 20 -> "안전 사고 주의가 필요해요"
        else -> "안전 관심은 항상 필요해요"
    }
    val windyColor = when {
        windy < 10 -> Color.Red
        windy < 20 -> Color(0xFFFF6600)
        else -> Color.Green
    }
    val windyText = when {
        windy < 10 -> "강풍경보"
        windy < 20 -> "강풍주의보"
        else -> "보통"
    }

    val rainyColor = when {
        rainy < 10 -> Color.Red
        rainy < 20 -> Color(0xFFFF6600)
        else -> Color.Green
    }
    val rainyText = when {
        rainy < 10 -> "호우경보"
        rainy < 20 -> "호우주의보"
        else -> "보통"
    }
    val tempColor = when {
        temp < 10 -> Color.Red
        temp < 20 -> Color(0xFFFF6600)
        else -> Color.Green
    }
    var current by remember {
        mutableStateOf(Calendar.getInstance().time)
    }
    var interestChecked by remember {
        mutableStateOf(false)
    }
    var accidentChecked by remember {
        mutableStateOf(false)
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
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .background(color = Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {
                Column {
                    Row {
                        Text(
                            text = "일일 안전 알림",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 2.dp)
                        )
                        Text(
                            text = SimpleDateFormat(
                                "EEEE, yyyy년 MM월 dd일",
                                Locale.getDefault()
                            ).format(current),
                            style = TextStyle(textAlign = TextAlign.End),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp, top = 2.dp)
                        )
                    }
                    Row {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, bottom = 5.dp)
                        )
                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(current),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                                .padding(start = 5.dp, bottom = 5.dp)
                        )
                    }

                }
            } //여기서부터
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .background(color = Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {
                Column {
                    Text(
                        text = "안전 관심도",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Row {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = interestColor,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, top = 5.dp)
                        )
                        Text(
                            text = interestText,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 5.dp, top = 5.dp)
                        )
                    }
                    Text(
                        text = interestTextDetail,
                        modifier = Modifier
                            .padding(start = 40.dp, bottom = 5.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Button(
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFF6600)),
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
                            text = "작업 현장 확인",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Button(
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFFB266)),
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
                            text = "안전모 등록",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End // 오른쪽 정렬을 지정합니다.
                ) {
                    Column {
                        Text(
                            text = "실시간 정보",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(260.dp))
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = "Refresh Icon",
                        tint = Color.LightGray, // 아이콘 색상
                        modifier = Modifier.size(24.dp) // 아이콘 크기 설정
                    )
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
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {

                Column {

                    Row {
                            //아이콘은 날시에 따라 바뀌게
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF00BFFF),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp)
                                .size(40.dp)

                        )
                        Column {
                            
                            Text(
                                text = "기상 정보 : 우천",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )
                            Row {
                                Text(
                                    text = "강수량 : ",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                                Text(
                                    text = "10mm",
                                    color = rainyColor,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                                Text(
                                    text = rainyText,
                                    color = rainyColor,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 5.dp)
                                )

                            }
                            Row {
                                Text(
                                    text = "기온 : ",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                )
                                Text(
                                    text = "10ºC",
                                    color = tempColor,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
                                )

                            }
                        }
                        Column {

                            Text(
                                text = "풍속",
                                fontSize = 16.sp,
                                style = TextStyle(textAlign = TextAlign.End),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp, top = 10.dp)
                            )
                            Row {
                                Text(
                                    text = "16m/s",
                                    fontSize = 16.sp,
                                    color = windyColor,
                                    style = TextStyle(textAlign = TextAlign.End),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 10.dp)
                                )

                            }
                            Row {
                                Text(
                                    text = windyText,
                                    fontSize = 16.sp,
                                    color = windyColor,
                                    style = TextStyle(textAlign = TextAlign.End),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 10.dp)
                                )

                            }
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
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {
                Column {

                    Row {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null,
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, top = 25.dp, bottom = 25.dp)
                                .size(40.dp)

                        )
                        Column {

                            Row {
                                Text(
                                    text = "주의 행동 요령",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, top = 30.dp)
                                )

                            }

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
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()

            ) {

                Column {

                    Row {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, top = 25.dp, bottom = 25.dp)
                                .size(40.dp)

                        )
                        Column {

                            Row {
                                Text(
                                    text = "사고 처리 내역",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, top = 30.dp)
                                )

                            }

                        }
                    }
                }
            }
         //   NavigationBar(navController = navController)
        }
    }
}