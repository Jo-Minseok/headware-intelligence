package com.headmetal.headwareintelligence

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun Loading(navController: NavController) {
    var autoLogin by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auto: SharedPreferences = context.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val userId = auto.getString("userid", null)
    val userPassword = auto.getString("password",null)
    val accessToken = auto.getString("token", null)
    val type = auto.getString("type",null)
    if (userId != null && accessToken != null) {
        autoLogin = true
    }

    // 권한 요청
    val permissionsToRequest = mutableListOf<String>()
    val permissions = mutableListOf<String>(
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
    permissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(LocalContext.current, permission) != PackageManager.PERMISSION_GRANTED) {
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
    
    var serverNotConnect by remember { mutableStateOf(false) }

    // 서버 상태 확인
    val apiService = RetrofitInstance.apiService
    val call = apiService.API_getStatus()
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                if (autoLogin) {
                    when(performLogin(userId,userPassword, isManager = type == "manager",auto)){
                        0-> navController.navigate("mainScreen")
                        1-> {
                            AlertDialog.Builder(context)
                                .setTitle("자동 로그인 실패")
                                .setMessage("비밀번호나 계정 정보가 변경되었습니다.")
                                .setPositiveButton("확인") { dialog, _ -> navController.navigate("loginScreen") }
                                .show()
                        }
                        2-> AlertDialog.Builder(context)
                            .setTitle("서버 연결 실패")
                            .setMessage("네트워크가 불안정하거나 서버에 연결되지 않습니다.")
                            .setPositiveButton("확인") { dialog, _ -> (context as Activity).finish() }
                            .show()
                    }
                } else {
                    navController.navigate("loginScreen")
                }
            } else {
                serverNotConnect = true
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            serverNotConnect = true
        }
    })

    if (serverNotConnect) {
        AlertDialog.Builder(context)
            .setTitle("서버 연결 실패")
            .setMessage("네트워크가 불안정하거나 서버에 연결되지 않습니다.")
            .setPositiveButton("확인") { dialog, _ -> (context as Activity).finish() }
            .show()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.helmet),
                contentDescription = null
            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold
            )
            LoadingScreen()
        }
    }
}