package com.headmetal.headwareintelligence

import android.app.Activity
import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
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
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

enum class SituationCode {
    COMPLETE, PROCESSING, MALFUNCTION, REPORT119 // 처리 완료 : 0, 처리 중 : 1, 오작동 : 2, 119 신고 : 3
}

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response?
        var attempt = 0
        val exception: IOException? = null

        while (attempt < maxRetries) {
            try {
                attempt++
                response = chain.proceed(request)
                if (response.isSuccessful) {
                    return response
                }
            } catch (e: SocketTimeoutException) {
                Log.e("HEAD METAL", "서버 통신 재시도 ${attempt}회")
            }
        }

        throw exception ?: IOException("Unknown error")
    }
}

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
