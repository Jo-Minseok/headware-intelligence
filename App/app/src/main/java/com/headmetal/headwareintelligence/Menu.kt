package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
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

@Composable
fun Menu(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9))
    {
        val auto: SharedPreferences = LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
        val autoLoginEdit: SharedPreferences.Editor = auto.edit()
        val userrank = auto.getString("type", null)
        val username = auto.getString("name", null)

        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.
                padding(20.dp)
                .clickable {navController.navigateUp()}
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "메뉴",
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                Spacer(modifier = Modifier.width(125.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp, vertical = 15.5.dp)
            ) {
                Button(
                    onClick = {navController.navigate("privacyScreen")},
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
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.Black // 아이콘 색상 설정
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (userrank != null) {
                                Text(
                                    text = userrank,
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            if (username != null) {
                                Text(
                                    text = username,
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Button(
                        onClick = {navController.navigate("companyinfoScreen")},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 2.5.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = Color.Black
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
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 2.5.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "알림 설정",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Switch(
                                checked = true,
                                onCheckedChange = { },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF2FA94E),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFF1D2024)
                                ),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Button(
                        onClick = {navController.navigate("etcScreen")},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 2.5.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "기타",
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
                        onClick = {
                            autoLoginEdit.clear()
                            autoLoginEdit.apply()
                            navController.navigate("loginScreen")
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 2.5.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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