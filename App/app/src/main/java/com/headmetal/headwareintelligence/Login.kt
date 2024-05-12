package com.headmetal.headwareintelligence

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// 서버로부터 받는 로그인 응답 데이터 모델 정의
data class LoginResponse(
    val id: String,
    val access_token: String,
    val token_type: String
)

fun performLogin(username: String, password: String, isManager: Boolean, navController: NavController, idState: MutableState<String>, pwState: MutableState<String>) {
    val call = if (isManager) {
        RetrofitInstance.apiService.loginmanager(username, password)
    } else {
        RetrofitInstance.apiService.loginemployee(username, password)
    }
    call.enqueue(object : Callback<LoginResponse>{
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.isSuccessful) {
                val loginResponse = response.body()
                navController.navigate("mainScreen")
            }
            else {
                // 로그인 실패 처리
                showLoginFailedDialog(navController, idState,pwState)
            }
        }
        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            // 통신 실패 처리
            println("서버 통신 실패: ${t.message}")
        }
    })
}

@Composable
fun Login(navController: NavController, modifier: Modifier = Modifier) {
    val idState = remember {
        mutableStateOf("")
    }
    val pwState = remember {
        mutableStateOf("")
    }
    var isManager by remember {
        mutableStateOf(false)
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
                    value = idState.value,
                    onValueChange = { idState.value = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.alpha(0.6f).width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Text(
                    text = "Password",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = pwState.value,
                    onValueChange = { pwState.value = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.alpha(0.6f).width(350.dp),
                    visualTransformation = PasswordVisualTransformation(),
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
                                selected = !isManager,
                                onClick = { isManager = false }
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
                                selected = isManager,
                                onClick = { isManager = true }
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

            Row {

                Button(
                    onClick = { performLogin(idState.value, pwState.value, isManager,
                        navController,idState, pwState) },
                    colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                    modifier = Modifier.padding(horizontal = 8.dp),
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
                    modifier = Modifier.padding(horizontal = 8.dp),
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
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "계정 정보 찾기",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "HeadWear - Intelligence",
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun showLoginFailedDialog(navController: NavController,idState: MutableState<String>, pwState: MutableState<String>) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("로그인 실패")
    builder.setMessage("아이디나 비밀번호를 확인하세요")

    // 확인 버튼 설정
    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
        idState.value = ""
        pwState.value = ""

    }

    // 다이얼로그 표시
    val dialog = builder.create()
    dialog.show()
}
