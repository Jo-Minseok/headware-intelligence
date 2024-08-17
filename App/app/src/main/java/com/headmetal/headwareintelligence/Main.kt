package com.headmetal.headwareintelligence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class WeatherResponse(
    val temperature: Float,
    val airVelocity: Float,
    val precipitation: Float,
    val humidity: Float
)

// 프리뷰
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    Main(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun WelcomeUserComposablePreview() {
    WelcomeUserComposable(userName = "사용자")
}

@Preview(showBackground = true)
@Composable
fun MainFieldLabelPreview() {
    MainFieldLabel(text = "관리자 기능")
}

@Preview(showBackground = true)
@Composable
fun MainFunctionButtonMenuPreview() {
    MainFunctionButtonMenu(type = "manager", navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun MainContentsHeaderPreview() {
    // Initialize state values with `remember` inside the body of the composable function
    val temperature = remember { mutableFloatStateOf(0.0f) }
    val airVelocity = remember { mutableFloatStateOf(0.0f) }
    val precipitation = remember { mutableFloatStateOf(0.0f) }
    val humidity = remember { mutableFloatStateOf(0.0f) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    // Pass the state values as parameters to MainContentsHeader
    MainContentsHeader(
        temperature = temperature,
        airVelocity = airVelocity,
        precipitation = precipitation,
        humidity = humidity,
        context = context,
        fusedLocationClient = fusedLocationClient,
        locationPermissionRequest = locationPermissionRequest,
        navController = rememberNavController()
    )
}

@Preview(showBackground = true)
@Composable
fun MainContentsPreview() {
    MainContents(type = "manager", navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun MainContentsBoxPreview() {
    ContentsBox(
        imageVector = Icons.Default.Report,
        iconColor = Color(0xFFFFCC00),
        contentsTexts = arrayOf({ MainContentsBoxText(text = "주의 행동 요령") })
    )
}


@Composable
fun Main(navController: NavController) {
    BackOnPressed()
    IconScreen(
        imageVector = Icons.Default.Menu,
        onClick = { navController.navigate("MenuScreen") },
        content = {
            val sharedAccount: SharedPreferences =
                LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
            val type: String = sharedAccount.getString("type", "") ?: ""
            val userName: String = sharedAccount.getString("name", "") ?: ""

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                WelcomeUserComposable(userName = userName)
                MainFunctionButtonMenu(type = type, navController = navController)
                MainContents(type = type, navController = navController)
            }
        }
    )
}

@Composable
fun WelcomeUserComposable(userName: String) {
    Column {
        Text(text = "반갑습니다,", fontSize = 16.sp)
        Row {
            Text(text = userName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "님", fontSize = 24.sp)
        }
    }
}

/**
 * MainFunction
 */
@Composable
fun MainFieldLabel(text: String) {
    BoldTextField(
        text = text,
        fontSize = 18.sp
    )
}

@Composable
fun MainFunctionButtonMenu(type: String, navController: NavController) {
    Column {
        if (type == "manager") {
            MainFieldLabel(text = "관리자 기능")
            RoundedButton(
                buttonText = "사고 추세 확인",
                colors = Color(0xFF99CCFF),
                onClick = { navController.navigate("TrendScreen") }
            )
            RoundedButton(
                buttonText = "사고 발생지 확인",
                colors = Color(0xFFFF6600),
                onClick = { navController.navigate("MapScreen") }
            )
            RoundedButton(
                buttonText = "미처리 사고 발생지 확인",
                colors = Color(0xFFFF8000),
                onClick = { navController.navigate("NullMapScreen") }
            )
            RoundedButton(
                buttonText = "작업장 관리",
                colors = Color(0xFFFF8000),
                onClick = { navController.navigate("WorkListScreen") }
            )
        } else {
            MainFieldLabel(text = "근로자 기능")
            RoundedButton(
                buttonText = "안전모 등록",
                colors = Color(0xFFFFB266),
                onClick = { navController.navigate("HelmetScreen") }
            )
        }
    }
}

/**
 * MainContents
 */
@Composable
fun MainContents(type: String, navController: NavController) {
    val context: Context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }
    val temperature: MutableState<Float> = remember { mutableFloatStateOf(0.0f) }
    val airVelocity: MutableState<Float> = remember { mutableFloatStateOf(0.0f) }
    val precipitation: MutableState<Float> = remember { mutableFloatStateOf(0.0f) }
    val humidity: MutableState<Float> = remember { mutableFloatStateOf(0.0f) }
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    LaunchedEffect(Unit) {
        getWeatherInformation(
            temperature = temperature,
            airVelocity = airVelocity,
            precipitation = precipitation,
            humidity = humidity,
            context = context,
            fusedLocationClient = fusedLocationClient,
            locationPermissionRequest = locationPermissionRequest,
            navController = navController
        )
    }

    val (weatherInfo: String, weatherIcon: ImageVector, weatherColor: Color) = getWeatherStatus(
        precipitation.value
    )

    Column {
        MainContentsHeader(
            temperature = temperature,
            airVelocity = airVelocity,
            precipitation = precipitation,
            humidity = humidity,
            context = context,
            fusedLocationClient = fusedLocationClient,
            locationPermissionRequest = locationPermissionRequest,
            navController = navController
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ContentsBox(
                imageVector = weatherIcon,
                iconColor = weatherColor,
                contentsTexts = arrayOf(
                    { MainContentsBoxText(text = "기상 정보 : $weatherInfo") },
                    { MainContentsBoxText(text = "1시간 강수량 : ${precipitation.value}mm") },
                    {
                        MainContentsBoxText(
                            text = "기온 : ${temperature.value}ºC" + if (temperature.value > 35) {
                                "(폭염 경보)"
                            } else if (temperature.value > 33) {
                                "(폭염 주의보)"
                            } else {
                                ""
                            }
                        )
                    },
                    {
                        MainContentsBoxText(
                            text = "풍속 : ${airVelocity.value}m/s" + if (airVelocity.value > 21) {
                                "(강풍 경보)"
                            } else if (airVelocity.value > 14) {
                                "(강풍 주의보)"
                            } else {
                                ""
                            }
                        )
                    },
                    { MainContentsBoxText(text = "습도 : ${humidity.value}%") }
                )
            )
            ContentsBox(
                modifier = Modifier.clickable { navController.navigate("CountermeasureScreen") },
                imageVector = Icons.Default.Report,
                iconColor = Color(0xFFFFCC00),
                contentsTexts = arrayOf({ MainContentsBoxText(text = "주의 행동 요령") })
            )
            if (type == "manager") {
                ContentsBox(
                    modifier = Modifier.clickable { navController.navigate("ProcessingScreen") },
                    imageVector = Icons.Default.Inventory,
                    iconColor = Color.Gray,
                    contentsTexts = arrayOf({ MainContentsBoxText(text = "사고 처리 내역") })
                )
            }
        }
    }
}

@Composable
fun MainContentsHeader(
    temperature: MutableState<Float>,
    airVelocity: MutableState<Float>,
    precipitation: MutableState<Float>,
    humidity: MutableState<Float>,
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    locationPermissionRequest: ActivityResultLauncher<Array<String>>,
    navController: NavController
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var isRefreshClickable by remember { mutableStateOf(true) }
    var iconColor by remember { mutableStateOf(Color.Black) }
    Row {
        BoldTextField(text = "정보", fontSize = 18.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier.clickable(enabled = isRefreshClickable) {
                if (isRefreshClickable) {
                    isRefreshClickable = false
                    coroutineScope.launch {
                        getWeatherInformation(
                            temperature = temperature,
                            airVelocity = airVelocity,
                            precipitation = precipitation,
                            humidity = humidity,
                            context = context,
                            fusedLocationClient = fusedLocationClient,
                            locationPermissionRequest = locationPermissionRequest,
                            navController = navController
                        )
                        iconColor = Color(121, 121, 121, 80)
                        delay(3000)
                        iconColor = Color.Black
                        isRefreshClickable = true
                    }
                }
            },
            imageVector = Icons.Default.Update,
            contentDescription = null,
            tint = iconColor
        )
    }
}

/**
 * ContentBox
 */
@Composable
fun ContentsBox(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconColor: Color = Color.Black,
    vararg contentsTexts: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = MaterialTheme.shapes.medium
            )
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = modifier
                    .padding(start = 10.dp, top = 25.dp, bottom = 25.dp)
                    .size(40.dp),
                imageVector = imageVector,
                contentDescription = null,
                tint = iconColor
            )
            Column {
                contentsTexts.forEach { contentsText ->
                    contentsText()
                }
            }
        }
    }
}

@Composable
fun MainContentsBoxText(
    text: String
) {
    Text(
        text = text,
        fontSize = 16.sp,
        modifier = Modifier
            .padding(start = 10.dp)
            .padding(vertical = 8.dp)
    )
}

fun getWeatherStatus(precipitation: Float): Triple<String, ImageVector, Color> {
    return when {
        precipitation > 30 -> Triple("호우 경보", Icons.Default.Water, Color(0xFF00BFFF))
        precipitation > 20 -> Triple("호우 주의보", Icons.Default.Water, Color(0xFF00BFFF))
        precipitation > 0 -> Triple("비", Icons.Default.WaterDrop, Color(0xFF00BFFF))
        else -> Triple("맑음", Icons.Default.WbSunny, Color(0xFFFF7F00))
    }
}

@SuppressLint("MissingPermission")
suspend fun getWeatherInformation(
    temperature: MutableState<Float>,
    airVelocity: MutableState<Float>,
    precipitation: MutableState<Float>,
    humidity: MutableState<Float>,
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    locationPermissionRequest: ActivityResultLauncher<Array<String>>,
    navController: NavController
) {
    if (hasLocationPermissions(context)) {
        val location = fusedLocationClient.lastLocation.await()
        location?.let { pos ->
            RetrofitInstance.retryApiService.getWeather(pos.latitude, pos.longitude)
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            val weather: WeatherResponse? = response.body()
                            weather?.let {
                                temperature.value = it.temperature
                                airVelocity.value = it.airVelocity
                                precipitation.value = it.precipitation
                                humidity.value = it.humidity
                            }
                            Log.d("HEAD METAL", "날씨 정보 로딩 성공")
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        networkErrorFinishApp(navController = navController, error = t)
                    }
                })
        }
    } else {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        Log.e("HEAD METAL", "위치 권한이 필요함")
    }
}