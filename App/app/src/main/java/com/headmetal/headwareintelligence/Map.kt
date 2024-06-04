package com.headmetal.headwareintelligence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// Accident 테이블의 데이터를 수신하는 데이터 클래스(Response)
data class AccidentResponse(
    val no: List<Int>, // 사고 번호 리스트
    val latitude: List<Double>, // 위도 리스트
    val longitude: List<Double>, // 경도 리스트
    val situationCode: List<Int>, // 처리 상황 코드 리스트
    val workId: List<String>
)

// Accident 테이블의 뷰 모델
class AccidentViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf<List<Int>>(emptyList())
    val no: State<List<Int>> = _no

    private val _latitude = mutableStateOf<List<Double>>(emptyList())
    val latitude: State<List<Double>> = _latitude

    private val _longitude = mutableStateOf<List<Double>>(emptyList())
    val longitude: State<List<Double>> = _longitude

    private val _situationCode = mutableStateOf<List<Int>>(emptyList())
    val situationCode: State<List<Int>> = _situationCode

    private val _workId = mutableStateOf<List<String>>(emptyList())
    val workId: State<List<String>> = _workId

    var state: Boolean = false // 데이터 수신 상태 확인

    fun getAccidentData(manager: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getAccidentData(manager)
            _no.value = response.no
            _latitude.value = response.latitude
            _longitude.value = response.longitude
            _situationCode.value = response.situationCode
            _workId.value = response.workId
            state = !state // 모든 데이터를 수신한 뒤 상태를 전환
        }
    }
}

// Accident_Processing 테이블의 뷰 모델
class AccidentProcessingViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf<Int?>(null)

    private val _situation = mutableStateOf<String?>(null)

    private val _detail = mutableStateOf<String?>(null)
    val detail: State<String?> = _detail

    private val _victimId = mutableStateOf<String?>(null)
    val victimId: State<String?> = _victimId

    private val _victimName = mutableStateOf<String?>(null)
    val victimName: State<String?> = _victimName

    var state: Boolean = false // 데이터 수신 상태 확인

    fun getAccidentProcessingData(no: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getAccidentProcessingData(no)
            _no.value = response.no
            _situation.value = response.situation
            _detail.value = response.detail
            _victimId.value = response.victimId
            _victimName.value = response.victimName
            state = !state // 모든 데이터를 수신한 뒤 상태를 전환
        }
    }
}

// 클러스터에 사용할 아이템 키 클래스
class ItemKey(val id: Int, private val position: LatLng = LatLng(0.0, 0.0)) : ClusteringKey {
    override fun getPosition() = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val itemKey = other as ItemKey
        return id == itemKey.id
    }

    override fun hashCode() = id
}

@SuppressLint("UnrememberedMutableState")
@Composable
@ExperimentalNaverMapApi
fun Map(
    navController: NavController,
    accidentViewModel: AccidentViewModel = remember { AccidentViewModel() }, // Accident 테이블의 뷰 모델 의존성 주입
    accidentProcessingViewModel: AccidentProcessingViewModel = remember { AccidentProcessingViewModel() } // Accident_Processing 테이블의 뷰 모델 의존성 주입
) {
    val isBottomSheetVisible: MutableState<Boolean> =
        remember { mutableStateOf(false) } // 바텀 시트 스위치
    val accidentNo: MutableState<Int> = remember { mutableIntStateOf(0) } // 사고 번호
    val situationCode: MutableList<Int> = remember { mutableListOf() } // 처리 상황 코드 리스트
    val workId: MutableList<String> = remember { mutableListOf() }
    val listIdx: MutableState<Int> = remember { mutableIntStateOf(0) }
    val victimId: MutableState<String> = remember { mutableStateOf("") }
    val victimName: MutableState<String> = remember { mutableStateOf("") } // 사고자 이름
    val cluster: MutableState<Clusterer<ItemKey>?> = remember { mutableStateOf(null) } // 클러스터
    val selectedMarker: MutableState<Marker?> = remember { mutableStateOf(null) } // 마지막으로 선택된 마커

    Surface(modifier = Modifier.fillMaxSize()) {
        LoadingScreen()
        MapScreen(
            accidentViewModel,
            accidentProcessingViewModel,
            isBottomSheetVisible,
            accidentNo,
            situationCode,
            workId,
            listIdx,
            victimId,
            victimName,
            cluster,
            selectedMarker
        )
        BottomSheetScreen(
            isBottomSheetVisible,
            accidentNo,
            situationCode,
            workId,
            listIdx,
            victimId,
            victimName,
            cluster,
            selectedMarker
        )
    }
}

@Composable
fun MapScreen(
    accidentViewModel: AccidentViewModel,
    accidentProcessingViewModel: AccidentProcessingViewModel,
    isBottomSheetVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    situationCode: MutableList<Int>,
    workId: MutableList<String>,
    listIdx: MutableState<Int>,
    victimId: MutableState<String>,
    victimName: MutableState<String>,
    cluster: MutableState<Clusterer<ItemKey>?>,
    selectedMarker: MutableState<Marker?>
) {
    val detail: MutableState<String> = remember { mutableStateOf("") } // 사고 처리 세부 내역

    val isDetailPrintDialogVisible: MutableState<Boolean> =
        remember { mutableStateOf(false) } // 사고 처리 세부 내역 출력창 스위치
    val isEndDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) } // 종료 알림창 스위치

    if (isDetailPrintDialogVisible.value) { // 스위치가 on이 될 경우 사고 처리 세부 내역 출력창 출력
        DetailPrintDialog(
            onClose = { isDetailPrintDialogVisible.value = false }, detail = detail.value
        )
    }

    if (isEndDialogVisible.value) { // 스위치가 on이 될 경우 종료 알림창 출력
        EndDialog(
            onEnd = { android.os.Process.killProcess(android.os.Process.myPid()) },
            message = "데이터 로딩에 실패하여\n앱을 종료합니다."
        )
    }

    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var initialLatitude by remember { mutableStateOf<Double?>(null) }
    var initialLongitude by remember { mutableStateOf<Double?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasLocationPermission = true
            }

            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        if (hasLocationPermission) {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                initialLatitude = it.latitude
                initialLongitude = it.longitude
            }
        } else {
            Log.e("HEAD METAL", "위치 권한이 필요함")
        }
    }

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { _ ->
        MapView(context).apply {
            getMapAsync { map ->
                CoroutineScope(Dispatchers.Main).launch { // 코루틴 Main 진입
                    LoadingState.show() // 로딩 창 출력
                    val accidentResponseResult =
                        withTimeoutOrNull(10000) { // 10초 동안 데이터를 수신하지 못할 경우 종료
                            CoroutineScope(Dispatchers.IO).async { // 데이터를 받아오기 위해 IO 상태로 전환하여 비동기 처리
                                val state = accidentViewModel.state // 현재 상태 값을 받아옴
                                accidentViewModel.getAccidentData(
                                    sharedAccount.getString("userid", null).toString()
                                ) // Accident 테이블 데이터 수신
                                while (state == accidentViewModel.state) {
                                    // 상태 값이 전환될 때까지 반복(로딩) = 모든 데이터를 수신할 때까지 반복(로딩)
                                }
                            }.await() // 데이터를 받아올 때까지 대기
                        }
                    LoadingState.hide() // 로딩 창 숨김

                    if (accidentResponseResult == null) { // 정해진 시간 동안 데이터를 수신하지 못한 경우 종료
                        Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                        isEndDialogVisible.value = true // 종료 알림창 on
                    }

                    // 수신한 Accident 테이블 데이터를 캡쳐
                    val no by accidentViewModel.no
                    val latitude by accidentViewModel.latitude
                    val longitude by accidentViewModel.longitude
                    situationCode.addAll(accidentViewModel.situationCode.value)
                    workId.addAll(accidentViewModel.workId.value)

                    // 지도의 초기 위치 설정
                    val initialCameraPosition =
                        CameraUpdate.scrollTo(LatLng(35.1336437235, 129.09320833287))
//                    val initialCameraPosition =
//                        CameraUpdate.scrollTo(LatLng(initialLatitude!!, initialLongitude!!))
                    map.moveCamera(initialCameraPosition)

                    // 클러스터 마커 및 단말 마커 설정 후 클러스터 구성
                    cluster.value = Clusterer.Builder<ItemKey>().clusterMarkerUpdater(object :
                        DefaultClusterMarkerUpdater() { // 클러스터 마커를 불러올 때마다 실행되는 이벤트
                        override fun updateClusterMarker(
                            info: ClusterMarkerInfo, marker: Marker
                        ) {
                            super.updateClusterMarker(info, marker)
                            marker.icon = if (info.size < 3) { // 클러스터 내에 존재하는 단말 마커가 3개 미만일 경우
                                MarkerIcons.CLUSTER_LOW_DENSITY // 연한 주황색 클러스터 아이콘 출력
                            } else { // 클러스터 내에 존재하는 단말 마커가 3개 이상일 경우
                                MarkerIcons.CLUSTER_MEDIUM_DENSITY // 진한 주황색 클러스터 아이콘 출력
                            }
                        }
                    }).leafMarkerUpdater(object :
                        DefaultLeafMarkerUpdater() { // 클러스터 내의 단말 마커를 불러올 때마다 실행되는 이벤트
                        override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                            super.updateLeafMarker(info, marker)
                            val key = info.key as ItemKey // 단말 마커의 id 값(사고 번호)

                            when (situationCode[no.indexOf(key.id)]) { // 사고 번호에 해당하는 사고의 처리 상황 코드에 따라 마커 아이콘 지정 및 마커 숨김
                                SituationCode.COMPLETE.ordinal -> {
                                    marker.icon = MarkerIcons.GREEN
                                } // 처리 상황 코드가 COMPLETE일 때 초록색 마커 출력(처리 완료)
                                SituationCode.PROCESSING.ordinal -> {
                                    marker.icon = MarkerIcons.YELLOW
                                } // 처리 상황 코드가 PROCESSING일 때 노란색 마커 출력(처리 중)
                                SituationCode.MALFUNCTION.ordinal -> {
                                    marker.map = null
                                } // 처리 상황 코드가 MALFUNCTION일 때 마커를 숨김(오작동)
                                else -> {
                                    marker.icon = MarkerIcons.RED
                                } // 처리 상황 코드가 REPORT119일 때 빨간색 마커 출력(119 신고)
                            }

                            marker.onClickListener =
                                Overlay.OnClickListener { // 단말 마커 클릭 시 발생하는 이벤트
                                    CoroutineScope(Dispatchers.Main).launch { // 코루틴 Main 진입
                                        accidentNo.value =
                                            key.id // 이벤트 함수 외부에서 마지막에 선택한 마커의 사고 번호를 사용하기 위해 캡처

                                        LoadingState.show() // 로딩 창 출력
                                        val accidentProcessingResponseResult =
                                            withTimeoutOrNull(10000) { // 10초 동안 데이터를 수신하지 못할 경우 종료
                                                CoroutineScope(Dispatchers.IO).async { // 데이터를 받아오기 위해 IO 상태로 전환하여 비동기 처리
                                                    val state =
                                                        accidentProcessingViewModel.state // 현재 상태 값을 받아옴
                                                    accidentProcessingViewModel.getAccidentProcessingData(
                                                        key.id
                                                    ) // Accident Processing 테이블 데이터 수신
                                                    while (state == accidentProcessingViewModel.state) {
                                                        // 상태 값이 전환될 때까지 반복(로딩) = 모든 데이터를 수신할 때까지 반복(로딩)
                                                    }
                                                }.await() // 데이터를 받아올 때까지 대기
                                            }
                                        LoadingState.hide() // 로딩 창 숨김

                                        if (accidentProcessingResponseResult == null) { // 정해진 시간 동안 데이터를 수신하지 못한 경우 종료
                                            Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                                            isEndDialogVisible.value = true // 종료 알림창 on
                                        }

                                        listIdx.value = no.indexOf(key.id)
                                        selectedMarker.value =
                                            marker // 이벤트 함수 외부에서 마지막에 선택한 마커의 속성을 변경하기 위해 캡처

                                        if (situationCode[listIdx.value] == SituationCode.COMPLETE.ordinal) { // 클릭한 단말 마커의 처리 상황 코드가 COMPLETE일 경우
                                            detail.value =
                                                accidentProcessingViewModel.detail.value.toString() // 사고 처리 세부 내역 업데이트
                                            isDetailPrintDialogVisible.value =
                                                true // 사고 처리 세부 내역 출력창 on
                                        } else { // 클릭한 단말 마커의 처리 상황 코드가 PROCESSING, MALFUNCTION, REPORT119일 경우
                                            victimId.value =
                                                accidentProcessingViewModel.victimId.value.toString()
                                            victimName.value =
                                                accidentProcessingViewModel.victimName.value.toString() // 사고자 이름 업데이트
                                            isBottomSheetVisible.value = true // 바텀 시트 on
                                        }
                                    }
                                    true
                                }
                        }
                    }).build() // 클러스터 마커 및 단말 마커를 설정하기 위한 빌더 구성 후 빌드

                    for (i in no.indices) { // 수신한 사고 건수만큼 반복
                        if (situationCode[i] != SituationCode.MALFUNCTION.ordinal) { // 처리 상황 코드가 MALFUNCTION인 경우 지도에 나타내지 않고 클러스터로 구성하지 않음
                            cluster.value!!.add(
                                ItemKey(
                                    no[i], LatLng(latitude[i], longitude[i])
                                ), null
                            ) // 사고 번호, 위도, 경도 값을 사용하여 클러스터를 구성
                        }
                    }

                    cluster.value!!.map = map // 구성한 클러스터를 지도에 표현
                }
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen(
    isBottomSheetVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    situationCode: MutableList<Int>,
    workId: MutableList<String>,
    listIdx: MutableState<Int>,
    victimId: MutableState<String>,
    victimName: MutableState<String>,
    cluster: MutableState<Clusterer<ItemKey>?>,
    selectedMarker: MutableState<Marker?>
) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val client = remember { OkHttpClient() }
    val scope = rememberCoroutineScope()
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val webSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("HEAD METAL", text)

            val messages = text.split(":")
            val manager = sharedAccount.getString("userid", null).toString()

            if (messages[1] == manager && messages[2] == "카메라완료") {
                imageUrl =
                    "http://minseok821lab.kro.kr:8000/accident/get_image/${victimId.value}/${manager}"
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            Log.e("HEAD METAL", "Error: ${t.message}")
        }
    }

    imageUrl?.let { url ->
        Box(modifier = Modifier.fillMaxSize()) {
            val painter =
                rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                    .data(url).build(),
                    onSuccess = { LoadingState.hide() })
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Button(
                onClick = { imageUrl = null },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }

    val isDetailInputDialogVisible: MutableState<Boolean> =
        remember { mutableStateOf(false) } // 사고 처리 세부 내역 입력창 스위치

    if (isDetailInputDialogVisible.value) { // 스위치가 on이 될 경우 사고 처리 세부 내역 입력창 출력
        DetailInputDialog(
            onClose = { isDetailInputDialogVisible.value = false },
            isBottomSheetVisible = isBottomSheetVisible,
            accidentNo = accidentNo,
            selectedMarker = selectedMarker,
            situationCode = situationCode,
            listIdx = listIdx
        )
    }

    if (isBottomSheetVisible.value) { // 스위치가 on이 될 경우 바텀 시트 출력
        val request = Request.Builder().url(
            "ws://minseok821lab.kro.kr:8000/accident/ws/${workId[listIdx.value]}/${
                sharedAccount.getString(
                    "userid", null
                ).toString()
            }"
        ).build()
        val webSocket = client.newWebSocket(request, webSocketListener)

        ModalBottomSheet(modifier = Modifier.height(270.dp),
            onDismissRequest = { isBottomSheetVisible.value = false },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }) {
            Column(
                modifier = Modifier.padding(
                    top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "사고 발생지 ${accidentNo.value}",
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "작업자", color = Color.Gray)
                        Text(victimName.value)
                    }
                    IconButton(onClick = {
                        Log.i(
                            "IconClick", "카메라 아이콘 클릭"
                        )
                        scope.launch(Dispatchers.IO) {
                            webSocket.send("${victimId.value}:카메라")
                            isBottomSheetVisible.value = false
                            LoadingState.show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.VideoCameraFront,
                            contentDescription = null,
                            tint = Color(0xFFFF6600),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = {
                        Log.i(
                            "IconClick", "스피커 아이콘 클릭"
                        )
                        scope.launch(Dispatchers.IO) {
                            webSocket.send("${victimId.value}:소리")
                            isBottomSheetVisible.value = false
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color(0xFFFF6600),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp), color = Color.Gray
                )
                Spacer(Modifier.height(10.dp))
                Row {
                    Button( // 바텀 시트의 '처리 완료' 버튼
                        onClick = {
                            Log.i("ButtonClick", "처리 완료 버튼 클릭")
                            isDetailInputDialogVisible.value = true // 사고 처리 세부 내역 입력창 on
                        },
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
                            fontSize = 9.sp
                        )
                    }
                    Button( // 바텀 시트의 '처리 중' 버튼
                        onClick = {
                            Log.i("ButtonClick", "처리 중 버튼 클릭")
                            updateAccidentSituation(
                                accidentNo.value, SituationCode.PROCESSING.ordinal.toString(), null
                            ) // 처리 상황을 '처리 중'으로 갱신(DB 반영)
                            situationCode[listIdx.value] =
                                SituationCode.PROCESSING.ordinal // 마지막으로 선택한 마커의 처리 상황 코드 리스트 값을 PROCESSING으로 갱신
                            selectedMarker.value?.icon = MarkerIcons.YELLOW // 단말 마커 아이콘을 노란색 마커로 갱신
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
                            fontSize = 9.sp
                        )
                    }
                    Button( // 바텀 시트의 '오작동' 버튼
                        onClick = {
                            Log.i("ButtonClick", "오작동 버튼 클릭")
                            updateAccidentSituation(
                                accidentNo.value, SituationCode.MALFUNCTION.ordinal.toString(), null
                            ) // 처리 상황을 '오작동'으로 갱신(DB 반영)
                            situationCode[listIdx.value] =
                                SituationCode.MALFUNCTION.ordinal // 마지막으로 선택한 마커의 처리 상황 코드 리스트 값을 MALFUNCTION으로 갱신
                            selectedMarker.value?.map = null // 지도에서 단말 마커를 삭제
                            cluster.value!!.remove(ItemKey(accidentNo.value)) // 삭제된 마커를 클러스터에서 제외
                            isBottomSheetVisible.value = false // 바텀 시트 off
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
                            fontSize = 9.sp
                        )
                    }
                    Button( // 바텀 시트의 '119 신고' 버튼
                        onClick = {
                            Log.i("ButtonClick", "119 신고 버튼 클릭")
                            updateAccidentSituation(
                                accidentNo.value, SituationCode.REPORT119.ordinal.toString(), null
                            ) // 처리 상황을 '119 신고'로 갱신(DB 반영)
                            situationCode[listIdx.value] =
                                SituationCode.REPORT119.ordinal // 마지막으로 선택한 마커의 처리 상황 코드 리스트 값을 REPORT119로 갱신
                            selectedMarker.value?.icon = MarkerIcons.RED // 단말 마커 아이콘을 빨간색 마커로 갱신
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
                            fontSize = 9.sp
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// 사고 처리 세부 내역 입력창 Composable
@Composable
fun DetailInputDialog(
    onClose: () -> Unit,
    isBottomSheetVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    selectedMarker: MutableState<Marker?>,
    situationCode: MutableList<Int>,
    listIdx: MutableState<Int>
) {
    val detail = remember { mutableStateOf("") } // 사고 처리 세부 내역(입력)

    val isAlertDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) } // 알림창 스위치

    if (isAlertDialogVisible.value) { // 스위치가 on이 될 경우 알림창 출력
        AlertDialog(onClose = { isAlertDialogVisible.value = false })
    }

    Dialog(onDismissRequest = onClose, content = {
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
                TextField(value = detail.value, onValueChange = { detail.value = it })
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(44.dp)) {
                    Button( // 사고 처리 세부 내역 입력창의 '처리' 버튼
                        onClick = {
                            Log.i("ButtonClick", "처리 버튼 클릭")
                            if (detail.value == "") {
                                isAlertDialogVisible.value = true
                            } else {
                                updateAccidentSituation(
                                    accidentNo.value,
                                    SituationCode.COMPLETE.ordinal.toString(),
                                    detail.value
                                ) // 처리 상황을 '처리 완료'로 갱신(DB 반영)
                                situationCode[listIdx.value] =
                                    SituationCode.COMPLETE.ordinal // 마지막으로 선택한 마커의 처리 상황 코드 리스트 값을 COMPLETE로 갱신
                                selectedMarker.value?.icon =
                                    MarkerIcons.GREEN // 단말 마커 아이콘을 초록색 마커로 갱신
                                isBottomSheetVisible.value = false // 바텀 시트 off
                                onClose() // 사고 처리 세부 내역 입력창 off
                            }
                        }, colors = ButtonDefaults.buttonColors(Color(0xFF2FA94E))
                    ) { Text(text = "처리") }
                    Button(
                        onClick = onClose, colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) { Text(text = "닫기") }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    })
}

// 사고 처리 세부 내역 출력창 Composable
@Composable
fun DetailPrintDialog(onClose: () -> Unit, detail: String) {
    Dialog(onDismissRequest = onClose, content = {
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
                Text(text = "사고 처리 세부 내역")
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = detail)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onClose, colors = ButtonDefaults.buttonColors(Color.Gray)
                ) { Text(text = "닫기") }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    })
}
