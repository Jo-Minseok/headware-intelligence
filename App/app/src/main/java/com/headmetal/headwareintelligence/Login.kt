package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback

data class LoginResponse(
    val id: String,
    val name: String,
    val phoneNo: String,
    val email: String,
    val accessToken: String,
    val tokenType: String
)

@Composable
fun Login(navController: NavController = rememberNavController()) {
    BackOnPressed()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        LoadingScreen()
        LoginComposable(navController = navController)
    }
}

@Composable
fun LoginComposable(navController: NavController = rememberNavController()) {
    val id: MutableState<String> = remember { mutableStateOf("") }
    val pw: MutableState<String> = remember { mutableStateOf("") }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HelmetImage()
        TextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "ID") },
            customTextField = { CustomTextField(inputText = id) }
        )
        TextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "PW") },
            customTextField = {
                CustomTextField(
                    inputText = pw,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        )
        RadioButtonComposable(
            fieldLabel = { LoginFieldLabel(text = "Part") },
            customRadioButtonGroup = { CustomRadioButtonGroup(isEmployee, isManager) }
        )
        LoginFunctionButtonComposable(
            id = id,
            pw = pw,
            isManager = isManager,
            navController = navController
        )
        AppNameText()
    }
}

@Composable
fun LoginFunctionButtonComposable(
    id: MutableState<String> = remember { mutableStateOf("") },
    pw: MutableState<String> = remember { mutableStateOf("") },
    isManager: MutableState<Boolean> = remember { mutableStateOf(false) },
    navController: NavController = rememberNavController()
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
    ) {
        Row {
            LoginFunctionButton(
                modifier = Modifier.weight(1f),
                buttonText = "로그인"
            ) {
                val sharedAlert: SharedPreferences =
                    navController.context.getSharedPreferences(
                        "Alert",
                        Activity.MODE_PRIVATE
                    )
                val sharedAccount: SharedPreferences =
                    navController.context.getSharedPreferences(
                        "Account",
                        Activity.MODE_PRIVATE
                    )
                val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()

                LoadingState.show()
                RetrofitInstance.apiService.apiLogin(
                    alertToken = sharedAlert.getString("alert_token", null).toString(),
                    type = if (isManager.value) "manager" else "employee",
                    id = id.value,
                    pw = pw.value
                ).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: retrofit2.Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            sharedAccountEdit.putString("userid", response.body()?.id)
                            sharedAccountEdit.putString("password", pw.value)
                            sharedAccountEdit.putString("name", response.body()?.name)
                            sharedAccountEdit.putString("phone", response.body()?.phoneNo)
                            sharedAccountEdit.putString("email", response.body()?.email)
                            sharedAccountEdit.putString(
                                "token",
                                response.body()?.accessToken
                            )
                            sharedAccountEdit.putString(
                                "token_type",
                                response.body()?.tokenType
                            )
                            sharedAccountEdit.putString(
                                "type",
                                if (isManager.value) "manager" else "employee"
                            )
                            sharedAccountEdit.apply()
                            navController.navigate("mainScreen")
                            Toast.makeText(
                                navController.context,
                                response.body()?.name + "님 반갑습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showAlertDialog(
                                context = navController.context,
                                title = "로그인 실패",
                                message = "아이디 및 비밀번호를 확인하세요.",
                                buttonText = "확인"
                            ) {
                                pw.value = ""
                            }
                        }
                        LoadingState.hide()
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showAlertDialog(
                            context = navController.context,
                            title = "로그인 실패",
                            message = "서버 상태 및 네트워크 접속 불안정",
                            buttonText = "확인"
                        ) {
                            (navController.context as Activity).finish()
                        }
                        LoadingState.hide()
                        Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
                    }
                })
            }
            LoginFunctionButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                buttonText = "회원가입"
            ) { navController.navigate("signupScreen") }
            LoginFunctionButton(
                modifier = Modifier.weight(1f),
                buttonText = "계정 찾기"
            ) { navController.navigate("findidScreen") }
        }
    }
}

//프리뷰
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}

@Preview(showBackground = true)
@Composable
fun LoginComposablePreview() {
    LoginComposable()
}

@Preview(showBackground = true)
@Composable
fun LoginHelmetImagePreview() {
    HelmetImage()
}

@Preview(showBackground = true)
@Composable
fun LoginTextFieldComposable() {
    TextFieldComposable(
        fieldLabel = { LoginFieldLabel(text = "ID") },
        customTextField = { CustomTextField() }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginFieldLabelPreview() {
    LoginFieldLabel(text = "ID")
}

@Preview(showBackground = true)
@Composable
fun LoginCustomTextFieldPreview() {
    CustomTextField()
}

@Preview(showBackground = true)
@Composable
fun LoginRadioButtonComposablePreview() {
    RadioButtonComposable(
        fieldLabel = { LoginFieldLabel(text = "Part") },
        customRadioButtonGroup = { CustomRadioButtonGroup() }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginCustomRadioButtonGroupPreview() {
    CustomRadioButtonGroup()
}

@Preview(showBackground = true)
@Composable
fun LoginCustomRadioButtonSinglePreview() {
    CustomRadioButtonSingle(buttonText = "일반직")
}

@Preview(showBackground = true)
@Composable
fun LoginFunctionButtonComposablePreview() {
    LoginFunctionButtonComposable()
}

@Preview(showBackground = true)
@Composable
fun LoginFunctionButtonPreview() {
    LoginFunctionButton(buttonText = "로그인")
}

@Preview(showBackground = true)
@Composable
fun LoginAppNameTextPreview() {
    AppNameText()
}
