package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    Surface(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            LabelAndInputComposable(
                labelText = "아이디",
                inputText = id,
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "비밀번호",
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
            LabelAndInputComposable(
                labelText = "이름",
                inputText = name,
                placeholder = "4글자 이내",
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "전화번호",
                inputText = phone,
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "이메일",
                inputText = email,
                placeholder = "'@' 를 포함한 이메일 형식",
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndDropdownMenu(
                fieldText = "건설업체",
                expanded = expanded,
                selectedItem = selectCompany,
                selectableItems = selectableCompany,
                modifier = Modifier.alpha(0.6f)
            )
            LabelAndRadioButtonComposable(
                labelText = "직무",
                firstButtonSwitch = isEmployee,
                secondButtonSwitch = isManager,
                firstButtonText = "일반직",
                secondButtonText = "관리직"
            )
            Button(
                onClick = {
                    if (pw.value != rePw.value) {
                        showAlertDialog(
                            context = navController.context,
                            title = "비밀번호 불일치",
                            message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                            buttonText = "확인"
                        )
                    } else {
                        register(
                            id = id.value,
                            pw = pw.value,
                            rePw = rePw.value,
                            name = name.value,
                            email = email.value,
                            phone = phone.value,
                            selectCompany = selectCompany.value,
                            isManager = isManager.value,
                            navController = navController
                        )
                    }
                },
                shape = MaterialTheme.shapes.small,
                content = { Text("회원가입") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x59000000))
            )
        }
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
fun SignUpHelmetImagePreview() {
    HelmetImage()
}

@Preview(showBackground = true)
@Composable
fun SignUpCompanyDropdownMenuComposablePreview() {
    LabelAndDropdownMenu(
        fieldText = "건설업체",
        expanded = remember { mutableStateOf(false) },
        selectedItem = remember { mutableStateOf("") },
        selectableItems = listOf("")
    )
}

fun register(
    id: String,
    pw: String,
    rePw: String,
    name: String,
    email: String,
    phone: String,
    selectCompany: String,
    isManager: Boolean,
    navController: NavController
) {
    LoadingState.show()
    RetrofitInstance.apiService.apiRegister(
        RegisterInputModel(
            id,
            pw,
            rePw,
            name,
            email,
            phone,
            if (selectCompany == "없음") null else selectCompany,
            if (isManager) "manager" else "employee"
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

        override fun onFailure(
            call: Call<RegisterInputModel>,
            t: Throwable
        ) {
            LoadingState.hide()
            Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
        }
    })
}