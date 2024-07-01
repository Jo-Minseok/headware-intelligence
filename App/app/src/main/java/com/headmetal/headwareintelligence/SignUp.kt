package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

data class RegisterInputModel(
    val id: String,
    val password: String,
    val rePassword: String,
    val name: String,
    val email: String,
    val phoneNo: String,
    val company: String?,
    val type: String
)

data class CompanyList(
    val companies: List<String>
)

@Composable
fun SignUp(navController: NavController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        LoadingScreen()
        SignUpComposable(navController = navController)
    }
}

@Composable
fun SignUpComposable(navController: NavController = rememberNavController()) {
    val id: MutableState<String> = remember { mutableStateOf("") }
    val pw: MutableState<String> = remember { mutableStateOf("") }
    val rePw: MutableState<String> = remember { mutableStateOf("") }
    val name: MutableState<String> = remember { mutableStateOf("") }
    val phone: MutableState<String> = remember { mutableStateOf("") }
    val email: MutableState<String> = remember { mutableStateOf("") }
    val selectCompany: MutableState<String> = remember { mutableStateOf("없음") }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

    var selectableCompany by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    response.body()?.let { selectableCompany = it.companies }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
            }
        })
    }

    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HelmetImage()
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "아이디") },
            inputTextField = { InputTextField(inputText = id) }
        )
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "비밀번호") },
            inputTextField = {
                InputTextField(
                    inputText = pw,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        )
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "비밀번호 확인") },
            inputTextField = {
                InputTextField(
                    inputText = rePw,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        )
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "이름") },
            inputTextField = {
                InputTextField(
                    inputText = name,
                    placeholder = { Text("4글자 이내") }
                )
            }
        )
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "전화번호") },
            inputTextField = { InputTextField(inputText = phone) }
        )
        LoginTextFieldComposable(
            fieldLabel = { LoginFieldLabel(text = "이메일") },
            inputTextField = {
                InputTextField(
                    inputText = email,
                    placeholder = { Text("'@' 를 포함한 이메일 형식") }
                )
            }
        )
        CompanyDropdownMenuComposable(
            fieldLabel = { LoginFieldLabel(text = "건설업체") },
            companyDropdownMenu = {
                CompanyDropdownMenu(
                    expanded,
                    selectCompany,
                    selectableCompany
                )
            }
        )
        CustomRadioButtonComposable(
            fieldLabel = { LoginFieldLabel(text = "직무") },
            customRadioButtonGroup = { CustomRadioButtonGroup(isEmployee, isManager) }
        )
        LoginFunctionButtonComposable(
            loginFunctionButtons = arrayOf(
                {
                    LoginFunctionButton(buttonText = "회원가입") {
                        if (pw.value != rePw.value) {
                            showAlertDialog(
                                context = navController.context,
                                title = "비밀번호 불일치",
                                message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                                buttonText = "확인"
                            )
                        } else {
                            LoadingState.show()
                            RetrofitInstance.apiService.apiRegister(
                                RegisterInputModel(
                                    id.value,
                                    pw.value,
                                    rePw.value,
                                    name.value,
                                    email.value,
                                    phone.value,
                                    if (selectCompany.value == "없음") null else selectCompany.value,
                                    if (isManager.value) "manager" else "employee"
                                )
                            ).enqueue(object : Callback<RegisterInputModel> {
                                override fun onResponse(
                                    call: Call<RegisterInputModel>,
                                    response: Response<RegisterInputModel>
                                ) {
                                    if (response.isSuccessful) {
                                        showAlertDialog(
                                            context = navController.context,
                                            title = "회원가입 성공",
                                            message = "로그인 화면으로 이동합니다.",
                                            buttonText = "확인"
                                        ) { navController.navigate("LoginScreen") }
                                    } else {
                                        showAlertDialog(
                                            context = navController.context,
                                            title = "회원가입 실패",
                                            message = "이미 존재하는 회원 또는 잘못된 정보입니다.",
                                            buttonText = "확인"
                                        )
                                    }
                                    LoadingState.hide()
                                }

                                override fun onFailure(call: Call<RegisterInputModel>, t: Throwable) {
                                    LoadingState.hide()
                                    Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
                                }
                            })
                        }
                    }
                }
            )
        )
    }
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUp()
}

@Preview(showBackground = true)
@Composable
fun SignUpComposablePreview() {
    SignUpComposable()
}

@Preview(showBackground = true)
@Composable
fun SignUpHelmetImagePreview() {
    HelmetImage()
}

@Preview(showBackground = true)
@Composable
fun SignUpTextFieldComposablePreview() {
    LoginTextFieldComposable(
        fieldLabel = { LoginFieldLabel(text = "아이디") },
        inputTextField = { InputTextField() }
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpFieldLabelPreview() {
    LoginFieldLabel(text = "아이디")
}

@Preview(showBackground = true)
@Composable
fun SignUpInputTextFieldPreview() {
    InputTextField()
}

@Preview(showBackground = true)
@Composable
fun SignUpTextFieldPlaceHolderComposablePreview() {
    LoginTextFieldComposable(
        fieldLabel = { LoginFieldLabel(text = "이름") },
        inputTextField = { InputTextField(placeholder = { Text("4글자 이내") }) }
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpInputTextFieldPlaceHolderPreview() {
    InputTextField(placeholder = { Text("4글자 이내") })
}

@Preview(showBackground = true)
@Composable
fun SignUpCompanyDropdownMenuComposablePreview() {
    CompanyDropdownMenuComposable(
        fieldLabel = { LoginFieldLabel(text = "건설업체") },
        companyDropdownMenu = { CompanyDropdownMenu() }
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpCompanyDropdownMenuPreview() {
    CompanyDropdownMenu()
}

@Preview(showBackground = true)
@Composable
fun SignupCustomRadioButtonComposablePreview() {
    CustomRadioButtonComposable(
        fieldLabel = { LoginFieldLabel(text = "직무") },
        customRadioButtonGroup = { CustomRadioButtonGroup() }
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpCustomRadioButtonGroupPreview() {
    CustomRadioButtonGroup()
}

@Preview(showBackground = true)
@Composable
fun SignUpCustomRadioButtonSinglePreview() {
    CustomRadioButtonSingle(buttonText = "일반직")
}

@Preview(showBackground = true)
@Composable
fun SignUpFunctionButtonPreview() {
    LoginFunctionButton(buttonText = "회원가입")
}
