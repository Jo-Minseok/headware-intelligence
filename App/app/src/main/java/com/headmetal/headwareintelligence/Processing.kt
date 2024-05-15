package com.headmetal.headwareintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class AllAccidentProcessingResponse(
    val no: List<Int>,
    val date: List<String>,
    val time: List<String>,
    val latitude: List<Double>,
    val longitude: List<Double>,
    val category: List<String>,
    val victim: List<String>,
    val situation: List<String>,
    val processingDate: List<String>,
    val processingTime: List<String>,
    val detail: List<String>
)

data class Item(
    val no: Int,
    val date: String,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val victim: String,
    val situation: String,
    val processingDate: String,
    val processingTime: String,
    val detail: String
)

class AllAccidentProcessingViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf<List<Int>>(emptyList())
    val no: State<List<Int>> = _no

    private val _date = mutableStateOf<List<String>>(emptyList())
    val date: State<List<String>> = _date

    private val _time = mutableStateOf<List<String>>(emptyList())
    val time: State<List<String>> = _time

    private val _latitude = mutableStateOf<List<Double>>(emptyList())
    val latitude: State<List<Double>> = _latitude

    private val _longitude = mutableStateOf<List<Double>>(emptyList())
    val longitude: State<List<Double>> = _longitude

    private val _category = mutableStateOf<List<String>>(emptyList())
    val category: State<List<String>> = _category

    private val _victim = mutableStateOf<List<String>>(emptyList())
    val victim: State<List<String>> = _victim

    private val _situation = mutableStateOf<List<String>>(emptyList())
    val situation: State<List<String>> = _situation

    private val _processingDate = mutableStateOf<List<String>>(emptyList())
    val processingDate: State<List<String>> = _processingDate

    private val _processingTime = mutableStateOf<List<String>>(emptyList())
    val processingTime: State<List<String>> = _processingTime

    private val _detail = mutableStateOf<List<String>>(emptyList())
    val detail: State<List<String>> = _detail

    var state: Boolean = false // 데이터 수신 상태 확인

    fun getAllAccidentProcessingData(situationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getAllAccidentProcessingData(situationCode)
            _no.value = response.no
            _date.value = response.date
            _time.value = response.time
            _latitude.value = response.latitude
            _longitude.value = response.longitude
            _category.value = response.category
            _victim.value = response.victim
            _situation.value = response.situation
            _processingDate.value = response.processingDate
            _processingTime.value = response.processingTime
            _detail.value = response.detail
            state = !state // 모든 데이터를 수신한 뒤 상태를 전환
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Processing(accidentProcessingViewModel: AllAccidentProcessingViewModel = remember { AllAccidentProcessingViewModel() }) {
    var searchText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val situationCode: MutableState<String> = remember { mutableStateOf("1") }
    val tabIndexState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val refreshState: MutableState<Boolean> = remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        LoadingScreen()
        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
            )
            Row {
                Text(
                    text = "처리 내역",
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                )
                IconButton(onClick = { refreshState.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 3.dp, top = 5.dp)
                    )
                }
            }
            Box(
                modifier = Modifier.height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    singleLine = true,
                    placeholder = { Text("사고 처리 내역 검색") },
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
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxSize()
                )
            }
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.border(width = 1.dp, color = Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            ) {
                Tab(
                    modifier = Modifier.background(color = Color.White),
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        situationCode.value = SituationCode.PROCESSING.ordinal.toString()
                        tabIndexState.value = true
                    },
                    text = { Text(text = "사고 처리", fontSize = 20.sp, color = Color.Black) }
                )
                Tab(
                    modifier = Modifier.background(color = Color.White),
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        situationCode.value = SituationCode.MALFUNCTION.ordinal.toString()
                        tabIndexState.value = true
                    },
                    text = { Text(text = "오작동 처리", fontSize = 20.sp, color = Color.Black) }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                LaunchedEffect(refreshState.value || tabIndexState.value) {
                    LoadingState.show()
                    CoroutineScope(Dispatchers.IO).async {
                        refreshState.value = false
                        tabIndexState.value = false
                        val state = accidentProcessingViewModel.state
                        accidentProcessingViewModel.getAllAccidentProcessingData(situationCode.value)
                        while (state == accidentProcessingViewModel.state) {
                            //
                        }
                    }.await()
                    LoadingState.hide()
                }

                val no by accidentProcessingViewModel.no
                val date by accidentProcessingViewModel.date
                val time by accidentProcessingViewModel.time
                val latitude by accidentProcessingViewModel.latitude
                val longitude by accidentProcessingViewModel.longitude
                val category by accidentProcessingViewModel.category
                val victim by accidentProcessingViewModel.victim
                val situation by accidentProcessingViewModel.situation
                val processingDate by accidentProcessingViewModel.processingDate
                val processingTime by accidentProcessingViewModel.processingTime
                val detail by accidentProcessingViewModel.detail

                val itemList = mutableListOf<Item>()

                for (i in no.indices) {
                    itemList.add(Item(no[i], date[i], time[i], latitude[i], longitude[i], category[i], victim[i], situation[i], processingDate[i], processingTime[i], detail[i]))
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(itemList) { item ->
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                                .background(color = Color.White)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Column {
                                Row {
                                    Text(
                                        text = "# 사건번호 ${item.no}",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                    )
                                    Text(
                                        text = "처리내역 : ${item.situation}",
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
                                        text = "사고 위치",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "위도 : ${item.latitude}\n경도 : ${item.longitude}",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 38.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(30.dp))
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
                                        text = "사고 발생자",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = item.victim,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 38.dp, bottom = 10.dp)
                                    )
                                }
                                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = "사고 내역 : ${item.category}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                )
                                Text(
                                    text = "사고 발생 일시 : ${item.date} ${item.time}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                )
                                Text(
                                    text = "사고 처리 일시 : ${item.processingDate} ${item.processingTime}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
