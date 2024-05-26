package com.headmetal.headwareintelligence

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class WeatherResponse(
    val temperature: Double,
    val airVelocity: Double,
    val precipitation: Double,
    val humidity: Double
)

class WeatherViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _temperature = mutableStateOf<Double?>(null)
    val temperature: State<Double?> = _temperature

    private val _airVelocity = mutableStateOf<Double?>(null)
    val airVelocity: State<Double?> = _airVelocity

    private val _precipitation = mutableStateOf<Double?>(null)
    val precipitation: State<Double?> = _precipitation

    private val _humidity = mutableStateOf<Double?>(null)
    val humidity: State<Double?> = _humidity

    var state: Boolean = false // 데이터 수신 상태 확인

    fun getWeather(city: String, district: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getWeather(city, district)
            _temperature.value = response.temperature
            _airVelocity.value = response.airVelocity
            _precipitation.value = response.precipitation
            _humidity.value = response.humidity
            state = !state // 모든 데이터를 수신한 뒤 상태를 전환
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Main(navController: NavController, weatherViewModel: WeatherViewModel = remember { WeatherViewModel() }) {
    val auto: SharedPreferences = LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val username: String = auto.getString("name", null).toString()

    BackOnPressed()

    var interest = 10
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

    var current by remember {
        mutableStateOf(Calendar.getInstance().time)
    }

    CoroutineScope(Dispatchers.Main).launch {
        LoadingState.show() // 로딩 창 출력
        val accidentResponseResult = withTimeoutOrNull(10000) { // 10초 동안 데이터를 수신하지 못할 경우 종료
            CoroutineScope(Dispatchers.IO).async { // 데이터를 받아오기 위해 IO 상태로 전환하여 비동기 처리
                val state = weatherViewModel.state // 현재 상태 값을 받아옴
                weatherViewModel.getWeather("부산광역시", "남구")
                while (state == weatherViewModel.state) {
                    // 상태 값이 전환될 때까지 반복(로딩) = 모든 데이터를 수신할 때까지 반복(로딩)
                }
            }.await() // 데이터를 받아올 때까지 대기
        }
        LoadingState.hide() // 로딩 창 숨김

        if (accidentResponseResult == null) { // 정해진 시간 동안 데이터를 수신하지 못한 경우 종료
            //
        }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(top = 30.dp)) {
                Row() {
                    Text(
                        text = "안녕하세요 ",
                        fontSize = 30.sp
                    )
                    Text(
                        text = username,
                        textDecoration = TextDecoration.Underline,
                        fontStyle = FontStyle.Italic,
                        fontSize = 30.sp
                    )
                    Text(
                        text = "님!",
                        fontSize = 30.sp
                    )
                }
                Text(
                    text = "오늘도 안전한 근무 되시길 바랍니다!",
                    fontSize = 15.sp
                )
            }
            Column(modifier = Modifier.padding(top = 15.dp)) {
                Box(
                    modifier = Modifier
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
                                text = SimpleDateFormat(
                                    "HH:mm",
                                    Locale.getDefault()
                                ).format(current),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 5.dp, bottom = 5.dp)
                            )
                        }

                    }
                }
                Box(
                    modifier = Modifier
                        .padding()
                        .background(color = Color.White)
                        .clickable { navController.navigate("trendScreen") }
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
                                fontWeight = FontWeight.Bold,
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
            }
            Column() {
                if (auto.getString("type", null) == "manager") {
                    Button(
                        onClick = { navController.navigate("mapScreen") },
                        modifier = Modifier.padding(),
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
                            ) {
                                Text(
                                    text = "사고 발생지 확인",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            navController.navigate("nullmapScreen")
                        },
                        modifier = Modifier.padding(),
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
                            ) {
                                Text(
                                    text = "미처리 사고 발생지 확인",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                else {
                    Button(
                        onClick = {
                            navController.navigate("helmetScreen")
                        },
                        modifier = Modifier.padding(),
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
                            ) {
                                Text(
                                    text = "안전모 등록",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(top = 30.dp)
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

            val temperature by weatherViewModel.temperature
            val airVelocity by weatherViewModel.airVelocity
            val precipitation by weatherViewModel.precipitation
            val humidity by weatherViewModel.humidity

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
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
                                    text = "1시간 강수량 : " + humidity.toString() + "mm",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                            Row {
                                Text(
                                    text = "기온 : " + temperature.toString() + "ºC",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
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
                                    text = airVelocity.toString() + "m/s",
                                    fontSize = 16.sp,
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

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(color = Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
                    .clickable { navController.navigate("countermeasuresScreen") }

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

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(color = Color.White)
                    .clickable { navController.navigate("processingScreen") }
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
        }
    }
}