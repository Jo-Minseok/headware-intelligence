package com.headmetal.headwareintelligence

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.headmetal.headwareintelligence.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
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
                }
            }
        }
    }
}