package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Surface
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import retrofit2.http.GET
import retrofit2.http.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrendViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    // 기울기와 절편을 상태로 정의합니다.
    private val _inclination = mutableStateOf(0.0)
    val inclination: State<Double> = _inclination

    private val _intercept = mutableStateOf(0.0)
    val intercept: State<Double> = _intercept

    // API로부터 데이터를 가져오는 함수
    fun getTrendData(start: String, end: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getTrendData(start, end)
                // API에서 받은 기울기와 절편을 상태에 저장합니다.
                _inclination.value = response.inclination
                _intercept.value = response.intercept
            } catch (e: Exception) {
                // 오류 처리
                // 데이터를 가져오는 도중 오류가 발생한 경우에 대한 처리
                Log.e("TrendViewModel", "Failed to fetch trend data", e)
                // 예외 처리를 하고 사용자에게 적절한 안내를 제공하거나 기본값으로 설정할 수 있습니다.
                // 여기에서는 기본값으로 0으로 설정합니다.
                _inclination.value = 0.0
                _intercept.value = 0.0
            }
        }
    }
}
data class TrendResponse(
    val inclination: Double,
    val intercept: Double
)

@Preview(showBackground = true)
@Composable
fun Trend(viewModel: TrendViewModel = remember { TrendViewModel() }, interest: Int = 10) {
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
    var interestChecked by remember {
        mutableStateOf(false)
    }
    var accidentChecked by remember {
        mutableStateOf(false)
    }

    val inclination by viewModel.inclination
    val intercept by viewModel.intercept

    LaunchedEffect(Unit) {
        viewModel.getTrendData("2024-02-01", "2024-04-01")
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        LaunchedEffect(true) {
            while (true) {
                delay(1000)
                current = Calendar.getInstance().time
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
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
            } //여기까지 메인 화면에서 공통으로 사용되는 부분
            Spacer(
                modifier = Modifier.height(50.dp)
            )
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "안전 추세선",
                        fontSize = 20.sp
                    )
                    Row {
                        Text(
                            text = "기간 ",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(end = 0.dp, top = 20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 300.dp, top = 20.dp)
                        )
                        //DatePicker 구현
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // 데이터가 유효한 경우에만 그래프 그리기
                            if (inclination != 0.0 && intercept != 0.0) {
                                val startY = (size.height * (1 - inclination) + intercept).toFloat() // 시작점의 y 좌표 계산
                                val endY = (size.height * (0 - inclination) + intercept).toFloat() // 끝점의 y 좌표 계산

                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, startY),
                                    end = Offset(size.width, endY),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                    Row (
                        modifier = Modifier
                        .padding(top = 30.dp)
                    ){
                        Checkbox(
                            checked = interestChecked,
                            onCheckedChange = { checked ->
                                interestChecked = checked
                                if (checked) {
                                    //
                                } else {
                                    //
                                }
                            })
                        Text(
                            text = "관심도",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(
                            modifier = Modifier.width(20.dp)
                        )
                        Checkbox(
                            checked = accidentChecked,
                            onCheckedChange = { checked ->
                                accidentChecked = checked
                                if (checked) {
                                    //
                                } else {
                                    //
                                }
                            })
                        Text(
                            text = "사고 발생",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}