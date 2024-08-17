package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ForgotIdRequest(
    val name: String,
    val email: String,
    val type: String
)

data class ForgotIdResult(
    val id: String
)

// 프리뷰
@Preview(showBackground = true)
@Composable
fun FindIdPreview() {
    FindId()
}

@Preview(showBackground = true)
@Composable
fun FindIdHelmetImagePreview() {
    HelmetImage()
}

@Preview(showBackground = true)
@Composable
fun FindIdTextFieldComposablePreview() {
    LabelAndInputComposable(labelText = "이름", inputText = remember {
        mutableStateOf("")
    })
}

@Preview(showBackground = true)
@Composable
fun FindIdFunctionButtonPreview() {
    LoginFunctionButton(buttonText = "아이디 찾기", onClick = {})
}

@Preview(showBackground = true)
@Composable
fun FindIdAppNameTextPreview() {
    AppNameText()
}

@Composable
fun FindId(navController: NavController = rememberNavController()) {
    LoginScreen(content = {
        val name: MutableState<String> = remember { mutableStateOf("") }
        val email: MutableState<String> = remember { mutableStateOf("") }
        val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
        val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    LabelAndInputComposable(
                        labelText = "이름",
                        inputText = name,
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                    LabelAndInputComposable(
                        labelText = "이메일",
                        inputText = email,
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                }
                LabelAndRadioButtonComposable(
                    labelText = "직무",
                    firstButtonSwitch = isEmployee,
                    secondButtonSwitch = isManager,
                    firstButtonText = "일반직",
                    secondButtonText = "관리직"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LoginFunctionButton(
                            buttonText = "아이디 찾기",
                            onClick = {
                                idSearch(
                                    name = name.value,
                                    email = email.value,
                                    isManager = isManager.value,
                                    navController = navController
                                )
                            }
                        )
                        LoginFunctionButton(
                            buttonText = "비밀번호 변경",
                            onClick = { navController.navigate("FindPwScreen") }
                        )
                    }
                }
            }

            AppNameText()
        }
    }
    )
}

fun idSearchVerify(name: String, email: String, isManager: Boolean, navController: NavController){
    
}

fun idSearch(name: String, email: String, isManager: Boolean, navController: NavController) {
    LoadingState.show()
    RetrofitInstance.apiService.apiFindId(
        ForgotIdRequest(
            name,
            email,
            if (isManager) "manager" else "employee"
        )
    ).enqueue(object : Callback<ForgotIdResult> {
        override fun onResponse(
            call: Call<ForgotIdResult>,
            response: Response<ForgotIdResult>
        ) {
            if (response.isSuccessful) {
                val id = response.body()?.id
                if (!id.isNullOrEmpty()) {
                    showAlertDialog(
                        context = navController.context,
                        title = "아이디 찾기 성공",
                        message = "ID: $id",
                        buttonText = "확인"
                    )
                } else {
                    showAlertDialog(
                        context = navController.context,
                        title = "서버 응답 실패",
                        message = "서버 응답에 실패 하였습니다.",
                        buttonText = "확인"
                    )
                }
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "아이디 찾기 실패",
                    message = "일치하는 계정을 찾을 수 없습니다.",
                    buttonText = "확인"
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<ForgotIdResult>, t: Throwable) {
            LoadingState.hide()
            Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
        }
    })
}