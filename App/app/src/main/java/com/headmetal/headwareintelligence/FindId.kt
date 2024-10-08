package com.headmetal.headwareintelligence

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
                        textFieldmodifier = Modifier.alpha(0.6f),
                        placeholder = "4글자 이내"
                    )
                    LabelAndInputComposable(
                        labelText = "이메일",
                        inputText = email,
                        textFieldmodifier = Modifier.alpha(0.6f),
                        placeholder = "'@' 를 포함한 이메일 형식"
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
                                idSearchVerify(
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

fun idSearchVerify(name: String, email: String, isManager: Boolean, navController: NavController) {
    when {
        !isNameValid(name) -> showAlertDialog(
            context = navController.context,
            title = "이름 글자 수 불일치",
            message = "이름을 4자리 이하로 작성바랍니다.",
            buttonText = "확인"
        )

        !isEmailValid(email) -> showAlertDialog(
            context = navController.context,
            title = "이메일 형식 불일치",
            message = "이메일 형식이 일치하지 않습니다.\nex)XXX@XXX.XXX(공백 제외)",
            buttonText = "확인"
        )

        else -> idSearchPOST(name, email, isManager, navController)
    }
}