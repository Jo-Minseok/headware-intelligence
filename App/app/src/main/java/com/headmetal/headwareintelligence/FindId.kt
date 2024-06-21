package com.headmetal.headwareintelligence

import android.app.AlertDialog
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ForgotIdRequest(
    val name: String,
    val email: String,
    val type: String
)

data class ForgotIdResult(
    val id: String
)

fun performFindID(name: String, email: String, isManager: Boolean, navController: NavController) {
    val builder = AlertDialog.Builder(navController.context)

    LoadingState.show()
    RetrofitInstance.apiService.apiFindId(
        ForgotIdRequest(
            name,
            email,
            if (isManager) "manager" else "employee"
        )
    ).enqueue(object : Callback<ForgotIdResult> {
        override fun onResponse(
            call: Call<ForgotIdResult>,
            response: Response<ForgotIdResult>
        ) {
            if (response.isSuccessful) {
                val idResponse = response.body()
                val id = idResponse?.id
                if (!id.isNullOrEmpty()) {
                    builder.setTitle("아이디")
                    builder.setMessage("ID: $id")
                } else {
                    builder.setTitle("서버 응답 실패")
                    builder.setMessage("서버 응답에 실패 하였습니다.")
                }
            } else {
                builder.setTitle("아이디 찾기 실패")
                builder.setMessage("일치하는 계정을 찾을 수 없습니다.")
            }
            builder.setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
            LoadingState.hide()
        }

        override fun onFailure(call: Call<ForgotIdResult>, t: Throwable) {
            Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
            LoadingState.hide()
        }
    })
}

@Composable
fun FindId(navController: NavController) {
    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var isManager by remember {
        mutableStateOf(false)
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
                    onValueChange = { name = it },
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
            Row {
                Button(
                    onClick = { performFindID(name, email, isManager, navController) },
                    colors = ButtonDefaults.buttonColors(Color(0x59000000)),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "아이디 찾기",
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { navController.navigate("findpwScreen") },
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
                text = stringResource(id = R.string.app_name),
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
