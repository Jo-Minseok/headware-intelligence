package com.headmetal.headwareintelligence

import android.app.AlertDialog
import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Forgot_Id_Request(
    val name: String,
    val email: String,
    val type: String
)

data class Forgot_Id_Result(
    val id: String
)

fun performFindId(name: String, email: String, isManager: Boolean, navController: NavController) {
    val apiService = RetrofitInstance.apiService
    val call = apiService.API_findid(
        Forgot_Id_Request(
            name,
            email,
            if (isManager) "manager" else "employee"
        )
    )
    call.enqueue(object : Callback<Forgot_Id_Result> {
        override fun onResponse(
            call: Call<Forgot_Id_Result>,
            response: Response<Forgot_Id_Result>
        ) {
            // 서버로부터 응답을 받았을 때

            if (response.isSuccessful) {
                // 서버가 요청을 성공적으로 처리했을 때의 경우
                val idResponse = response.body()
                val id = idResponse?.id
                if (!id.isNullOrEmpty()) {
                    // ID 값이 비어 있지 않은 경우
                    showIdDialog(navController, id)
                } else {
                    // ID 값이 비어 있거나 null인 경우
                    showAccessFailedDialog(navController)
                }
            } else {
                // 서버가 요청을 처리하지 못했을 때의 경우(예: 404 Not Found, 500 Internal Server Error 등)
                showFindIdFailedDialog(navController)
            }
        }

        override fun onFailure(call: Call<Forgot_Id_Result>, t: Throwable) {
            // 서버 통신에 실패했을 때
            Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
        }
    })
}


private fun showFindIdFailedDialog(navController: NavController) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("아이디 찾기 실패")
    builder.setMessage("일치하는 계정을 찾을 수 없습니다.")

    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

fun showAccessFailedDialog(navController: NavController) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("서버 응답 실패")
    builder.setMessage("서버 응답에 실패 하였습니다.")

    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

private fun showIdDialog(navController: NavController, id: String) {
    val builder = AlertDialog.Builder(navController.context)
    builder.setTitle("아이디")
    builder.setMessage("ID: $id")

    builder.setPositiveButton("확인") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

@Composable
fun Findid(navController: NavController, modifier: Modifier = Modifier) {
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
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "이름",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    shape = RoundedCornerShape(8.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(8.dp),
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
                    onClick = { performFindId(name, email, isManager, navController) },
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