package com.headmetal.headwareintelligence

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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

    var expanded by remember { mutableStateOf(false) }
    var selectedCompany by remember { mutableStateOf("") }
    val companyList = listOf("행복건설", "안전건설", "생명건설")

    var isManagement by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
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

            if (isManagement) {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "건설업체",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp) // 좌우 여백 추가
                            .clip(RoundedCornerShape(8.dp)) // 둥근 테두리 설정
                    ) {
                        Text(
                            text = selectedCompany.takeUnless { it.isEmpty() } ?: "선택하세요",
                            modifier = Modifier
                                .clickable(onClick = { expanded = true })
                                .background(Color(1f, 1f, 1f, 0.5f))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .width(350.dp)
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            companyList.forEach { company ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedCompany = company
                                        expanded = false
                                    }
                                ) {
                                    Text(text = company)
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "직무",
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
                                onClick = {
                                    part = "Normal"
                                    isManagement = false // 관리직이 아님
                                }
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
                                onClick = {
                                    part = "Manage"
                                    isManagement = true // 관리직임
                                }
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