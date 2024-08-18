package com.headmetal.headwareintelligence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class LoginResponse(
    val id: String,
    val name: String,
    val phoneNo: String,
    val email: String,
    val accessToken: String,
    val tokenType: String
)

// 프리뷰
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun LoginFunctionButtonComposablePreview() {
    Row {
        LoginFunctionButton(
            modifier = Modifier.weight(1f),
            buttonText = "로그인"
        ) {}
        LoginFunctionButton(
            modifier = Modifier.weight(1f),
            buttonText = "회원가입"
        ) {}
        LoginFunctionButton(
            modifier = Modifier.weight(1f),
            buttonText = "계정 찾기"
        ) {}
    }
}

@Composable
fun Login(navController: NavController) {
    val id: MutableState<String> = remember { mutableStateOf("") }
    val pw: MutableState<String> = remember { mutableStateOf("") }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

    BackOnPressed()
    LoginScreen {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    LabelAndInputComposable(
                        labelText = "ID",
                        inputText = id,
                        labelFontWeight = FontWeight.Bold,
                        labelFontSize = 18.sp,
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                    LabelAndInputComposable(
                        labelText = "PW",
                        inputText = pw,
                        labelFontWeight = FontWeight.Bold,
                        labelFontSize = 18.sp,
                        visualTransformation = PasswordVisualTransformation(),
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                }
                LabelAndRadioButtonComposable(
                    labelText = "Part",
                    labelFontWeight = FontWeight.Bold,
                    labelFontSize = 18.sp,
                    firstButtonText = "일반직",
                    secondButtonText = "관리직",
                    firstButtonSwitch = isEmployee,
                    secondButtonSwitch = isManager
                )
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    LoginFunctionButton(
                        modifier = Modifier.weight(1f),
                        buttonText = "로그인",
                        onClick = {
                            loginPOST(
                                inputId = id.value,
                                inputPw = pw,
                                isManager = isManager.value,
                                navController = navController
                            )
                        }
                    )
                    LoginFunctionButton(
                        modifier = Modifier.weight(1f),
                        buttonText = "회원가입",
                        onClick = { navController.navigate("SignUpScreen") }
                    )
                    LoginFunctionButton(
                        modifier = Modifier.weight(1f),
                        buttonText = "계정 찾기",
                        onClick = { navController.navigate("FindIdScreen") }
                    )
                }
            }
            AppNameText()
        }
    }
}
