package com.headmetal.headwareintelligence

import android.os.Bundle
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.headmetal.headwareintelligence.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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