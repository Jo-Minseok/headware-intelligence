package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    val id: MutableState<String> = remember { mutableStateOf("") }
    val pw: MutableState<String> = remember { mutableStateOf("") }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

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
            HelmetImage()
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "ID")
                CustomTextField(inputText = id)
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "PW")
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
                FieldLabel(text = "Part")
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
                                response: Response<LoginResponse>
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
            AppNameText(modifier = Modifier.padding(top = 20.dp))
        }
    }
}

@Composable
fun FieldLabel(
    text: String = ""
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

@Composable
fun CustomTextField(
    inputText: MutableState<String> = remember { mutableStateOf("") },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: @Composable (() -> Unit)? = null
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.6f),
        value = inputText.value,
        onValueChange = { inputText.value = it },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        visualTransformation = visualTransformation,
        placeholder = placeholder
    )
}

@Composable
fun DistinguishButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    firstButtonSwitch: MutableState<Boolean> = remember { mutableStateOf(false) },
    secondButtonSwitch: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Button(
        modifier = modifier,
        onClick = {
            firstButtonSwitch.value = true
            secondButtonSwitch.value = false
        },
        shape = RoundedCornerShape(8.dp),
        content = { Text(text = buttonText, color = Color.Black) },
        colors = ButtonDefaults.buttonColors(
            if (firstButtonSwitch.value) Color(0xDFFFFFFF) else Color(0x5FFFFFFF)
        )
    )
}

@Composable
fun FunctionButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = null,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        content = { Text(text = buttonText, fontWeight = FontWeight.Bold) },
        colors = colors,
        elevation = elevation,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
    )
}

@Composable
fun LoginFunctionButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    onClick: () -> Unit = {}
) {
    FunctionButton(
        modifier = modifier,
        buttonText = buttonText,
        colors = ButtonDefaults.buttonColors(Color(0x59000000)),
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}

@Preview(showBackground = true)
@Composable
fun FieldLabelPreview() {
    FieldLabel(text = "테스트")
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    CustomTextField()
}

@Preview(showBackground = true)
@Composable
fun DistinguishButtonPreview() {
    DistinguishButton(buttonText = "테스트")
}

@Preview(showBackground = true)
@Composable
fun FunctionButtonPreview() {
    FunctionButton(buttonText = "테스트", colors = ButtonDefaults.buttonColors(Color(0x59000000))) {}
}

@Preview(showBackground = true)
@Composable
fun LoginFunctionButtonPreview() {
    LoginFunctionButton(buttonText = "테스트") {}
}
