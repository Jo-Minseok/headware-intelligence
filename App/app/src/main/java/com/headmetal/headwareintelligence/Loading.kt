package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController

@Composable
fun Loading(navController: NavController = rememberNavController()) {
    val sharedAlert: SharedPreferences =
        LocalContext.current.getSharedPreferences("Alert", MODE_PRIVATE)
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", MODE_PRIVATE)
    val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()

    val userId = sharedAccount.getString("userid", null)
    val userPassword = sharedAccount.getString("password", null)
    val accessToken = sharedAccount.getString("token", null)
    val type = sharedAccount.getString("type", null)

    var autoLogin by remember { mutableStateOf(false) }

    if (userId != null && accessToken != null) {
        autoLogin = true
    }

    val permissions = mutableListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        permissions.add(Manifest.permission.BLUETOOTH)
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
    } else {
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    }

    val permissionsToRequest = mutableListOf<String>()
    permissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(
                LocalContext.current,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(permission)
        }
    }

    if (permissionsToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            LocalContext.current as Activity, permissionsToRequest.toTypedArray(),
            MainActivity.REQUEST_PERMISSIONS_CODE
        )
        Log.d("HEAD METAL", "권한을 요청하였습니다.")
    } else {
        Log.d("HEAD METAL", "권한이 이미 존재합니다.")
    }

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.apiGetStatus().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    if (autoLogin) {
                        RetrofitInstance.apiService.apiLogin(
                            alertToken = sharedAlert.getString("alert_token", null).toString(),
                            type = type.toString(),
                            id = userId,
                            pw = userPassword
                        ).enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful) {
                                    if (navController.currentDestination?.route != "mainScreen") {
                                        Toast.makeText(
                                            navController.context,
                                            response.body()?.name + "님 반갑습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("mainScreen") {
                                            popUpTo("loadingScreen") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                } else {
                                    showAlertDialog(
                                        context = navController.context,
                                        title = "자동 로그인 실패",
                                        message = "변경된 비밀번호를 확인하세요.",
                                        buttonText = "확인"
                                    ) {
                                        navController.navigate("loginScreen")
                                        sharedAccountEdit.clear()
                                        sharedAccountEdit.apply()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                showAlertDialog(
                                    context = navController.context,
                                    title = "로그인 실패",
                                    message = "서버 상태 및 네트워크 접속 불안정",
                                    buttonText = "확인"
                                ) { (navController.context as Activity).finish() }
                                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
                            }
                        })
                    } else {
                        navController.navigate("loginScreen")
                    }
                } else {
                    showAlertDialog(
                        context = navController.context,
                        title = "서버 접속 실패",
                        message = "서버 상태 및 네트워크 접속 불안정",
                        buttonText = "확인"
                    ) { (navController.context as Activity).finish() }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showAlertDialog(
                    context = navController.context,
                    title = "서버 접속 실패",
                    message = "서버 상태 및 네트워크 접속 불안정",
                    buttonText = "확인"
                ) { (navController.context as Activity).finish() }
                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
            }
        })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            AppNameText()
        }
    }
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    Loading()
}

@Preview(showBackground = true)
@Composable
fun LoadingHelmetImagePreview() {
    HelmetImage()
}

@Preview(showBackground = true)
@Composable
fun LoadingAppNameTextPreview() {
    AppNameText()
}
