package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

// 프리뷰
@Preview(showBackground = true)
@Composable
fun FindPwPreview() {
    FindPw(navController = rememberNavController())
}

@Composable
fun FindPw(navController: NavController) {
    LoginScreen(
        content = {
            val id: MutableState<String> = remember { mutableStateOf("") }
            val phone: MutableState<String> = remember { mutableStateOf("") }
            val pw: MutableState<String> = remember { mutableStateOf("") }
            val rePw: MutableState<String> = remember { mutableStateOf("") }
            val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
            val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HelmetImage()
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    LabelAndInputComposable(
                        labelText = "아이디",
                        inputText = id,
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                    LabelAndInputComposable(
                        labelText = "전화번호",
                        inputText = phone,
                        textFieldmodifier = Modifier.alpha(0.6f),
                        placeholder = "XXX-XXXX-XXXX"
                    )
                    LabelAndInputComposable(
                        labelText = "새 비밀번호",
                        inputText = pw,
                        visualTransformation = PasswordVisualTransformation(),
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                    LabelAndInputComposable(
                        labelText = "비밀번호 확인",
                        inputText = rePw,
                        visualTransformation = PasswordVisualTransformation(),
                        textFieldmodifier = Modifier.alpha(0.6f)
                    )
                    LabelAndRadioButtonComposable(
                        labelText = "직무",
                        firstButtonSwitch = isEmployee,
                        secondButtonSwitch = isManager,
                        firstButtonText = "일반직",
                        secondButtonText = "관리직"
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                LoginFunctionButton(
                    buttonText = "비밀번호 변경",
                    onClick = {
                        changePasswordVerify(
                            pw = pw.value,
                            rePw = rePw.value,
                            id = id.value,
                            phone = phone.value,
                            isManager = isManager.value,
                            navController = navController
                        )
                    }
                )
                AppNameText()
            }
        }
    )
}

fun changePasswordVerify(
    pw: String,
    rePw: String,
    id: String,
    phone: String,
    isManager: Boolean,
    navController: NavController
) {
    when {
        !isPhoneValid(phone) -> showAlertDialog(
            context = navController.context,
            title = "전화번호 형식 불일치",
            message = "전화번호 형식이 일치하지 않습니다.\nex)XXX-XXXX-XXXX",
            buttonText = "확인"
        )

        !isPasswordValid(pw) -> showAlertDialog(
            context = navController.context,
            title = "비밀번호 형식 불일치",
            message = "비밀번호는 최소 1개의 알파벳, 1개의 숫자, 1개의 특수문자가 포함되어야 하며, 6자리 이상 16자리 이하이어야 합니다.\n" +
                    "사용가능 특수 문자: @\$!%*?&",
            buttonText = "확인"
        )

        !arePasswordsMatching(pw, rePw) -> showAlertDialog(
            context = navController.context,
            title = "비밀번호 불일치",
            message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
            buttonText = "확인"
        )

        else -> changePassword(
            pw = pw,
            rePw = rePw,
            id = id,
            phone = phone,
            isManager = isManager,
            navController = navController
        )
    }
}

fun changePassword(
    pw: String,
    rePw: String,
    id: String,
    phone: String,
    isManager: Boolean,
    navController: NavController
) {
    if (pw != rePw && pw.isEmpty()) {
        LoadingState.show()
        RetrofitInstance.apiService.apiChangePw(
            ForgotPw(
                id,
                phone,
                pw,
                rePw,
                if (isManager) "manager" else "employee"
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
                        buttonText = "확인",
                        onButtonClick = { navController.navigate("LoginScreen") }
                    )
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
                networkErrorFinishApp(navController = navController, error = t)
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
