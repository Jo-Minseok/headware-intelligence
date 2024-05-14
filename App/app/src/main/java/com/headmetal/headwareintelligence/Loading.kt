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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

@Composable
fun Loading(navController: NavController) {
    var autoLogin: Boolean = false
    val auto: SharedPreferences = LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val user_id = auto.getString("userid", null)
    val access_token = auto.getString("token", null)

    if (user_id != null && access_token != null) {
        autoLogin = true
    }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionsToRequest = remember { mutableListOf<String>() }
    val permissions = mutableListOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
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

    // 권한 요청 런처 설정
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, isGranted) ->
            if (!isGranted) {
                showDialog = true
            }
        }
    }

    // 필요한 권한 확인 및 요청
    permissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(permission)
        }
    }
    if (permissionsToRequest.isNotEmpty()) {
        launcher.launch(permissionsToRequest.toTypedArray())
    }

    // 서버 상태 확인
    val apiService = RetrofitInstance.apiService
    val call = apiService.getStatus()
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                if (autoLogin) {
                    navController.navigate("mainScreen")
                } else {
                    navController.navigate("loginScreen")
                }
            } else {
                showDialog = true
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            showDialog = true
        }
    })

    if (showDialog) {
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
