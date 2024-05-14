package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun Loading(navController: NavController) {
    var autoLogin:Boolean = false
    val auto: SharedPreferences = LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val user_id = auto.getString("userid",null)
    val access_token = auto.getString("token",null)
    Log.d("HEAD METAL",user_id.toString())
    Log.d("HEAD METAL",access_token.toString())
    if(user_id != null && access_token != null){
        autoLogin = true
    }
    LaunchedEffect(true) {
        LoadingState.show()
        LoadingState.hide()
        if(autoLogin){
            navController.navigate("mainScreen")
        }
        else {
            navController.navigate("loginScreen")
        }
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
