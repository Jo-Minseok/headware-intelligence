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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
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
fun TextFieldComposable(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDropdownMenu(
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    selectedCompany: MutableState<String> = remember { mutableStateOf("없음") },
    selectableCompany: List<String> = listOf("없음")
) {
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

@Composable
fun CompanyDropdownMenuComposable(
    fieldLabel: @Composable (() -> Unit),
    companyDropdownMenu: @Composable (() -> Unit)
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
    ) {
        fieldLabel()
        companyDropdownMenu()
    }
}

@Composable
fun ClickableIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector
) {
    Icon(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = null
    )
}

@Composable
fun DrawerMenuIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector
) {
    ClickableIcon(
        modifier = modifier.padding(top = 20.dp),
        imageVector = imageVector
    )
}
