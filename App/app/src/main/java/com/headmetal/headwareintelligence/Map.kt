package com.headmetal.headwareintelligence

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.ClusteringKey
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class LocationResponse(
    val no: List<Int>,
    val latitude: List<Double>,
    val longitude: List<Double>,
    val processCode: List<Int>
)

data class AccidentResponse(
    val no: Int,
    val situation: String,
    val date: String,
    val time: String,
    val detail: String,
    val victim: String
)

data class AccidentUpdateRequest(
    val detail: String
)

class LocationViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf<List<Int>>(emptyList())
    val no: State<List<Int>> = _no

    private val _latitude = mutableStateOf<List<Double>>(emptyList())
    val latitude: State<List<Double>> = _latitude

    private val _longitude = mutableStateOf<List<Double>>(emptyList())
    val longitude: State<List<Double>> = _longitude

    private val _processCode = mutableStateOf<List<Int>>(emptyList())
    val processCode: State<List<Int>> = _processCode

    fun getLocationData() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getLocationData()
            _no.value = response.no
            _latitude.value = response.latitude
            _longitude.value = response.longitude
            _processCode.value = response.processCode
        }
    }

    fun emptyData(): Boolean {
        return no.value.isEmpty() || latitude.value.isEmpty() || longitude.value.isEmpty() || processCode.value.isEmpty()
    }
}

class AccidentViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf<Int?>(null)
    private val no: State<Int?> = _no

    private val _situation = mutableStateOf<String?>(null)
    private val situation: State<String?> = _situation

    private val _date = mutableStateOf<String?>(null)
    private val date: State<String?> = _date

    private val _time = mutableStateOf<String?>(null)
    private val time: State<String?> = _time

    private val _detail = mutableStateOf<String?>(null)
    val detail: State<String?> = _detail

    private val _victim = mutableStateOf<String?>(null)
    val victim: State<String?> = _victim

    var state: Boolean = true

    fun getAccidentData(no: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getAccidentData(no)
            _no.value = response.no
            _situation.value = response.situation
            _date.value = response.date
            _time.value = response.time
            _detail.value = response.detail
            _victim.value = response.victim
            state = !state
        }
    }

//    fun emptyData(): Boolean {
//        return no.value == null || situation.value == null || date.value == null || time.value == null || detail.value == null || victim.value == null
//    }
}

fun updateAccidentComplete(no: Int, detail: String) {
    val call = RetrofitInstance.apiService.updateAccidentComplete(no, AccidentUpdateRequest(detail))
    call.enqueue(object : Callback<AccidentUpdateRequest> {
        override fun onResponse(call: Call<AccidentUpdateRequest>, response: Response<AccidentUpdateRequest>) {
            if (response.isSuccessful) {
                println("사고 상황 업데이트 성공(완료)")
            } else {
                println(response.message())
                println("서버에서 오류 응답을 받음")
            }
        }

        override fun onFailure(call: Call<AccidentUpdateRequest>, t: Throwable) {
            println("네트워크 오류 또는 예외 발생: ${t.message}")
        }
    })
}

fun updateAccidentSituation(no: Int, situation: String) {
    val call = RetrofitInstance.apiService.updateAccidentSituation(no, situation)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                println("사고 상황 업데이트 성공")
            } else {
                println("서버에서 오류 응답을 받음")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("네트워크 오류 또는 예외 발생: ${t.message}")
        }
    })
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
@ExperimentalNaverMapApi
fun Map(locationViewModel: LocationViewModel = remember { LocationViewModel() }, accidentViewModel: AccidentViewModel = remember { AccidentViewModel() }) {
    val isBottomSheetVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isDetailInputVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isDetailPrintVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
    val accidentNo: MutableState<Int> = remember { mutableIntStateOf(0) }
    val victimName: MutableState<String> = remember { mutableStateOf("") }
    val selectedMarker: MutableState<Marker?> = remember { mutableStateOf(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        LoadingScreen()
        MapScreen(
            locationViewModel,
            accidentViewModel,
            isBottomSheetVisible,
            isDetailPrintVisible,
            accidentNo,
            victimName,
            selectedMarker
        )
        BottomSheetScreen(
            isBottomSheetVisible,
            isDetailInputVisible,
            accidentNo,
            victimName,
            selectedMarker
        )
    }
}

@Composable
fun LoadingScreen() {
    val isLoading = LoadingState.isLoading.collectAsState().value

    if (isLoading) {
        Dialog(
            onDismissRequest = { LoadingState.hide() },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            CircularProgressIndicator()
        }
    }
}

object LoadingState {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun show() {
        _isLoading.value = true
    }

    fun hide() {
        _isLoading.value = false
    }
}

@Composable
fun MapScreen(
    locationViewModel: LocationViewModel,
    accidentViewModel: AccidentViewModel,
    isBottomSheetVisible: MutableState<Boolean>,
    isDetailPrintVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    victimName: MutableState<String>,
    selectedMarker: MutableState<Marker?>
) {
    class ItemKey(val id: Int, private val position: LatLng) : ClusteringKey {
        override fun getPosition() = position

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val itemKey = other as ItemKey
            return id == itemKey.id
        }

        override fun hashCode() = id
    }

    val detail: MutableState<String> = remember { mutableStateOf("") }

    if (isDetailPrintVisible.value) {
        DetailPrint(onClose = { isDetailPrintVisible.value = false }, detail = detail.value)
    }

    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { _ ->
            MapView(context).apply {
                getMapAsync { map ->
                    CoroutineScope(Dispatchers.Main).launch {
                        LoadingState.show()
                        CoroutineScope(Dispatchers.IO).async {
                            locationViewModel.getLocationData()
                            while (locationViewModel.emptyData()) {
                                //
                            }
                        }.await()
                        LoadingState.hide()

                        val no by locationViewModel.no
                        val latitude by locationViewModel.latitude
                        val longitude by locationViewModel.longitude
                        val processCode by locationViewModel.processCode

                        // 초기 위치 설정(GPS 사용)
                        val initialCameraPosition = CameraUpdate.scrollTo(LatLng(35.1336437235, 129.09320833287))
                        map.moveCamera(initialCameraPosition)
                        //

                        // 클러스터 및 마커 설정
                        val builder = Clusterer.Builder<ItemKey>()

                        builder.clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
                            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                                super.updateClusterMarker(info, marker)
                                marker.icon = if (info.size < 3) {
                                    MarkerIcons.CLUSTER_LOW_DENSITY
                                } else {
                                    MarkerIcons.CLUSTER_MEDIUM_DENSITY
                                }
                            }
                        }).leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
                            override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                                super.updateLeafMarker(info, marker)
                                val key = info.key as ItemKey
                                val code = processCode[no.indexOf(key.id)]

                                when (code) {
                                    0 -> {
                                        marker.icon = MarkerIcons.GREEN
                                    }
                                    1 -> {
                                        marker.icon = MarkerIcons.YELLOW
                                    }
                                    2 -> {
                                        marker.map = null
                                    }
                                    else -> {
                                        marker.icon = MarkerIcons.RED
                                    }
                                }
                                marker.onClickListener = Overlay.OnClickListener {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        accidentNo.value = key.id

                                        LoadingState.show()
                                        CoroutineScope(Dispatchers.IO).async {
                                            val state = accidentViewModel.state
                                            accidentViewModel.getAccidentData(key.id)
                                            while (state == accidentViewModel.state) {
                                                //
                                            }
                                        }.await()
                                        LoadingState.hide()

                                        if (code == 0) {
                                            detail.value = accidentViewModel.detail.value.toString()
                                            println(key.id)
                                            println(detail.value)
                                            println(accidentViewModel.detail.value.toString())
                                            isDetailPrintVisible.value = true
                                        }
                                        else {
                                            val victim by accidentViewModel.victim
                                            victimName.value = victim.toString()
                                            selectedMarker.value = marker
                                            isBottomSheetVisible.value = true
                                        }
                                    }

                                    true
                                }
                            }
                        })
                        //

                        // 클러스터
                        val cluster = builder.build()

                        for (i in latitude.indices) {
                            cluster.add(ItemKey(no[i], LatLng(latitude[i], longitude[i])), null)
                        }

                        cluster.map = map
                        //
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen(
    isBottomSheetVisible: MutableState<Boolean>,
    isDetailInputVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    victimName: MutableState<String>,
    selectedMarker: MutableState<Marker?>
) {
    val sheetState = rememberModalBottomSheetState()

    if (isDetailInputVisible.value) {
        DetailInput(onClose = { isDetailInputVisible.value = false }, accidentNo = accidentNo)
    }

    if (isBottomSheetVisible.value) {
        ModalBottomSheet(
            modifier = Modifier.height(270.dp),
            onDismissRequest = {
                isBottomSheetVisible.value = false
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 24.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "사고 발생지 ${accidentNo.value}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(160.dp))
                    Text(
                        text = "0.0KM", // 현재 위치로부터 선택한 마커까지의 거리(차후 수정 필요)
                        color = Color(0xFFFF6600),
                        modifier = Modifier
                            .background(Color(0x26FF6600), RoundedCornerShape(4.dp))
                            .width(100.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                    Column {
                        Text(text = "작업자", color = Color.Gray)
                        Text(text = victimName.value)
                    }
                    Spacer(modifier = Modifier.width(205.dp))
                    ClickableIcon(
                        imageVector = Icons.Default.VideoCameraFront,
                        onClick = { println("영상통화 아이콘 클릭") } // 안전모의 카메라 연결(차후 수정 필요)
                    )
                    ClickableIcon(
                        imageVector = Icons.Default.Campaign,
                        onClick = { println("스피커 아이콘 클릭") } // 안전모의 스피커 출력(차후 수정 필요)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color.Gray
                )
                Spacer(Modifier.height(10.dp))
                Row {
                    Button(
                        onClick = { isDetailInputVisible.value = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2FA94E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "처리 완료",
                            color = Color.White,
                            softWrap = false,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = {
                            selectedMarker.value?.icon = MarkerIcons.YELLOW
                            updateAccidentSituation(accidentNo.value, "처리 중")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "처리 중",
                            color = Color.White,
                            softWrap = false,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { selectedMarker.value?.map = null
                            // DB 업데이트
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "오작동",
                            color = Color.White,
                            softWrap = false,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { selectedMarker.value?.icon = MarkerIcons.RED
                            // DB 업데이트
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF6600)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "119 신고",
                            color = Color.White,
                            softWrap = false,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ClickableIcon(
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = Color(0xFFFF6600),
            modifier = Modifier
                .size(40.dp)
        )
    }
}

@Composable
fun DetailInput(onClose: () -> Unit, accidentNo: MutableState<Int>) {
    val detail = remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onClose,
        content = {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "사고 처리 세부 내역 입력")
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = detail.value,
                        onValueChange = { detail.value = it },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Button(onClick = {
                            println("작성 버튼 클릭")
                            updateAccidentComplete(accidentNo.value, detail.value)
                            onClose()
                        }) {
                            Text(text = "작성")
                        }
                        Spacer(modifier = Modifier.weight(0.001f))
                        Button(onClick = onClose) {
                            Text(text = "닫기")
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    )
}

@Composable
fun DetailPrint(onClose: () -> Unit, detail: String) {
    Dialog(
        onDismissRequest = onClose,
        content = {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("사고 처리 세부 내역")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(detail)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = onClose) {
                        Text(text = "닫기")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    )
}
