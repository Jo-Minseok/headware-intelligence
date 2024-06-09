package com.headmetal.headwareintelligence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val temperature: Float?,
    val airVelocity: Float?,
    val precipitation: Float?,
    val humidity: Float?
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
    val userRank = sharedAccount.getString("type", null)
    val userName = sharedAccount.getString("name", null)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val temperature by weatherViewModel.temperature
    val airVelocity by weatherViewModel.airVelocity
    val precipitation by weatherViewModel.precipitation
    val humidity by weatherViewModel.humidity
    var refreshState by remember { mutableStateOf(false) }
    var isRefreshClickable by remember { mutableStateOf(true) }

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
                weatherViewModel.getWeather(it.latitude, it.longitude)
                refreshState = false
            }
        } else {
            Log.e("HEAD METAL", "위치 권한이 필요함")
        }
    }

    if (refreshState) {
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
                    weatherViewModel.getWeather(it.latitude, it.longitude)
                    refreshState = false
                }
            } else {
                Log.e("HEAD METAL", "위치 권한이 필요함")
            }

            Toast
                .makeText(
                    navController.context,
                    "새로고침 되었습니다.",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    BackOnPressed()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "메뉴",
                modifier = Modifier
                    .padding(start = 4.dp, top = 20.dp, bottom = 20.dp)
                    .clickable { navController.navigate("menuScreen") }
            )
            Box(modifier = Modifier
                .padding(top = 8.dp)
                .background(color = Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .fillMaxWidth()
                .clickable { navController.navigate("privacyScreen") }
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "개인정보",
                        tint = Color.Black,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp)
                            .size(40.dp)
                    )
                    Column {
                        userRank?.let { rank ->
                            Text(
                                text = if (rank == "manager") "관리자" else "근무자",
                                modifier = Modifier.padding(start = 10.dp, top = 6.dp),
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        userName?.let { name ->
                            Text(
                                text = name,
                                modifier = Modifier.padding(start = 10.dp),
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            text = "Today : " + SimpleDateFormat(
                                "yyyy년 MM월 dd일, EEEE", Locale.getDefault()
                            ).format(Calendar.getInstance().time),
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                            fontSize = 16.sp
                        )
                        Text(
                            text = "오늘도 안전한 근무 되시길 바랍니다!",
                            modifier = Modifier.padding(start = 10.dp, bottom = 6.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Column(Modifier.padding(top = 30.dp)) {
                if (userRank == "manager") {
                    Text(
                        text = "관리자 기능",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Button(
                        onClick = { navController.navigate("trendScreen") },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF99CCFF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "사고 추세 확인",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Button(
                        onClick = { navController.navigate("mapScreen") },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF6600)),
                        shape = RoundedCornerShape(8.dp)
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
                    Button(
                        onClick = { navController.navigate("nullmapScreen") },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFB266)),
                        shape = RoundedCornerShape(8.dp)
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
                } else {
                    Text(
                        text = "근로자 기능",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Button(
                        onClick = { navController.navigate("helmetScreen") },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFB266)),
                        shape = RoundedCornerShape(8.dp)
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
            temperature?.let {
                Row(modifier = Modifier.padding(top = 30.dp)) {
                    Text(
                        text = "정보",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = "새로고침",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(enabled = isRefreshClickable) {
                                refreshState = true
                                isRefreshClickable = false

                                coroutineScope.launch {
                                    delay(3000)
                                    isRefreshClickable = true
                                }
                            }
                    )
                }

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
                    Row {
                        Icon(
                            imageVector = weatherIcon,
                            contentDescription = "날씨",
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
                            Text(
                                text = "1시간 강수량 : " + precipitation.toString() + "mm",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                            )
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
                            Text(
                                text = "습도 : " + humidity.toString() + "%",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                            )
                        }
                    }
                }
                Box(modifier = Modifier
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
                    Row {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "주의 행동 요령",
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp, top = 25.dp, bottom = 25.dp)
                                .size(40.dp)
                        )
                        Text(
                            text = "주의 행동 요령",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 30.dp)
                        )
                    }
                }
                if (userRank == "manager") {
                    Box(modifier = Modifier
                        .padding(top = 8.dp)
                        .background(color = Color.White)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .clickable { navController.navigate("processingScreen") }
                    ) {
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
