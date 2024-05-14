package com.headmetal.headwareintelligence

import android.os.Bundle
import android.util.Log
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.headmetal.headwareintelligence.ui.theme.MyApplicationTheme
import android.Manifest

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
                    val navController: NavHostController = rememberNavController()
                    var buttonsVisible = remember { mutableStateOf(true) }

                    Scaffold(
                        bottomBar = {
                            BottomBar(
                                navController = navController,
                                state = buttonsVisible,
                                modifier = Modifier
                            )
                        }) { paddingValues ->
                        Box(
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            NavigationGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }
}