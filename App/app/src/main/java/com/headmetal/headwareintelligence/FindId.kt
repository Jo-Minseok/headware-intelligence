package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun FindId(navController: NavController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) { FindIdComposable(navController = navController) }
}

@Composable
fun FindIdComposable(navController: NavController = rememberNavController()) {
    val name: MutableState<String> = remember { mutableStateOf("") }
    val email: MutableState<String> = remember { mutableStateOf("") }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HelmetImage()
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "이름") },
            inputTextField = { InputTextField(inputText = name) }
        )
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "이메일") },
            inputTextField = { InputTextField(inputText = email) }
        )
        CustomRadioButtonComposable(
            fieldLabel = { LoginFieldLabel(text = "직무") },
            customRadioButtonGroup = { CustomRadioButtonGroup(isEmployee, isManager) }
        )
        LoginFunctionButtonComposable(
            loginFunctionButtons = arrayOf(
                {
                    LoginFunctionButton(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        buttonText = "아이디 찾기"
                    ) {
                        LoadingState.show()
                        RetrofitInstance.apiService.apiFindId(
                            ForgotIdRequest(
                                name.value,
                                email.value,
                                if (isManager.value) "manager" else "employee"
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
                },
                {
                    LoginFunctionButton(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        buttonText = "비밀번호 변경"
                    ) { navController.navigate("FindPwScreen") }
                }
            )
        )
        AppNameText()
    }
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun FindIdPreview() {
    FindId()
}

@Preview(showBackground = true)
@Composable
fun FindIdComposablePreview() {
    FindIdComposable()
}

@Preview(showBackground = true)
@Composable
fun FindIdHelmetImagePreview() {
    HelmetImage()
}

@Preview(showBackground = true)
@Composable
fun FindIdTextFieldComposablePreview() {
    LoginTextFieldComposable(
        fieldLabel = { LoginFieldLabel(text = "이름") },
        inputTextField = { InputTextField() }
    )
}

@Preview(showBackground = true)
@Composable
fun FindIdFieldLabelPreview() {
    LoginFieldLabel(text = "이름")
}

@Preview(showBackground = true)
@Composable
fun FindIdInputTextFieldPreview() {
    InputTextField()
}

@Preview(showBackground = true)
@Composable
fun FindIdCustomRadioButtonComposablePreview() {
    CustomRadioButtonComposable(
        fieldLabel = { LoginFieldLabel(text = "직무") },
        customRadioButtonGroup = { CustomRadioButtonGroup() }
    )
}

@Preview(showBackground = true)
@Composable
fun FindIdCustomRadioButtonGroupPreview() {
    CustomRadioButtonGroup()
}

@Preview(showBackground = true)
@Composable
fun FindIdCustomRadioButtonSinglePreview() {
    CustomRadioButtonSingle(buttonText = "일반직")
}

@Preview(showBackground = true)
@Composable
fun FindIdFunctionButtonComposablePreview() {
    LoginFunctionButtonComposable(
        { LoginFunctionButton(buttonText = "아이디 찾기") },
        { LoginFunctionButton(buttonText = "비밀번호 변경") }
    )
}

@Preview(showBackground = true)
@Composable
fun FindIdFunctionButtonPreview() {
    LoginFunctionButton(buttonText = "아이디 찾기")
}

@Preview(showBackground = true)
@Composable
fun FindIdAppNameTextPreview() {
    AppNameText()
}
