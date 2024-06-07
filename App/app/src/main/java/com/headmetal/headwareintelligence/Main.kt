package com.headmetal.headwareintelligence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class WeatherResponse(
    val temperature: Float, val airVelocity: Float, val precipitation: Float, val humidity: Float
)

class WeatherViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    private val _temperature = mutableStateOf<Float?>(null)
    val temperature: State<Float?> = _temperature

    private val _airVelocity = mutableStateOf<Float?>(null)
    val airVelocity: State<Float?> = _airVelocity

    private val _precipitation = mutableStateOf<Float?>(null)
    val precipitation: State<Float?> = _precipitation

    private val _humidity = mutableStateOf<Float?>(null)
    val humidity: State<Float?> = _humidity

    fun getWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getWeather(latitude, longitude)
            _temperature.value = response.temperature
            _airVelocity.value = response.airVelocity
            _precipitation.value = response.precipitation
            _humidity.value = response.humidity
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Main(
    navController: NavController,
    weatherViewModel: WeatherViewModel = remember { WeatherViewModel() }
) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val username: String = sharedAccount.getString("name", null).toString()
    val temperature by weatherViewModel.temperature
    val airVelocity by weatherViewModel.airVelocity
    val precipitation by weatherViewModel.precipitation
    val humidity by weatherViewModel.humidity
    val refreshState: MutableState<Boolean> = remember { mutableStateOf(false) }

    var current by remember { mutableStateOf(Calendar.getInstance().time) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
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
                latitude = it.latitude
                longitude = it.longitude
            }
        } else {
            Log.e("HEAD METAL", "위치 권한이 필요함")
        }

        refreshState.value = true

        while (true) {
            delay(1000)
            current = Calendar.getInstance().time
        }
    }

    LaunchedEffect(refreshState.value) {
        if (latitude != null && longitude != null) {
            weatherViewModel.getWeather(latitude!!, longitude!!)
            refreshState.value = false
        }
    }

    BackOnPressed()

    Surface(
        modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(top = 30.dp)) {
                Row {
                    Text(
                        text = "안녕하세요 ", fontSize = 30.sp
                    )
                    Text(
                        text = username, textDecoration = TextDecoration.Underline, fontSize = 30.sp
                    )
                    Text(
                        text = "님!", fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { navController.navigate("menuScreen") }) {
                        Icon(
                            imageVector = Icons.Default.Menu, contentDescription = "메뉴"
                        )
                    }
                }
                Text(
                    text = "오늘도 안전한 근무 되시길 바랍니다!", fontSize = 15.sp
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
                                    "EEEE, yyyy년 MM월 dd일", Locale.getDefault()
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
                                    "HH:mm", Locale.getDefault()
                                ).format(current),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
                }
            }
            Column {
                if (sharedAccount.getString("type", null) == "manager") {
                    Button(
                        onClick = { navController.navigate("trendScreen") },
                        modifier = Modifier.padding(),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF99CCFF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "사고 추세 확인",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
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
                                modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
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
                        onClick = { navController.navigate("nullmapScreen") },
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
                                modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
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
                } else {
                    Button(
                        onClick = { navController.navigate("helmetScreen") },
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
                                modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
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
            if (temperature != null && airVelocity != null && precipitation != null && humidity != null) {
                val weatherInfo: String
                val weatherIcon: ImageVector
                val weatherColor: Color

                if (precipitation!! > 30) {
                    weatherInfo = "호우 경보"
                    weatherIcon = Icons.Default.Water
                    weatherColor = Color(0xFF00BFFF)
                } else if (precipitation!! > 20) {
                    weatherInfo = "호우 주의보"
                    weatherIcon = Icons.Default.Water
                    weatherColor = Color(0xFF00BFFF)
                } else if (precipitation!! > 0) {
                    weatherInfo = "비"
                    weatherIcon = Icons.Default.WaterDrop
                    weatherColor = Color(0xFF00BFFF)
                } else {
                    weatherInfo = "맑음"
                    weatherIcon = Icons.Default.WbSunny
                    weatherColor = Color(0xFFFF7F00)
                }

                Column(modifier = Modifier.padding(top = 30.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column {
                            Text(
                                text = "정보",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { refreshState.value = true }) {
                            Icon(
                                imageVector = Icons.Default.Update,
                                contentDescription = "Refresh Icon",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
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
                ) {
                    Column {
                        Row {
                            Icon(
                                imageVector = weatherIcon,
                                contentDescription = null,
                                tint = weatherColor,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(40.dp)
                            )
                            Column {
                                Text(
                                    text = "기상 정보 : $weatherInfo",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(
                                        start = 10.dp, top = 10.dp, bottom = 10.dp
                                    )
                                )
                                Row {
                                    Text(
                                        text = "1시간 강수량 : " + precipitation.toString() + "mm",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "기온 : " + temperature.toString() + "ºC" + if (temperature!! > 35) {
                                            "(폭염 경보)"
                                        } else if (temperature!! > 33) {
                                            "(폭염 주의보)"
                                        } else {
                                            ""
                                        },
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "풍속 : " + airVelocity.toString() + "m/s" + if (airVelocity!! > 21) {
                                            "(강풍 경보)"
                                        } else if (airVelocity!! > 14) {
                                            "(강풍 주의보)"
                                        } else {
                                            ""
                                        },
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "습도 : " + humidity.toString() + "%",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 10.dp, bottom = 14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier
                    .padding(top = 8.dp)
                    .background(color = Color.White)
                    .border(
                        width = 1.dp, color = Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
                    .clickable { navController.navigate("countermeasuresScreen") }) {
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
                if (sharedAccount.getString("type", null) == "manager") {
                    Box(modifier = Modifier
                        .padding(top = 8.dp)
                        .background(color = Color.White)
                        .clickable { navController.navigate("processingScreen") }
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()) {
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
    }
}