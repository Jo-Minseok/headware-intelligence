package com.headmetal.headwareintelligence

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.ClusterMarkerUpdater
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.ClusteringKey
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.DefaultMarkerManager
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.Calendar

data class LocationResponse(
    val no: List<Int>,
    val latitude: List<Double>,
    val longitude: List<Double>
)

data class AccidentResponse(
    val no: Int,
    val situation: String,
    val date: String,
    val time: String,
    val detail: String
)

class LocationViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf(emptyList<Int>())
    val no: State<List<Int>> = _no

    private val _latitude = mutableStateOf(emptyList<Double>())
    val latitude: State<List<Double>> = _latitude

    private val _longitude = mutableStateOf(emptyList<Double>())
    val longitude: State<List<Double>> = _longitude

    fun getLocationData() {
        viewModelScope.launch {
            val response = apiService.getLocationData()
            _no.value = response.no
            _latitude.value = response.latitude
            _longitude.value = response.longitude
        }
    }
}

class AccidentViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableIntStateOf(0)
    val no: State<Int> = _no

    private val _situation = mutableStateOf<String>("")
    val situation: State<String> = _situation

    private val _date = mutableStateOf<String>("")
    val date: State<String> = _date

    private val _time = mutableStateOf<String>("")
    val time: State<String> = _time

    private val _detail = mutableStateOf<String>("")
    val detail: State<String> = _detail

    fun getAccidentData(no: BigInteger) {
        viewModelScope.launch {
            val response = apiService.getAccidentData(no)
            _no.intValue = response.no
            _situation.value = response.situation
            _date.value = response.date
            _time.value = response.time
            _detail.value = response.detail
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
@ExperimentalNaverMapApi
fun Map(locationViewModel: LocationViewModel = remember { LocationViewModel() }, accidentViewModel: AccidentViewModel = remember { AccidentViewModel() }) {
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetVisible by mutableStateOf(true)
    var accidentNo by mutableIntStateOf(0)

    LaunchedEffect(Unit) {
        locationViewModel.getLocationData()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        Box {
            val context = LocalContext.current

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { _ ->
                    MapView(context).apply {
                        getMapAsync { map ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val no by locationViewModel.no
                                val latitude by locationViewModel.latitude
                                val longitude by locationViewModel.longitude

                                withContext(Dispatchers.Main) {
                                    // 초기 위치 설정
                                    val initialCameraPosition = CameraUpdate.scrollTo(LatLng(35.1336437235, 129.09320833287))
                                    map.moveCamera(initialCameraPosition)
                                    //

                                    // 클러스터 및 마커 설정
                                    val builder = Clusterer.Builder<ItemKey>()
                                    val icons = arrayOf(MarkerIcons.BLUE, MarkerIcons.GREEN, MarkerIcons.RED, MarkerIcons.YELLOW)

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
                                            marker.icon = icons[key.id % icons.size]
                                            marker.onClickListener = Overlay.OnClickListener {
                                                accidentNo = key.id
                                                isBottomSheetVisible = true
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
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Button(
                    onClick = { isBottomSheetVisible = true },
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF6600)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "작업자 확인",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
        if (isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    isBottomSheetVisible = false
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
                        Text(text = "사고 발생지 $accidentNo", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(160.dp))
                        Text(
                            text = "0.0KM",
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
                        Column() {
                            Text(text = "작업자", color = Color.Gray)
                            Text(text = "홍길동")
                        }
                        Spacer(modifier = Modifier.width(165.dp))
                        Icon(
                            imageVector = Icons.Default.VideoCameraFront,
                            contentDescription = null,
                            tint = Color(0xFFFF6600), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(40.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color(0xFFFF6600), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(40.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = Color(0xFFFF6600), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(40.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row() {
                        Text(text = "정보")
                    }
                    Spacer(Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp), // 원하는 높이 설정 가능
                        color = Color.Gray // 가로줄 색상 설정 가능
                    )

                    Spacer(Modifier.height(10.dp))

                    Row() {
                        Button(
                            onClick = {},
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
                                fontSize = 11.sp,
                            )
                        }
                        Button(
                            onClick = {},
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
                                fontSize = 11.sp,
                            )
                        }
                        Button(
                            onClick = {},
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
                                fontSize = 11.sp,
                            )
                        }
                        Button(
                            onClick = {},
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
                                fontSize = 11.sp,
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

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
