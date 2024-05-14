package com.headmetal.headwareintelligence

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Loading(navController: NavController) {
    var autoLogin by remember { mutableStateOf(false) }
    val auto: SharedPreferences = LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val userId = auto.getString("userid", null)
    val accessToken = auto.getString("token", null)

    if (userId != null && accessToken != null) {
        autoLogin = true
    }

    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionsToRequest = remember { mutableListOf<String>() }
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.INTERNET
    )

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        permissions.add(Manifest.permission.BLUETOOTH)
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
    } else {
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    }

    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap -> val areGranted = permissionsMap.values.reduce{acc,next -> acc && next}
        if(areGranted){
            Log.d("HEAD METAL","권한이 동의되었습니다.")
        }
        else{
            Log.d("HEAD METAL", "권한이 거부되었습니다.")
        }

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