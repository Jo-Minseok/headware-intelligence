package com.headmetal.headwareintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun Processing(navController: NavController, modifier: Modifier = Modifier) {
    var searchText by remember {
        mutableStateOf("")
    }
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
            )
            Text(
                text = "처리 내역",
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Box(
                modifier = modifier.height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    singleLine = true,
                    placeholder = { // 워터마크로 사용할 힌트 텍스트
                        Text("사고 처리 내역 검색")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxSize()
                )
            }
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Tab(
                    modifier = modifier.background(color = Color.White),
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "사고 처리", fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                )
                Tab(
                    modifier = modifier.background(color = Color.White),
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "오작동 처리", fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTabIndex) {
                    0 -> ProcessingHistoryScreen() //사고처리
                    1 -> MalfunctionHistoryScreen() //오작동처리
                }
            }
        }
    }
}

@Composable
fun ProcessingHistoryScreen() {

    Box(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
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
                    text = "#사건번호", //사건 번호는 #2024040101 양식 날짜와 사건 발생 순의 번호
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                )
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier
                        .size(27.dp)
                        .align(Alignment.CenterVertically)
                        .padding(start = 5.dp, top = 12.dp)
                )
                Text(
                    text = "처리내역 : 119신고", // 처리 내역에 따라 텍스트는 바뀜
                    style = TextStyle(textAlign = TextAlign.End),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, top = 10.dp)
                )

            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.TripOrigin,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp, top = 1.dp)
                )

                Text(
                    text = "사고 위치", //사건 번호는 #2024040101 양식 날짜와 사건 발생 순의 번호
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Row {
                Text(
                    text = "사고 위치 주소",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 38.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )


            Row {
                Icon(
                    imageVector = Icons.Default.TripOrigin,
                    contentDescription = null,
                    tint = Color(0xFFFF6600),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp, top = 1.dp)
                )

                Text(
                    text = "사고 발생자", //사건 번호는 #2024040101 양식 날짜와 사건 발생 순의 번호
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Row {
                Text(
                    text = "사고 발생자 이름",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 38.dp, bottom = 10.dp)
                )
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            Row {
                Text(
                    text = "사고 내역 : ",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                )

                Text(
                    text = "낙상사고", //사고 내역 값 받기?
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Row {
                Text(
                    text = "사고 발생 일시 : ",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                )

                Text(
                    text = "사고 발생 일시", //사고 발생 날짜/시간 값
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Row {
                Text(
                    text = "사고 처리 일시 : ",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                )

                Text(
                    text = "사고 처리 일시", //사고 처리 일시 날짜/시간 값
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

        }
    }
}

@Composable
fun MalfunctionHistoryScreen() {
    Text(
        "오작동 내역 임시"
    )
}