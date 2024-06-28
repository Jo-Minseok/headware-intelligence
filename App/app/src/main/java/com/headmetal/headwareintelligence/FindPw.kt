package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ForgotPw(
    val id: String,
    val phoneNo: String,
    val password: String,
    val rePassword: String,
    val type: String
)

@Composable
fun FindPw(navController: NavController = rememberNavController()) {
    val id: MutableState<String> = remember { mutableStateOf("") }
    val phone: MutableState<String> = remember { mutableStateOf("") }
    val pw: MutableState<String> = remember { mutableStateOf("") }
    val rePw: MutableState<String> = remember { mutableStateOf("") }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "아이디")
                CustomTextField(inputText = id)
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "전화번호")
                CustomTextField(inputText = phone)
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "새 비밀번호")
                CustomTextField(
                    inputText = pw,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "비밀번호 확인")
                CustomTextField(
                    inputText = rePw,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "직무")
                Row {
                    DistinguishButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        buttonText = "일반직",
                        firstButtonSwitch = isEmployee,
                        secondButtonSwitch = isManager,
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    DistinguishButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        buttonText = "관리직",
                        firstButtonSwitch = isManager,
                        secondButtonSwitch = isEmployee,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                LoginFunctionButton(buttonText = "비밀번호 변경") {
                    if (pw.value == rePw.value && pw.value.isNotEmpty()) {
                        LoadingState.show()
                        RetrofitInstance.apiService.apiChangePw(
                            ForgotPw(
                                id.value,
                                phone.value,
                                pw.value,
                                rePw.value,
                                if (isManager.value) "manager" else "employee"
                            )
                        ).enqueue(object : Callback<ForgotPw> {
                            override fun onResponse(
                                call: Call<ForgotPw>,
                                response: Response<ForgotPw>
                            ) {
                                if (response.isSuccessful) {
                                    showAlertDialog(
                                        context = navController.context,
                                        title = "비밀번호 변경 성공",
                                        message = "로그인 화면으로 이동합니다.",
                                        buttonText = "확인"
                                    ) {
                                        navController.navigate("loginScreen")
                                    }
                                } else {
                                    showAlertDialog(
                                        context = navController.context,
                                        title = "비밀번호 변경 실패",
                                        message = "존재하지 않는 계정입니다.",
                                        buttonText = "확인"
                                    )
                                    Log.e("HEAD METAL", "비밀번호 변경 요청 실패: ${response.code()}")
                                }
                                LoadingState.hide()
                            }

                            override fun onFailure(call: Call<ForgotPw>, t: Throwable) {
                                LoadingState.hide()
                                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
                            }
                        })
                    } else {
                        showAlertDialog(
                            context = navController.context,
                            title = "비밀번호 변경 실패",
                            message = "입력한 정보를 다시 확인하세요!",
                            buttonText = "확인"
                        )
                    }
                }
            }
            AppNameText()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FindPwPreview() {
    FindPw()
}
