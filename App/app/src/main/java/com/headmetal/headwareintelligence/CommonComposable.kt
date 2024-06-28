package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                response = chain.proceed(request)
                if (response.isSuccessful) {
                    return response
                }
            } catch (e: SocketTimeoutException) {
                Log.e("HEAD METAL", "서버 통신 재시도 ${attempt + 1}회")
                attempt++
            }
        }

        throw exception ?: IOException("Unknown error")
    }
}

fun showAlertDialog(
    context: Context,
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit = {}
) {
    val builder = android.app.AlertDialog.Builder(context)

    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(buttonText) { dialog, _ ->
        onButtonClick()
        dialog.dismiss()
    }
    builder.create().show()
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

@Composable
fun BackOnPressed() {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L

    BackHandler(enabled = backPressedState) {
        if (System.currentTimeMillis() - backPressedTime <= 800L) {
            (context as Activity).finish()
        } else {
            backPressedState = true
            Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}

@Composable
fun HelmetImage() {
    Image(
        painter = painterResource(id = R.drawable.helmet),
        contentDescription = null
    )
}

@Composable
fun AppNameText(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.app_name),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun FieldLabel(
    text: String = "",
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize
    )
}

@Composable
fun CustomTextField(
    inputText: MutableState<String> = remember { mutableStateOf("") },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: @Composable (() -> Unit)? = null
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.6f),
        value = inputText.value,
        onValueChange = { inputText.value = it },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        visualTransformation = visualTransformation,
        placeholder = placeholder
    )
}

@Composable
fun FunctionButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        content = { Text(text = buttonText, fontWeight = FontWeight.Bold) },
        colors = colors,
        elevation = elevation,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
    )
}

// 로그인, 회원가입, ID찾기, PW찾기에서 사용하는 공통 컴포저블
@Composable
fun LoginFieldLabel(text: String = "") {
    FieldLabel(text = text, fontSize = 16.sp)
}

@Composable
fun LoginFunctionButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    onClick: () -> Unit = {}
) {
    FunctionButton(
        modifier = modifier,
        buttonText = buttonText,
        colors = ButtonDefaults.buttonColors(Color(0x59000000)),
        onClick = onClick
    )
}

@Composable
fun TextFieldElement(
    fieldLabel: @Composable (() -> Unit),
    customTextField: @Composable (() -> Unit)
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
    ) {
        fieldLabel()
        customTextField()
    }
}

@Composable
fun CustomRadioButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    firstButtonSwitch: MutableState<Boolean> = remember { mutableStateOf(true) },
    secondButtonSwitch: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Button(
        modifier = modifier,
        onClick = {
            firstButtonSwitch.value = true
            secondButtonSwitch.value = false
        },
        shape = RoundedCornerShape(8.dp),
        content = { Text(text = buttonText, color = Color.Black) },
        colors = ButtonDefaults.buttonColors(
            if (firstButtonSwitch.value) Color(0xDFFFFFFF) else Color(
                0x5FFFFFFF
            )
        )
    )
}

@Composable
fun CustomRadioButtonGroup(
    firstButtonSwitch: MutableState<Boolean> = remember { mutableStateOf(true) },
    secondButtonSwitch: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Row {
        CustomRadioButton(
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            buttonText = "일반직",
            firstButtonSwitch = firstButtonSwitch,
            secondButtonSwitch = secondButtonSwitch,
        )
        Spacer(modifier = Modifier.width(20.dp))
        CustomRadioButton(
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            buttonText = "관리직",
            firstButtonSwitch = secondButtonSwitch,
            secondButtonSwitch = firstButtonSwitch,
        )
    }
}

@Composable
fun RadioButtonElement(
    fieldLabel: @Composable (() -> Unit),
    customRadioButtonGroup: @Composable (() -> Unit)
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
    ) {
        fieldLabel()
        customRadioButtonGroup()
    }
}


//
