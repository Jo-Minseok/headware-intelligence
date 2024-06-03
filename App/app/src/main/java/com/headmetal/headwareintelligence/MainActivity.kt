package com.headmetal.headwareintelligence

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import android.app.Activity
import android.content.SharedPreferences

class MainActivity : ComponentActivity() {
    companion object{
        const val REQUEST_PERMISSIONS_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedAlert: SharedPreferences = this.getSharedPreferences("Alert", Activity.MODE_PRIVATE)
        val sharedAlertEdit: SharedPreferences.Editor = sharedAlert.edit()
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
            sharedAlertEdit.putString("alert_token",token).apply()
            Log.d("FCM MESSAGE", "token $token")
        })

        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()
            Scaffold(
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