package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PermContactCalendar
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
        var selectCompany by remember { mutableStateOf("없음") }
        var expanded by remember { mutableStateOf(false) }
        var companies: List<String> = listOf("없음")

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                TextField(
                    value = id,
                    onValueChange = { /* 사용자 입력이 변경될 때 처리할 로직 */ },
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .alpha(0.6f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.LightGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
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
                TextField(
                    value = name,
                    onValueChange = { /* 사용자 입력이 변경될 때 처리할 로직 */ },
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .alpha(0.6f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(255, 190, 0, 150),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
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
                TextField(
                    value = phone,
                    onValueChange = { /* 사용자 입력이 변경될 때 처리할 로직 */ },
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .alpha(0.6f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(255, 190, 0, 150),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
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
                TextField(
                    value = email,
                    onValueChange = { /* 사용자 입력이 변경될 때 처리할 로직 */ },
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .alpha(0.6f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(255, 190, 0, 150),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
            Row(Modifier.padding(start = 30.dp, top = 30.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                )
                Text(
                    text = "비밀번호",
                    modifier = Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
            }

            TextField(
                value = "****",
                onValueChange = { /* 사용자 입력이 변경될 때 처리할 로직 */ },
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .alpha(0.6f),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(255, 190, 0, 150),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Row(Modifier.padding(start = 30.dp, top = 30.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Business,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(30.dp)
                )
                Text(
                    text = "건설업체",
                    modifier = Modifier.padding(end = 10.dp),
                    fontSize = 20.sp
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = selectCompany.takeUnless { it.isEmpty() } ?: "없음",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expanded = true })
                            .background(Color(255, 190, 0, 100))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(30.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        companies.forEach { company ->
                            DropdownMenuItem(
                                onClick = {
                                    selectCompany = company
                                    expanded = false
                                }
                            ) {
                                Text(text = company)
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* 처리할 로직 */ },
                    colors = ButtonDefaults.buttonColors(Color(0xFF372A1F)),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "개인정보 변경",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
