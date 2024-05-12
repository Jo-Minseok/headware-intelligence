package com.headmetal.headwareintelligence

import android.os.Bundle
import android.util.Log
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.headmetal.headwareintelligence.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 알림 토큰 생성
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
            if(!task.isSuccessful){
                Log.w(Constants.MessageNotificationKeys.TAG,"Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("FCM MESSAGE","token $token")
        })

        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF9F9F9)
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "loadingScreen") {
                        composable("loadingScreen") {
                            Loading(navController)
                        }
                        composable("loginScreen") {
                            Login(navController)
                        }
                        composable("signupScreen") {
                            Signup(navController)
                        }
                        composable("mainScreen") {
                            Main(navController)
                        }
                        composable("processingScreen") {
                            Processing(navController)
                        }
                        composable("menuScreen") {
                            Menu(navController)
                        }
                    }
                }
            }
        }
    }
}