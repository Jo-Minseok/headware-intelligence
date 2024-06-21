package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Menu(navController: NavController) {
    val context = LocalContext.current
    val sharedAccount: SharedPreferences =
        context.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val sharedConfigure: SharedPreferences =
        context.getSharedPreferences("Configure", Activity.MODE_PRIVATE)
    val sharedAlert: SharedPreferences =
        context.getSharedPreferences("Alert", Activity.MODE_PRIVATE)
    val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()
    val sharedConfigureEdit: SharedPreferences.Editor = sharedConfigure.edit()
    val userRank = sharedAccount.getString("type", null)
    val userName = sharedAccount.getString("name", null)

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "로그아웃")
            },
            text = {
                Text("로그아웃 하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (sharedAccount.getString("type", null) == "manager") {
                            val call = RetrofitInstance.apiService.apiLogout(
                                id = sharedAccount.getString("userid", null).toString(),
                                alertToken = sharedAlert.getString("alert_token", null).toString()
                            )
                            call.enqueue(object : Callback<Void> {
                                override fun onResponse(p0: Call<Void>, p1: Response<Void>) {
                                    showLogoutDialog = false
                                    Toast.makeText(
                                        context,
                                        "로그아웃을 성공하였습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sharedAccountEdit.clear()
                                    sharedAccountEdit.apply()
                                    sharedConfigureEdit.clear()
                                    sharedConfigureEdit.apply()
                                    navController.navigate("loginScreen")
                                }

                                override fun onFailure(p0: Call<Void>, p1: Throwable) {
                                    Toast.makeText(
                                        context,
                                        "로그아웃을 실패하였습니다. 인터넷을 확인하세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        } else {
                            showLogoutDialog = false
                            Toast.makeText(
                                context,
                                "로그아웃을 성공하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            sharedAccountEdit.clear()
                            sharedAccountEdit.apply()
                            sharedConfigureEdit.clear()
                            sharedConfigureEdit.apply()
                            navController.navigate("loginScreen")
                        }
                    }
                ) { Text(text = "예") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(text = "아니오") }
            }
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "뒤로 가기",
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() }
            )
            Text(
                text = "메뉴",
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 10.dp)
            )
            Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                Button(
                    onClick = { navController.navigate("privacyScreen") },
                    modifier = Modifier.fillMaxWidth(),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "개인정보",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 10.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (userRank != null) {
                                Text(
                                    text = if (userRank == "manager") "관리자" else "근무자",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            if (userName != null) {
                                Text(
                                    text = userName,
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Button(
                        onClick = { navController.navigate("companyinfoScreen") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "참여 건설 업체",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
                            )
                        }
                    }
                    if (sharedAccount.getString("type", null) == "manager") {
                        Button(
                            onClick = {
                                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    notificationSettingOreo(context)
                                } else {
                                    notificationSettingOreoLess(context)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    e.printStackTrace()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "알림 설정",
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Button(
                        onClick = { navController.navigate("etcScreen") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "기타",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
                            )
                        }
                    }
                    Button(
                        onClick = { showLogoutDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "로그아웃",
                                color = Color(0xFFFF6600),
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun notificationSettingOreo(context: Context): Intent {
    return Intent().also { intent ->
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}

fun notificationSettingOreoLess(context: Context): Intent {
    return Intent().also { intent ->
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", context.packageName)
        intent.putExtra("app_uid", context.applicationInfo?.uid)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}
