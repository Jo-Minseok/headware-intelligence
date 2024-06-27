package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
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
fun Signup(navController: NavController = rememberNavController()) {
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        LoadingScreen()
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .verticalScroll(rememberScrollState()),
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
                FieldLabel(text = "비밀번호")
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
                FieldLabel(text = "이름")
                CustomTextField(
                    inputText = name,
                    placeholder = { Text("4글자 이내") }
                )
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
                FieldLabel(text = "이메일")
                CustomTextField(
                    inputText = email,
                    placeholder = { Text("'@' 를 포함한 이메일 형식") }
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                FieldLabel(text = "건설업체")
                CompanyDropdownMenu(expanded, selectCompany)
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
            PerformButton(
                modifier = Modifier.padding(vertical = 16.dp),
                buttonText = "회원가입"
            ) {
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
                                ) { navController.navigate("loginScreen") }
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDropdownMenu(
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    selectedCompany: MutableState<String> = remember { mutableStateOf("없음") }
) {
    var selectableCompany by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val companyList: CompanyList? = response.body()
                    companyList?.let {
                        selectableCompany = it.companies
                    }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
            }
        })
    }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .alpha(0.6f),
            value = selectedCompany.value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            modifier = Modifier.background(Color.White),
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            selectableCompany.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        expanded.value = false
                        selectedCompany.value = item
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    Signup()
}

@Preview(showBackground = true)
@Composable
fun CompanyDropdownMenuPreview() {
    CompanyDropdownMenu()
}
