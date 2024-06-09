package com.headmetal.headwareintelligence

import android.app.AlertDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

fun performSignup(
    id: String,
    password: String,
    rePassword: String,
    name: String,
    email: String,
    phoneNo: String,
    company: String,
    isManager: Boolean,
    navController: NavController
) {
    val builder = AlertDialog.Builder(navController.context)

    if (password != rePassword) {
        builder.setTitle("비밀번호 불일치")
        builder.setMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    } else {
        val companyToSend = if (company == "없음") null else company

        LoadingState.show()
        RetrofitInstance.apiService.apiRegister(
            RegisterInputModel(
                id,
                password,
                rePassword,
                name,
                email,
                phoneNo,
                companyToSend,
                if (isManager) "manager" else "employee"
            )
        ).enqueue(object : Callback<RegisterInputModel> {
            override fun onResponse(
                call: Call<RegisterInputModel>,
                response: Response<RegisterInputModel>
            ) {
                if (response.isSuccessful) {
                    builder.setTitle("회원가입 성공")
                    builder.setMessage("로그인 화면으로 이동합니다.")
                    builder.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                        navController.navigate("loginScreen")
                    }
                } else {
                    builder.setTitle("회원가입 실패")
                    builder.setMessage("이미 존재하는 회원 또는 잘못된 정보입니다.")
                    builder.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                val dialog = builder.create()
                dialog.show()
                LoadingState.hide()
            }

            override fun onFailure(call: Call<RegisterInputModel>, t: Throwable) {
                Log.e("HEAD METAL", t.message.toString())
                LoadingState.hide()
            }
        })
    }
}

@Composable
fun Signup(navController: NavController) {
    var id by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var rePassword by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var phoneNo by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var expanded by remember { mutableStateOf(false) }
    var selectCompany by remember { mutableStateOf("없음") }
    var isManager by remember {
        mutableStateOf(false)
    }
    var companies: List<String> = listOf("없음")

    RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
        override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
            if (response.isSuccessful) {
                val companyList: CompanyList? = response.body()
                companyList?.let {
                    companies = companies + it.companies
                }
            }
        }

        override fun onFailure(call: Call<CompanyList>, t: Throwable) {
            println("서버 통신 실패: ${t.message}")
        }
    })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        LoadingScreen()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.helmet),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "아이디",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "비밀번호",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "비밀번호 확인",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = rePassword,
                    onValueChange = { rePassword = it },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "이름",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 4) {
                            name = it
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    placeholder = { Text("4글자 이내") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "전화번호",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = phoneNo,
                    onValueChange = { phoneNo = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "이메일",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    placeholder = { Text("'@' 를 포함한 이메일 형식") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "건설업체",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = selectCompany.takeUnless { it.isEmpty() } ?: "없음",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expanded = true })
                            .background(Color(1f, 1f, 1f, 0.4f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(30.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        companies.forEach { company ->
                            DropdownMenuItem(
                                onClick = {
                                    selectCompany = company
                                    expanded = false
                                }
                            ) {
                                Text(text = company)
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "직무",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row {
                    Button(
                        onClick = { isManager = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        content = { Text(text = "일반직", color = Color.Black) },
                        colors = ButtonDefaults.buttonColors(
                            if (!isManager) Color(0xDFFFFFFF) else Color(
                                0x5FFFFFFF
                            )
                        )
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = { isManager = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        content = { Text(text = "관리직", color = Color.Black) },
                        colors = ButtonDefaults.buttonColors(
                            if (isManager) Color(0xDFFFFFFF) else Color(
                                0x5FFFFFFF
                            )
                        )
                    )
                }
            }
            Button(
                onClick = {
                    performSignup(
                        id,
                        password,
                        rePassword,
                        name,
                        email,
                        phoneNo,
                        selectCompany,
                        isManager,
                        navController
                    )
                },
                colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                modifier = Modifier.padding(vertical = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "회원가입",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
