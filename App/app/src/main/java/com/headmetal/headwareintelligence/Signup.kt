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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.RadioButton
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
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
data class RegisterInputModel(
    val id: String,
    val password: String,
    val re_password: String,
    val name: String,
    val email: String,
    val phone_no: String,
    val company: String?,
    val type:String
)

data class CompanyList(
    val companies: List<String>
)

// 회원가입 함수
fun performSignup(id: String, password: String, re_password: String, name: String,
    email: String, phone_no: String, company: String, isManager: Boolean, navController: NavController
) {
    if (password != re_password) {
        showPasswordMismatchDialog(navController)
        return
    }
    val companyToSend = if(company=="없음") null else company
    val apiService = RetrofitInstance.apiService
    val call = apiService.apiRegister(
        RegisterInputModel(id, password, re_password, name, email, phone_no,companyToSend,if (isManager) "manager" else "employee")
    )
    call.enqueue(object : Callback<RegisterInputModel>
    {
        override fun onResponse(call: Call<RegisterInputModel>, response: Response<RegisterInputModel>
        ) {
            if (response.isSuccessful) {
                // 회원 가입 성공 시
                showSignupSuccessDialog(navController)
            } else {
                // 회원 가입 실패 시 처리할 코드
                showSignupFailedDialog(navController)
            }
        }

        override fun onFailure(call: Call<RegisterInputModel>
                               , t: Throwable) {
            // 통신 실패 시 처리할 코드
            Log.e("HEAD METAL", t.message.toString())
        }
    })
}

// 비밀번호 일치 불일치 확인 함수
private fun showPasswordMismatchDialog(navController: NavController) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("비밀번호 불일치")
    builder.setMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.")

    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

//회원가입 실패 다이얼로그
fun showSignupFailedDialog(navController: NavController) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("회원가입 실패")
    builder.setMessage("이미 존재하는 회원 또는 잘못된 정보입니다.")

    // 확인 버튼 설정
    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
    }

    // 다이얼로그 표시
    val dialog = builder.create()
    dialog.show()
}

// 회원가입 성공 다이얼로그
fun showSignupSuccessDialog(navController: NavController) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("회원가입 성공")
    builder.setMessage("로그인 화면으로 이동합니다.")

    // 확인 버튼 설정
    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
        navController.navigate("loginScreen")
    }

    // 다이얼로그 표시
    val dialog = builder.create()
    dialog.show()
}

// 회원가입 화면
@Composable
fun Signup(navController: NavController, modifier: Modifier = Modifier) {
    var id by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var re_password by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var phone_no by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }
    var Company by remember { mutableStateOf("없음") }


    var isManager by remember {
        mutableStateOf(false)
    }

    var companies:List<String> = listOf("없음")

    val apiService = RetrofitInstance.apiService
    val call = apiService.getCompanyList()
    call.enqueue(object : Callback<CompanyList>
    {
        override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
            if (response.isSuccessful) {
                val companyList: CompanyList? = response.body()
                companyList?.let{
                    companies = companies + it.companies
                }
            }
        }

        override fun onFailure(p0: Call<CompanyList>, t: Throwable) {
            println("서버 통신 실패: ${t.message}")
        }
    })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.helmet),
                contentDescription = null
            )
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "아이디",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "비밀번호",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "비밀번호 확인",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = re_password,
                    onValueChange = { re_password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "이름",
                    fontWeight = FontWeight.Bold
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
                    placeholder = { // 워터마크로 사용할 힌트 텍스트
                        Text("4글자 이내")
                    },
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "전화번호",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = phone_no,
                    onValueChange = { phone_no = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "이메일",
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    placeholder = { // 워터마크로 사용할 힌트 텍스트
                        Text(" '@' 를 포함한 이메일 형식")
                    },
                    modifier = Modifier
                        .alpha(0.6f)
                        .width(350.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "건설업체",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = Company.takeUnless { it.isEmpty() } ?: "선택하세요",
                        modifier = Modifier
                            .clickable(onClick = { expanded = true })
                            .background(Color(1f, 1f, 1f, 0.4f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .width(350.dp)
                            .height(30.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        companies.forEach { company ->
                            DropdownMenuItem(
                                onClick = {
                                    Company = company
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
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "직무",
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        Row {
                            RadioButton(
                                selected = !isManager,
                                onClick = { isManager = false }
                            )
                            Text(
                                text = "일반직",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        Row {
                            RadioButton(
                                selected = isManager,
                                onClick = { isManager = true }
                            )
                            Text(
                                text = "관리직",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    //DB에 이미 존재하는 ID, E-mail 체크, 입력한 비밀번호와 비밀번호 확인이 일치한지 체크
                    //입력된 내용에 무결성이 존재하지 않을 경우 입력된 정보를 DB에 추가
                    performSignup(id, password, re_password, name, email, phone_no, Company, isManager, navController)
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