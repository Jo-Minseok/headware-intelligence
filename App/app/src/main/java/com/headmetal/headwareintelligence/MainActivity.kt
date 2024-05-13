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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
                    val navController: NavHostController = rememberNavController()
                    val bottomBarHeight = 56.dp
                    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

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
//                    NavHost(navController, startDestination = "loadingScreen") {
//                        composable("loadingScreen") {
//                            Loading(navController)
//                        }
//                        composable("loginScreen") {
//                            Login(navController)
//                        }
//                        composable("signupScreen") {
//                            Signup(navController)
//                        }
//                        composable("mainScreen") {
//                            Main()
//                        }
//                        composable("processingScreen") {
//                            Processing()
//                        }
//                        composable("menuScreen") {
//                            Menu()
//                        }
//                        composable("findidScreen") {
//                            Findid(navController)
//                        }
//                        composable("findpwScreen") {
//                            Findpw(navController)
//                        }
//                    }
                }

            }
        }
    }
}