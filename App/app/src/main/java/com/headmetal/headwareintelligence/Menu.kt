package com.headmetal.headwareintelligence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun Menu() {
    Surface()
    {
        Column() {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "메뉴"
                )
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null
                    )
                }
            }

            Column() {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "개인 정보", color = Color.Black)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("건설 업체 정보", color = Color.Black)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("알림 설정", color = Color.Black)
                    Switch(
                        checked = true,
                        onCheckedChange = {
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2FA94E),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF1D2024)
                        )
                    )
                }
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("기타", color = Color.Black)
                }
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = Color(0xFFFF6600)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("로그 아웃", color = Color(0xFFFF6600))
                }
            }
        }
    }
}