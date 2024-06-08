package com.headmetal.headwareintelligence

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 서버로부터 받는 로그인 응답 데이터 모델 정의
data class LoginResponse(
    val id: String,
    val name: String,
    val accessToken: String,
    val tokenType: String
)

fun performLogin(
    username: String?,
    password: String?,
    isManager: Boolean,
    navController: NavController,
    pwState: MutableState<String>
) {
    val sharedAccount: SharedPreferences =
        navController.context.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val sharedAlert: SharedPreferences =
        navController.context.getSharedPreferences("Alert", Activity.MODE_PRIVATE)
    val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()
    val builder = AlertDialog.Builder(navController.context)

    LoadingState.show()
    RetrofitInstance.apiService.apiLogin(
        alertToken = sharedAlert.getString("alert_token", null).toString(),
        type = if (isManager) "manager" else "employee",
        id = username,
        pw = password
    ).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.isSuccessful) {
                sharedAccountEdit.putString("userid", response.body()?.id)
                sharedAccountEdit.putString("password", password)
                sharedAccountEdit.putString("name", response.body()?.name)
                sharedAccountEdit.putString("token", response.body()?.accessToken)
                sharedAccountEdit.putString("token_type", response.body()?.tokenType)
                sharedAccountEdit.putString("type", if (isManager) "manager" else "employee")
                sharedAccountEdit.apply()
                navController.navigate("mainScreen")
                Toast.makeText(
                    navController.context,
                    response.body()?.name + "님 반갑습니다",
                    Toast.LENGTH_SHORT
                ).show()
                LoadingState.hide()
            } else {
                builder.setTitle("로그인 실패")
                builder.setMessage("아이디 및 비밀번호를 확인하세요.")
                // 확인 버튼 설정
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                    pwState.value = ""
                }
                // 다이얼로그 표시
                val dialog = builder.create()
                dialog.show()
                LoadingState.hide()
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            builder.setTitle("로그인 실패")
            builder.setMessage("서버 상태 및 네트워크 접속 불안정")
            // 확인 버튼 설정
            builder.setPositiveButton("확인") { _, _ ->
                (navController.context as Activity).finish()
            }
            // 다이얼로그 표시
            val dialog = builder.create()
            dialog.show()
            LoadingState.hide()
        }
    })
}

@Composable
fun Login(navController: NavController) {
    val idState = remember {
        mutableStateOf("")
    }
    val pwState = remember {
        mutableStateOf("")
    }
    var isManager by remember {
        mutableStateOf(false)
    }

    BackOnPressed()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        LoadingScreen()
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.helmet),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "ID",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = idState.value,
                    onValueChange = { idState.value = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "PW",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = pwState.value,
                    onValueChange = { pwState.value = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Part",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row {
                    Button(
                        onClick = { isManager = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        content = { Text(text = "일반직", color = Color.Black) },
                        colors = ButtonDefaults.buttonColors(if (!isManager) Color(0xFFADD8E6) else Color.LightGray)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = { isManager = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        content = { Text(text = "관리직", color = Color.Black) },
                        colors = ButtonDefaults.buttonColors(if (isManager) Color(0xFFADD8E6) else Color.LightGray)
                    )
                }
            }
            Row {
                Button(
                    onClick = {
                        performLogin(
                            idState.value,
                            pwState.value,
                            isManager,
                            navController,
                            pwState
                        )
                    },
                    colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "로그인",
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { navController.navigate("signupScreen") },
                    colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "회원가입",
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { navController.navigate("findidScreen") },
                    colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "계정 찾기",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
