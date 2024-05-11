package com.headmetal.headwareintelligence

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



data class EmployeeForgotPw(
    val id: String,
    val email: String
)

data class EmployeeChangePw(
    val password: String,
    val re_password: String
)

data class ManagerForgotPw(
    val id: String,
    val email: String
)

data class ManagerChangePw(
    val password: String,
    val re_password: String
)
data class EmployeePasswordChangeRequest(
    @SerializedName("user_employee") val userEmployee: EmployeeForgotPw,
    @SerializedName("pw_change") val pwChange: EmployeeChangePw
)

data class ManagerPasswordChangeRequest(
    @SerializedName("user_manager") val userManager: ManagerForgotPw,
    @SerializedName("pw_change") val pwChange: ManagerChangePw
)
data class RedirectResponse(
    @SerializedName("location") val location: String
)

fun sendPasswordChangeRequest(id: String, email: String, password: String, re_password: String,
                              isManager: Boolean, navController: NavController) {
    val apiService = RetrofitInstance.apiService
    val call = if (isManager) {
        apiService.confirmManager(ManagerForgotPw(id, email))
    } else {
        apiService.confirmEmployee(EmployeeForgotPw(id, email))
    }

    call.enqueue(object : Callback<RedirectResponse> {
        override fun onResponse(call: Call<RedirectResponse>, response: Response<RedirectResponse>) {
            if (response.isSuccessful) {
                val redirectUrl = response.body()?.location
                if (!redirectUrl.isNullOrBlank()) {
                    // 리디렉션된 주소를 받아온 경우 변경된 비밀번호를 서버에 보냄
                    sendPasswordChangeRequestAfterRedirect(id, email, password, re_password,
                        isManager, navController)
                } else {
                    println("리디렉션된 주소가 없습니다.")

                }
            } else {
                println("비밀번호 변경 요청 실패: ${response.code()}")
                val errorBody = response.errorBody()?.string()
                println("에러 응답 본문: $errorBody")

            }
        }

        override fun onFailure(call: Call<RedirectResponse>, t: Throwable) {
            println("서버 통신 실패: ${t.message}")

        }
    })
}

// 리디렉션된 주소를 받아서 변경된 비밀번호를 서버에 보내는 함수
fun sendPasswordChangeRequestAfterRedirect(id: String, email: String, password: String, re_password:String,
                                          isManager: Boolean, navController: NavController) {
    val apiService = RetrofitInstance.apiService
    val call = if (isManager) {
        apiService.changeManagerPassword(
           ManagerPasswordChangeRequest(ManagerForgotPw(id,email),
               ManagerChangePw(password, re_password)
           )
        )
    } else {
        apiService.changeEmployeePassword(
            EmployeePasswordChangeRequest(EmployeeForgotPw(id, email),
                EmployeeChangePw(password, re_password)
            )
        )
    }

    call.enqueue(object : Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            if (response.isSuccessful) {
                println("비밀번호가 성공적으로 변경되었습니다.")
                navController.navigate("loginScreen")
            } else {
                println("비밀번호 변경에 실패했습니다.")
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            println("서버 통신 실패: ${t.message}")
        }
    })
}

// 변경된 OnPasswordChangeButtonClick 함수
fun OnPasswordChangeButtonClick(id: String, email: String, password: String, re_password: String,
                                isManager: Boolean, navController: NavController) {
    if (password == re_password && password.isNotEmpty()) {
        // 새 비밀번호와 비밀번호 확인 값이 일치하고 비밀번호가 비어 있지 않은 경우에만 서버로 요청을 보냄
        sendPasswordChangeRequest(id, email, password, re_password, isManager, navController)
    } else {
        println("새 비밀번호와 비밀번호 확인이 일치하지 않거나 비밀번호가 비어 있습니다.")
    }
}






@Composable
fun Findpw(navController: NavController, modifier: Modifier = Modifier) {
    var id by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var isManager by remember {
        mutableStateOf(false)
    }
    var password by remember {
        mutableStateOf("")
    }
    var re_password by remember {
        mutableStateOf("")
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        Column(
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.alpha(0.6f).width(350.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.alpha(0.6f).width(350.dp),
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
                    text = "새 비밀번호",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.alpha(0.6f).width(350.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = re_password,
                    onValueChange = { re_password = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.alpha(0.6f).width(350.dp),
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

            Row {

                Button(
                    onClick = {OnPasswordChangeButtonClick(id, email, password, re_password, isManager, navController)},
                    colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "비밀번호 변경",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "HeadWear - Intelligence",
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}