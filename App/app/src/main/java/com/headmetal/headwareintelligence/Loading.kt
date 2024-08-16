package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    Loading(navController = rememberNavController())
}

@Composable
fun Loading(navController: NavController) {
    val sharedAlert: SharedPreferences =
        LocalContext.current.getSharedPreferences("Alert", MODE_PRIVATE)
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", MODE_PRIVATE)
    val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()
    val userId = sharedAccount.getString("userid", "")
    val userPassword = sharedAccount.getString("password", "")
    val type = sharedAccount.getString("type", "") ?: "employee"

    val autoLogin = remember { isAutoLoginAvailable(sharedAccount) }

    requestRequiredPermissions(LocalContext.current)

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.apiGetStatus().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    if (autoLogin) {
                        RetrofitInstance.apiService.apiLogin(
                            alertToken = sharedAlert.getString("alert_token", null).toString(),
                            type = type,
                            id = userId,
                            pw = userPassword
                        ).enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful) {
                                    if (navController.currentDestination?.route != "MainScreen") {
                                        Toast.makeText(
                                            navController.context,
                                            "${response.body()?.name} 님 반갑습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("MainScreen") {
                                            popUpTo("LoadingScreen") {
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
                                        navController.navigate("LoginScreen")
                                        sharedAccountEdit.clear().apply()
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
                        navController.navigate("LoginScreen")
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

    LoginScreen {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            AppNameText()
        }
    }
}

fun isAutoLoginAvailable(sharedAccount: SharedPreferences): Boolean {
    val userId = sharedAccount.getString("userid", null)
    val accessToken = sharedAccount.getString("token", null)
    return userId != null && accessToken != null
}

fun requestRequiredPermissions(context: Context) {
    val permissions = getRequiredPermissions()

    val permissionsToRequest = mutableListOf<String>()
    permissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(permission)
        }
    }

    if (permissionsToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            context as Activity, permissionsToRequest.toTypedArray(),
            MainActivity.REQUEST_PERMISSIONS_CODE
        )
        Log.d("HEAD METAL", "권한을 요청하였습니다.")
    } else {
        Log.d("HEAD METAL", "권한이 이미 존재합니다.")
    }
}

fun getRequiredPermissions(): MutableList<String> {
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        permissions.add(Manifest.permission.BLUETOOTH)
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
    } else {
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    }

    return permissions
}