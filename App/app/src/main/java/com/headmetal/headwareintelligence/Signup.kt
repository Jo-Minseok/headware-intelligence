package com.headmetal.headwareintelligence

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Signup(navController: NavController, modifier: Modifier = Modifier) {
    var id by remember {
        mutableStateOf("")
    }
    var pw by remember {
        mutableStateOf("")
    }
    var confirm_pw by remember {
        mutableStateOf("")
    }
    var mail by remember {
        mutableStateOf("")
    }
    var part by remember {
        mutableStateOf("None")
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
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Id",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Password",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = pw,
                    onValueChange = { pw = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Confirm Password",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = confirm_pw,
                    onValueChange = { confirm_pw = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "E-mail",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = mail,
                    onValueChange = { mail = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Part",
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        Row {
                            RadioButton(
                                selected = (part == "Normal"),
                                onClick = { part = "Normal" }
                            )
                            Text(
                                text = "일반직",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        Row {
                            RadioButton(
                                selected = (part == "Manage"),
                                onClick = { part = "Manage" }
                                )
                            Text(
                                text = "관리직",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                    //DB에 이미 존재하는 ID, E-mail 체크, 입력한 비밀번호와 비밀번호 확인이 일치한지 체크
                    //입력된 내용에 무결성이 존재하지 않을 경우 입력된 정보를 DB에 추가
                    navController.navigate("loginScreen")
                },
                colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                modifier = Modifier.padding(vertical = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "회원가입",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}