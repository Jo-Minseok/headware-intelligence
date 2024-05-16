package com.headmetal.headwareintelligence


import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.app.AlertDialog
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 처리 상황 코드를 나타내는 열거형 클래스
enum class SituationCode {
    COMPLETE, PROCESSING, MALFUNCTION, REPORT119 // 처리 완료 : 0, 처리 중 : 1, 오작동 : 2, 119 신고 : 3
}

// 로딩 상태
object LoadingState {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun show() {
        _isLoading.value = true
    }

    fun hide() {
        _isLoading.value = false
    }
}

// 로딩 화면
@Composable
fun LoadingScreen() {
    val isLoading = LoadingState.isLoading.collectAsState().value

    if (isLoading) {
        Dialog(
            onDismissRequest = { LoadingState.hide() },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            CircularProgressIndicator()
        }
    }
}

// 두 번 뒤로 갈 경우 애플리케이션 종료
@Composable
fun BackOnPressed() {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L

    BackHandler(enabled = backPressedState) {
        if (System.currentTimeMillis() - backPressedTime <= 800L) {
            // 앱 종료
            (context as Activity).finish()
        } else {
            backPressedState = true
            Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}


// 서버로부터 받는 로그인 응답 데이터 모델 정의
data class LoginResponse(
    val id: String,
    val name: String,
    val access_token: String,
    val token_type: String
)

// 로그인 수행 함수
fun performLogin(
    username: String?,
    password: String?,
    isManager: Boolean,
    auto: SharedPreferences
): Int {
    val autoLoginEdit: SharedPreferences.Editor = auto.edit()
    val call = RetrofitInstance.apiService.API_login(
        alert_token = auto.getString("alert_token", null).toString(),
        type = if (isManager) "manager" else "employee",
        id = username,
        pw = password
    )
    var loginSuccess =0
    call.enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.isSuccessful) {
                autoLoginEdit.putString("userid", response.body()?.id)
                autoLoginEdit.putString("password", password)
                autoLoginEdit.putString("name", response.body()?.name)
                autoLoginEdit.putString("token", response.body()?.access_token)
                autoLoginEdit.putString("token_type", response.body()?.token_type)
                autoLoginEdit.putString("type", if (isManager) "manager" else "employee")
                autoLoginEdit.apply()
                loginSuccess = 0
            } else {
                loginSuccess = 1
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            loginSuccess = 2
        }
    })
    return loginSuccess
}