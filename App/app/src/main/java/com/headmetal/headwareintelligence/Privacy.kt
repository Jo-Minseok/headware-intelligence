package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PermContactCalendar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Privacy(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        val sharedAccount: SharedPreferences =
            LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
        val userId = sharedAccount.getString("userid", null)
        val userName = sharedAccount.getString("name", null)
        val userPhone = sharedAccount.getString("phone", null)
        val userEmail = sharedAccount.getString("email", null)

        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "뒤로 가기",
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() }
            )
            Text(
                text = "사용자 정보",
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 10.dp)
            )
            Row(Modifier.padding(start = 30.dp, top = 30.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                )
                Text(
                    text = "아이디",
                    modifier = Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
            }
            userId?.let { id ->
                Text(
                    text = id,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                )
            }
            Row(Modifier.padding(start = 30.dp, top = 30.dp)) {
                Icon(
                    imageVector = Icons.Outlined.PermContactCalendar,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                )
                Text(
                    text = "이름",
                    modifier = Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
            }
            userName?.let { name ->
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                )
            }
            Row(Modifier.padding(start = 30.dp, top = 30.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                )
                Text(
                    text = "전화번호",
                    modifier = Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
            }
            userPhone?.let { phone ->
                Text(
                    text = phone,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                )
            }
            Row(Modifier.padding(start = 30.dp, top = 30.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Mail,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                )
                Text(
                    text = "이메일",
                    modifier = Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
            }
            userEmail?.let { email ->
                Text(
                    text = email,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                )
            }
        }
    }
}
