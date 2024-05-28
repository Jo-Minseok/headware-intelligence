package com.headmetal.headwareintelligence

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    companion object{
        const val REQUEST_PERMISSIONS_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val auto: SharedPreferences = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
        val autoLoginEdit: SharedPreferences.Editor = auto.edit()
        // 알림 토큰 생성
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    Constants.MessageNotificationKeys.TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }
            val token = task.result
            autoLoginEdit.putString("alert_token",token)
            autoLoginEdit.apply()
            Log.d("FCM MESSAGE", "token $token")
        })

        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()
            var buttonsVisible = remember { mutableStateOf(true) }
            Scaffold(
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    if (currentRoute in listOf(
                            Destinations.Main.route,
                            Destinations.Processing.route,
                            Destinations.Menu.route
                        )) {
                        BottomBar(
                            navController = navController,
                            state = buttonsVisible,
                            modifier = Modifier
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    RootNavGraph(navController = navController)
                }
            }
        }
    }

}