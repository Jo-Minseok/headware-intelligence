package com.headmetal.headwareintelligence

import android.graphics.Paint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Surface
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.legend.verticalLegendItem
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.legend.VerticalLegend
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class TrendViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _month_data = mutableStateOf(emptyList<Int>())
    val month_data: State<List<Int>> = _month_data

    private val _inclination = mutableStateOf(0.0)
    val inclination: State<Double> = _inclination

    private val _intercept = mutableStateOf(0.0)
    val intercept: State<Double> = _intercept

    fun getTrendData(start: String, end: String) {
        viewModelScope.launch {
            val response = apiService.getTrendData(start, end)
            _month_data.value = response.month_data
            _inclination.value = response.inclination
            _intercept.value = response.intercept
        }
    }
}
data class TrendResponse(
    val month_data: List<Int>,
    val inclination: Double,
    val intercept: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun Trend(viewModel: TrendViewModel = remember { TrendViewModel() }) {
    val current by remember {
        mutableStateOf(Calendar.getInstance().time)
    }

    val month_data by viewModel.month_data
    val inclination by viewModel.inclination
    val intercept by viewModel.intercept

    val interestColor = when {
        inclination < -5 -> Color.Red
        inclination < 0 -> Color(0xFFFF6600)
        else -> Color.Green
    }
    val interestText = when {
        inclination < -5 -> "매우 높음"
        inclination < 0 -> "높음"
        else -> "보통"
    }
    val interestTextDetail = when {
        inclination < -5 -> "각별한 안전 사고 주의가 필요해요"
        inclination < 0 -> "안전 사고 주의가 필요해요"
        else -> "안전 관심은 항상 필요해요"
    }

    var expanded by remember { mutableStateOf(false) }
    val options = generateOptions()
    var selectedOption by remember { mutableStateOf(options[0]) }
    var previousSelectedOption by remember { mutableStateOf(options[0]) }

    //var chartVisible by remember { mutableStateOf(true) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
//        LaunchedEffect(Unit) {
//            while (true) {
//                delay(1000)
//                current = Calendar.getInstance().time
//            }
//        }
        LaunchedEffect(selectedOption) {
            val (startMonth, endMonth) = getMonthsFromOption(selectedOption)
            viewModel.getTrendData(startMonth, endMonth)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        Text(
                            text = "기간 ",
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            TextField(
                                value = selectedOption,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                }
                            ) {
                                options.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            previousSelectedOption = selectedOption
                                            selectedOption = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(5.dp)
                        ) {
                            if (!expanded) {
                                chartPrint(
                                    month_data = month_data,
                                    inclination = inclination,
                                    intercept = intercept,
                                    selectedOption = selectedOption
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun chartPrint(month_data: List<Int>, inclination: Double, intercept: Double, selectedOption: String) {
    val yValues = mutableListOf<Double>()
    for (x in month_data.indices) {
        yValues.add(x * inclination + intercept)
    }
    val monthDataProducer = ChartEntryModelProducer(
        month_data.mapIndexed { index, value ->
            FloatEntry(index.toFloat(), value.toFloat())
        }
    )
    val trendDataProducer = ChartEntryModelProducer(
        yValues.mapIndexed { index, value ->
            FloatEntry(index.toFloat(), value.toFloat())
        }
    )

    Chart(
        chart = columnChart( // 월별 사고 데이터 차트 부분
            columns = listOf(
                lineComponent(
                    color = Color.Blue,
                    thickness = 10.dp,
                    shape = Shapes.cutCornerShape(topRightPercent = 20, topLeftPercent = 20)
                )
            ),
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = 0f,
                maxY = month_data.maxOrNull()?.toFloat()?.times(1.1f) ?: Float.MIN_VALUE
            )
        ).plus(
            com.patrykandpatrick.vico.compose.chart.line.lineChart( // 추세선 차트 부분
                lines = listOf(
                    LineSpec(
                        lineColor = android.graphics.Color.RED,
                        lineCap = Paint.Cap.BUTT,
                        pointConnector = DefaultPointConnector(
                            cubicStrength = 0f
                        ),
                    )
                ),
                axisValuesOverrider = AxisValuesOverrider.fixed(
                    minY = 0f,
                    maxY = yValues.maxOrNull()?.toFloat()?.times(1.1f) ?: Float.MIN_VALUE
                )
            )
        ),
        chartModelProducer = monthDataProducer.plus(trendDataProducer),
        startAxis = startAxis(
            valueFormatter = { value, _ ->
                String.format("%.1f", value)
            }
        ),
        bottomAxis = bottomAxis(
            valueFormatter = { value, _ ->
                val startMonth = if (selectedOption.endsWith("상반기")) 1 else 7
                val month = (startMonth + value.toInt()) % 12
                String.format("%02d", if (month == 0) 12 else month)
            }
        ),
        runInitialAnimation = true,
        legend = rememberLegend(listOf(Color.Blue, Color.Red))
    )
}

@Composable
fun rememberLegend(colors: List<Color>) : VerticalLegend {
    val labelTextList = listOf("월별 사고 건수", "사고 추세")

    return VerticalLegend(
        items = List(labelTextList.size) { index ->
            verticalLegendItem(
                icon = shapeComponent(
                    shape = Shapes.pillShape,
                    color = colors[index],
                ),
                label = textComponent(),
                labelText = labelTextList[index]
            )
        },
        iconSizeDp = 10f,
        iconPaddingDp = 8f
    )
}

fun getMonthsFromOption(option: String): Pair<String, String> {
    val year = option.substringBefore("년").toInt()
    val half = option.substringAfter("년 ").substringBefore("반기")
    val startMonth = if (half == "상") "01" else "07"
    val endMonth = if (half == "상") "06" else "12"
    return Pair("$year-${startMonth.padStart(2, '0')}", "$year-${endMonth.padStart(2, '0')}")
}

fun generateOptions(): List<String> {
    val startDate = Calendar.getInstance()
    startDate.set(2023, Calendar.JANUARY, 1)
    val endDate = Calendar.getInstance()
    var options = mutableListOf<String>()
    val currentDate = startDate.clone() as Calendar

    while (currentDate.before(endDate) || currentDate == endDate) {
        val halfYear = if (currentDate.get(Calendar.MONTH) < Calendar.JUNE) "상반기" else "하반기"
        options.add("${currentDate.get(Calendar.YEAR)}년 $halfYear")
        if (currentDate.get(Calendar.MONTH) < Calendar.JUNE) {
            currentDate.set(currentDate.get(Calendar.YEAR), Calendar.JULY, 1)
        } else {
            currentDate.set(currentDate.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1)
        }
    }

    options[options.lastIndex] += "(현재)"
    options = options.reversed().toMutableList()

    return options
}