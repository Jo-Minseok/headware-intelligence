package com.headmetal.headwareintelligence

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// Accident 테이블의 데이터를 수신하는 데이터 클래스(Response)
data class NullAccidentResponse(
    val no: List<Int>, // 사고 번호 리스트
    val latitude: List<Double>, // 위도 리스트
    val longitude: List<Double>, // 경도 리스트
    val workId: List<String>
)

// Accident 테이블의 뷰 모델
class NullAccidentViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _no = mutableStateOf<List<Int>>(emptyList())
    val no: State<List<Int>> = _no

    private val _latitude = mutableStateOf<List<Double>>(emptyList())
    val latitude: State<List<Double>> = _latitude

    private val _longitude = mutableStateOf<List<Double>>(emptyList())
    val longitude: State<List<Double>> = _longitude

    private val _workId = mutableStateOf<List<String>>(emptyList())
    val workId: State<List<String>> = _workId

    var state: Boolean = false // 데이터 수신 상태 확인

    fun getNullAccidentData(manager: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getNullAccidentData(manager)
            _no.value = response.no
            _latitude.value = response.latitude
            _longitude.value = response.longitude
            _workId.value = response.workId
            state = !state // 모든 데이터를 수신한 뒤 상태를 전환
        }
    }
}

// Accident_Processing 테이블의 뷰 모델
class NullAccidentProcessingViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _victimId = mutableStateOf<String?>(null)
    val victimId: State<String?> = _victimId

    private val _victimName = mutableStateOf<String?>(null)
    val victimName: State<String?> = _victimName

    var state: Boolean = false // 데이터 수신 상태 확인

    fun getAccidentProcessingData(no: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getAccidentProcessingData(no)
            _victimId.value = response.victimId
            _victimName.value = response.victimName
            state = !state // 모든 데이터를 수신한 뒤 상태를 전환
        }
    }
}

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
@ExperimentalNaverMapApi
fun NullMap(
    navController: NavController,
    nullAccidentViewModel: NullAccidentViewModel = remember { NullAccidentViewModel() }, // Accident 테이블의 뷰 모델 의존성 주입
    nullAccidentProcessingViewModel: NullAccidentProcessingViewModel = remember { NullAccidentProcessingViewModel() } // Accident_Processing 테이블의 뷰 모델 의존성 주입
) {
    val isBottomSheetVisible: MutableState<Boolean> =
        remember { mutableStateOf(false) } // 바텀 시트 스위치
    val accidentNo: MutableState<Int> = remember { mutableIntStateOf(0) } // 사고 번호
    val workId: MutableList<String> = remember { mutableListOf() }
    val victimId: MutableState<String> = remember { mutableStateOf("") }
    val victimName: MutableState<String> = remember { mutableStateOf("") } // 사고자 이름
    val accidentNoList: MutableList<Int> = remember { mutableListOf() }
    val markerList: MutableList<Marker> = remember { mutableListOf() }
    val markerListIdx: MutableState<Int> = remember { mutableIntStateOf(0) }
    val selectedMarker: MutableState<Marker?> = remember { mutableStateOf(null) } // 마지막으로 선택된 마커

    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val imageUrl: MutableState<String?> = remember { mutableStateOf(null) }
    val webSocketSendData: MutableState<String?> = remember { mutableStateOf(null) }
    val imageDataReception: MutableState<Boolean> = remember { mutableStateOf(false) }
    val soundDataReception: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isWebSocketDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
    val webSocketMessage: MutableState<String> = remember { mutableStateOf("") }

    val client = remember { OkHttpClient() }
    val webSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("HEAD METAL", text)

            val messages = text.split(":")
            val manager = sharedAccount.getString("userid", null).toString()

            if (webSocketSendData.value == "카메라" && messages[1] == manager && messages[2] == "카메라완료") {
                imageUrl.value =
                    "http://minseok821lab.kro.kr:8000/accident/get_image/${victimId.value}/${manager}"
                imageDataReception.value = true
            } else if (webSocketSendData.value == "소리" && messages[1] == manager && messages[2] == "소리완료") {
                LoadingState.hide()
                webSocketMessage.value = "소리 출력이 완료되었습니다."
                isWebSocketDialogVisible.value = true
                soundDataReception.value = true
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "피해자 행동 요청 실패",
                message = "네트워크 문제로 인해 피해자에 대한 행동 요청이 되지 않았습니다.",
            )
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        LoadingScreen()
        NullMapScreen(
            nullAccidentViewModel,
            nullAccidentProcessingViewModel,
            isBottomSheetVisible,
            accidentNo,
            workId,
            victimId,
            victimName,
            accidentNoList,
            markerList,
            markerListIdx,
            selectedMarker,
            navController,
            sharedAccount,
            imageUrl
        )
        NullBottomSheetScreen(
            isBottomSheetVisible,
            isWebSocketDialogVisible,
            webSocketMessage,
            accidentNo,
            workId,
            victimId,
            victimName,
            accidentNoList,
            markerList,
            markerListIdx,
            selectedMarker,
            navController,
            sharedAccount,
            client,
            webSocketListener,
            imageUrl,
            webSocketSendData,
            imageDataReception,
            soundDataReception
        )
    }
}

@Composable
fun NullMapScreen(
    nullAccidentViewModel: NullAccidentViewModel,
    nullAccidentProcessingViewModel: NullAccidentProcessingViewModel,
    isBottomSheetVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    workId: MutableList<String>,
    victimId: MutableState<String>,
    victimName: MutableState<String>,
    accidentNoList: MutableList<Int>,
    markerList: MutableList<Marker>,
    markerListIdx: MutableState<Int>,
    selectedMarker: MutableState<Marker?>,
    navController: NavController,
    sharedAccount: SharedPreferences,
    imageUrl: MutableState<String?>
) {
    val isEndDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) } // 종료 알림창 스위치
    val isNoDataDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) }

    if (isEndDialogVisible.value) { // 스위치가 on이 될 경우 종료 알림창 출력
        AlertDialog(
            onClose = { android.os.Process.killProcess(android.os.Process.myPid()) },
            content = "데이터 로딩에 실패하여\n앱을 종료합니다."
        )
    }

    if (isNoDataDialogVisible.value) {
        AlertDialog(
            onClose = { navController.popBackStack() },
            content = "미처리된 사고 발생지가 존재하지\n않아 뒤로 돌아갑니다."
        )
    }

    val context = LocalContext.current

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { _ ->
        MapView(context).apply {
            getMapAsync { map ->
                CoroutineScope(Dispatchers.Main).launch { // 코루틴 Main 진입
                    LoadingState.show() // 로딩 창 출력
                    val nullAccidentResponseResult =
                        withTimeoutOrNull(10000) { // 10초 동안 데이터를 수신하지 못할 경우 종료
                            CoroutineScope(Dispatchers.IO).async { // 데이터를 받아오기 위해 IO 상태로 전환하여 비동기 처리
                                val state = nullAccidentViewModel.state // 현재 상태 값을 받아옴
                                nullAccidentViewModel.getNullAccidentData(
                                    sharedAccount.getString(
                                        "userid", null
                                    ).toString()
                                ) // Accident 테이블 데이터 수신
                                while (state == nullAccidentViewModel.state) {
                                    // 상태 값이 전환될 때까지 반복(로딩) = 모든 데이터를 수신할 때까지 반복(로딩)
                                }
                            }.await() // 데이터를 받아올 때까지 대기
                        }
                    LoadingState.hide() // 로딩 창 숨김

                    if (nullAccidentResponseResult == null) { // 정해진 시간 동안 데이터를 수신하지 못한 경우 종료
                        Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                        isEndDialogVisible.value = true // 종료 알림창 on
                    } else {
                        // 수신한 Accident 테이블 데이터를 캡쳐
                        accidentNoList.addAll(nullAccidentViewModel.no.value)
                        val latitude by nullAccidentViewModel.latitude
                        val longitude by nullAccidentViewModel.longitude
                        workId.addAll(nullAccidentViewModel.workId.value)

                        if (accidentNoList.isEmpty()) {
                            Log.e("HEAD METAL", "미처리된 사고 데이터가 존재하지 않음")
                            isNoDataDialogVisible.value = true
                        } else {
                            for (i in accidentNoList.indices) { // 수신한 사고 건수만큼 반복
                                val marker = Marker()
                                marker.tag = accidentNoList[i]
                                marker.setOnClickListener {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        imageUrl.value = null
                                        accidentNo.value = marker.tag as Int

                                        LoadingState.show() // 로딩 창 출력
                                        val accidentProcessingResponseResult =
                                            withTimeoutOrNull(10000) { // 10초 동안 데이터를 수신하지 못할 경우 종료
                                                CoroutineScope(Dispatchers.IO).async { // 데이터를 받아오기 위해 IO 상태로 전환하여 비동기 처리
                                                    val state =
                                                        nullAccidentProcessingViewModel.state // 현재 상태 값을 받아옴
                                                    nullAccidentProcessingViewModel.getAccidentProcessingData(
                                                        accidentNo.value
                                                    ) // Accident Processing 테이블 데이터 수신
                                                    while (state == nullAccidentProcessingViewModel.state) {
                                                        // 상태 값이 전환될 때까지 반복(로딩) = 모든 데이터를 수신할 때까지 반복(로딩)
                                                    }
                                                }.await() // 데이터를 받아올 때까지 대기
                                            }
                                        LoadingState.hide() // 로딩 창 숨김

                                        if (accidentProcessingResponseResult == null) { // 정해진 시간 동안 데이터를 수신하지 못한 경우 종료
                                            Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                                            isEndDialogVisible.value = true // 종료 알림창 on
                                        }

                                        markerListIdx.value =
                                            accidentNoList.indexOf(accidentNo.value)
                                        victimId.value =
                                            nullAccidentProcessingViewModel.victimId.value.toString()
                                        victimName.value =
                                            nullAccidentProcessingViewModel.victimName.value.toString() // 사고자 이름 업데이트
                                        selectedMarker.value =
                                            marker // 이벤트 함수 외부에서 마지막에 선택한 마커의 속성을 변경하기 위해 캡처
                                        map.moveCamera(
                                            CameraUpdate.scrollTo(
                                                LatLng(
                                                    marker.position.latitude,
                                                    marker.position.longitude
                                                )
                                            )
                                        )
                                        isBottomSheetVisible.value = true // 바텀 시트 on
                                    }
                                    true
                                }
                                marker.position = LatLng(latitude[i], longitude[i])
                                marker.icon = MarkerIcons.GRAY
                                markerList.add(marker)
                                marker.map = map
                            }
                            markerList[markerListIdx.value].performClick()
                        }
                    }
                }
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NullBottomSheetScreen(
    isBottomSheetVisible: MutableState<Boolean>,
    isWebSocketDialogVisible: MutableState<Boolean>,
    webSocketMessage: MutableState<String>,
    accidentNo: MutableState<Int>,
    workId: MutableList<String>,
    victimId: MutableState<String>,
    victimName: MutableState<String>,
    accidentNoList: MutableList<Int>,
    markerList: MutableList<Marker>,
    markerListIdx: MutableState<Int>,
    selectedMarker: MutableState<Marker?>,
    navController: NavController,
    sharedAccount: SharedPreferences,
    client: OkHttpClient,
    webSocketListener: WebSocketListener,
    imageUrl: MutableState<String?>,
    webSocketSendData: MutableState<String?>,
    imageDataReception: MutableState<Boolean>,
    soundDataReception: MutableState<Boolean>,
) {
    val isDetailInputDialogVisible: MutableState<Boolean> =
        remember { mutableStateOf(false) } // 사고 처리 세부 내역 입력창 스위치
    val isNoDataDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) }

    if (isDetailInputDialogVisible.value) { // 스위치가 on이 될 경우 사고 처리 세부 내역 입력창 출력
        NullDetailInputDialog(
            onClose = { isDetailInputDialogVisible.value = false },
            isBottomSheetVisible = isBottomSheetVisible,
            isNoDataDialogVisible = isNoDataDialogVisible,
            accidentNo = accidentNo,
            workId = workId,
            accidentNoList = accidentNoList,
            markerList = markerList,
            markerListIdx = markerListIdx,
            selectedMarker = selectedMarker,
            navController = navController
        )
    }

    if (isNoDataDialogVisible.value) {
        AlertDialog(
            onClose = { navController.popBackStack() },
            content = "모든 사고를 처리하여\n뒤로 돌아갑니다."
        )
    }

    if (isWebSocketDialogVisible.value) {
        AlertDialog(
            onClose = { isWebSocketDialogVisible.value = false },
            content = webSocketMessage.value
        )
    }

    imageUrl.value?.let { url ->
        Box(contentAlignment = Alignment.Center) {
            val painter =
                rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                    .data(url).diskCachePolicy(CachePolicy.DISABLED) // 디스크 캐시 비활성화
                    .memoryCachePolicy(CachePolicy.DISABLED).build(),
                    onSuccess = { LoadingState.hide() })
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(0.6f)
            )
            Button(
                onClick = { imageUrl.value = null },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .zIndex(1f),
                colors = ButtonDefaults.buttonColors(Color(0xFFFFA500))
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }

    val scope = rememberCoroutineScope()

    if (isBottomSheetVisible.value) { // 스위치가 on이 될 경우 바텀 시트 출력
        ModalBottomSheet(modifier = Modifier.height(336.dp),
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        if (markerListIdx.value > 0) {
                            markerListIdx.value--
                            markerList[markerListIdx.value].performClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
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
                                    isBottomSheetVisible.value = false
                                    LoadingState.show()
                                    val request = Request.Builder().url(
                                        "ws://minseok821lab.kro.kr:8000/accident/ws/${workId[markerListIdx.value]}/${
                                            sharedAccount.getString(
                                                "userid", null
                                            ).toString()
                                        }"
                                    ).build()
                                    val webSocket = client.newWebSocket(request, webSocketListener)
                                    val timeOut = withTimeoutOrNull(10000) {
                                        CoroutineScope(Dispatchers.IO).async {
                                            webSocketSendData.value = "카메라"
                                            webSocket.send("${victimId.value}:${webSocketSendData.value}")
                                            while (!imageDataReception.value) {
                                                //
                                            }
                                        }.await()
                                    }
                                    LoadingState.hide()

                                    if (timeOut == null) {
                                        Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                                        webSocketMessage.value = "서버에서 데이터를 불러오지 못하였습니다."
                                        isWebSocketDialogVisible.value = true
                                    } else {
                                        imageDataReception.value = false
                                    }

                                    webSocket.close(1000, "WebSocket Close")
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
                                    isBottomSheetVisible.value = false
                                    LoadingState.show()
                                    val request = Request.Builder().url(
                                        "ws://minseok821lab.kro.kr:8000/accident/ws/${workId[markerListIdx.value]}/${
                                            sharedAccount.getString(
                                                "userid", null
                                            ).toString()
                                        }"
                                    ).build()
                                    val webSocket = client.newWebSocket(request, webSocketListener)
                                    val timeOut = withTimeoutOrNull(10000) {
                                        CoroutineScope(Dispatchers.IO).async {
                                            webSocketSendData.value = "소리"
                                            webSocket.send("${victimId.value}:${webSocketSendData.value}")
                                            while (!soundDataReception.value) {
                                                //
                                            }
                                        }.await()
                                    }
                                    LoadingState.hide()

                                    if (timeOut == null) {
                                        Log.e("HEAD METAL", "서버에서 데이터를 불러오지 못함")
                                        webSocketMessage.value = "서버에서 데이터를 불러오지 못하였습니다."
                                        isWebSocketDialogVisible.value = true
                                    } else {
                                        soundDataReception.value = false
                                    }

                                    webSocket.close(1000, "WebSocket Close")
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
                    }
                    IconButton(onClick = {
                        if (markerListIdx.value < markerList.lastIndex) {
                            markerListIdx.value++
                            markerList[markerListIdx.value].performClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp), color = Color.Gray
                )
                Column(Modifier.padding(vertical = 10.dp)) {
                    Row {
                        Button( // 바텀 시트의 '처리 완료' 버튼
                            onClick = {
                                Log.i("ButtonClick", "처리 완료 버튼 클릭")
                                isDetailInputDialogVisible.value = true // 사고 처리 세부 내역 입력창 on
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF2FA94E)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "처리 완료",
                                color = Color.White,
                                softWrap = false,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                        Button( // 바텀 시트의 '처리 중' 버튼
                            onClick = {
                                Log.i("ButtonClick", "처리 중 버튼 클릭")
                                updateAccidentSituation(
                                    accidentNo.value,
                                    SituationCode.PROCESSING.ordinal.toString(),
                                    null,
                                    navController = navController
                                ) // 처리 상황을 '처리 중'으로 갱신(DB 반영)
                                selectedMarker.value?.map = null // 지도에서 단말 마커를 삭제
                                isBottomSheetVisible.value = false // 바텀 시트 off
                                isNoDataDialogVisible.value =
                                    markerDelete(workId, accidentNoList, markerList, markerListIdx)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "처리 중",
                                color = Color.White,
                                softWrap = false,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Row {
                        Button( // 바텀 시트의 '오작동' 버튼
                            onClick = {
                                Log.i("ButtonClick", "오작동 버튼 클릭")
                                updateAccidentSituation(
                                    accidentNo.value,
                                    SituationCode.MALFUNCTION.ordinal.toString(),
                                    null,
                                    navController = navController
                                ) // 처리 상황을 '오작동'으로 갱신(DB 반영)
                                selectedMarker.value?.map = null // 지도에서 단말 마커를 삭제
                                isBottomSheetVisible.value = false // 바텀 시트 off
                                isNoDataDialogVisible.value =
                                    markerDelete(workId, accidentNoList, markerList, markerListIdx)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(Color.Gray),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "오작동",
                                color = Color.White,
                                softWrap = false,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                        Button( // 바텀 시트의 '119 신고' 버튼
                            onClick = {
                                Log.i("ButtonClick", "119 신고 버튼 클릭")
                                updateAccidentSituation(
                                    accidentNo.value,
                                    SituationCode.REPORT119.ordinal.toString(),
                                    null,
                                    navController = navController
                                ) // 처리 상황을 '119 신고'로 갱신(DB 반영)
                                selectedMarker.value?.map = null // 지도에서 단말 마커를 삭제
                                isBottomSheetVisible.value = false // 바텀 시트 off
                                isNoDataDialogVisible.value =
                                    markerDelete(workId, accidentNoList, markerList, markerListIdx)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFFF6600)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "119 신고",
                                color = Color.White,
                                softWrap = false,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

fun markerDelete(
    workId: MutableList<String>,
    accidentNoList: MutableList<Int>,
    markerList: MutableList<Marker>,
    markerListIdx: MutableState<Int>
): Boolean {
    workId.removeAt(markerListIdx.value)
    accidentNoList.removeAt(markerListIdx.value)
    markerList.removeAt(markerListIdx.value)

    if (markerList.isEmpty()) {
        return true
    }

    if (markerListIdx.value != 0) {
        markerListIdx.value--
    }

    markerList[markerListIdx.value].performClick()
    return false
}

// 사고 처리 세부 내역 입력창 Composable
@Composable
fun NullDetailInputDialog(
    onClose: () -> Unit,
    isBottomSheetVisible: MutableState<Boolean>,
    isNoDataDialogVisible: MutableState<Boolean>,
    accidentNo: MutableState<Int>,
    workId: MutableList<String>,
    accidentNoList: MutableList<Int>,
    markerList: MutableList<Marker>,
    markerListIdx: MutableState<Int>,
    selectedMarker: MutableState<Marker?>,
    navController: NavController
) {
    val detail = remember { mutableStateOf("") } // 사고 처리 세부 내역(입력)

    val isAlertDialogVisible: MutableState<Boolean> = remember { mutableStateOf(false) } // 알림창 스위치

    if (isAlertDialogVisible.value) { // 스위치가 on이 될 경우 알림창 출력
        AlertDialog(
            onClose = { isAlertDialogVisible.value = false },
            content = "사고 처리 세부 내역은 최소\n한 글자 이상 입력해야 합니다."
        )
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
                TextField(
                    value = detail.value,
                    onValueChange = { detail.value = it },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = Color(0xD0D9F7BE)
                    )
                )
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
                                    detail.value,
                                    navController = navController
                                ) // 처리 상황을 '처리 완료'로 갱신(DB 반영)
                                selectedMarker.value?.map = null // 지도에서 단말 마커를 삭제
                                isBottomSheetVisible.value = false // 바텀 시트 off
                                isNoDataDialogVisible.value =
                                    markerDelete(workId, accidentNoList, markerList, markerListIdx)
                                onClose() // 사고 처리 세부 내역 입력창 off
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xD0D9F7BE)),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) { Text(text = "처리") }
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) { Text(text = "닫기") }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    })
}
