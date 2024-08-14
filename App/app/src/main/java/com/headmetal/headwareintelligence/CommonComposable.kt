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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

enum class SituationCode {
    COMPLETE, PROCESSING, MALFUNCTION, REPORT119
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

/**
 * 로딩 상태
 */
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

/**
 * 로딩 화면
 */
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

/**
 * 두 번 누를 경우 프로세스 종료 재반복
 * 재반복 쿨타임: 0.8초
 */
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

/**
 * 뒤로가기 버튼 + 화면
 */
@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    Screen(navController = rememberNavController(), content = {
        ScreenTitleText(text = "제목")
    })
}

@Composable
fun Screen(navController: NavController, content: @Composable () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column(modifier = Modifier.padding(top = 30.dp, start = 20.dp, end = 20.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .clickable { navController.navigateUp() }
            )
            Surface(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
                color = Color.Transparent
            ) {
                content()
            }
        }
    }
}

/**
 * 화면 제목
 */
@Preview(showBackground = true)
@Composable
fun ScreenTitleTextPreivew() {
    ScreenTitleText("test")
}

@Composable
fun ScreenTitleText(
    text: String
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

/**
 * 다이얼로그 제목
 */
@Preview(showBackground = true)
@Composable
fun AlertTitleTextPreivew() {
    AlertTitleText(text = "test")
}

@Composable
fun AlertTitleText(
    text: String
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    )
}

/**
 * 예, 아니오 다이얼로그
 */
@Preview
@Composable
fun YesNoAlertDialogPreview() {
    YesNoAlertDialog(
        title = "Title",
        textComposable = { Text(text = "test") },
        yesButton = "yesTest",
        noButton = "noTest"
    )
}

@Composable
fun YesNoAlertDialog(
    title: String,
    textComposable: @Composable () -> Unit = {},
    confirmButton: () -> Unit = {},
    dismissButton: () -> Unit = {},
    yesButton: String,
    noButton: String
) {
    AlertDialog(
        onDismissRequest = dismissButton,
        title = { Text(text = title) },
        text = textComposable,
        confirmButton = { TextButton(onClick = confirmButton) { Text(text = yesButton) } },
        dismissButton = { TextButton(onClick = dismissButton) { Text(text = noButton) } }
    )
}


/**
 * 오직 'Yes' 다이얼로그
 */
@Preview(showBackground = true)
@Composable
fun OnlyYesAlertDialogPreview() {
    OnlyYesAlertDialog(
        title = "Title",
        textComposable = { Text(text = "test") },
        yesButton = "yesTest"
    )
}

@Composable
fun OnlyYesAlertDialog(
    title: String,
    textComposable: @Composable () -> Unit,
    confirmButton: () -> Unit = {},
    yesButton: String = "확인",
    dismissButton: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = dismissButton,
        title = { Text(text = title) },
        text = textComposable,
        confirmButton = { TextButton(onClick = confirmButton) { Text(text = yesButton) } },
    )
}

/**
 * Label 아래에 텍스트 필드
 */
@Preview(showBackground = true)
@Composable
fun LabelAndInputComposablePreview() {
    LabelAndInputComposable(
        labelText = "test", inputText = remember {
            mutableStateOf("test")
        },
        placeholder = "placeholerTest"
    )
}

@Composable
fun LabelAndInputComposable(
    labelText: String,
    modifier: Modifier = Modifier,
    inputText: MutableState<String>,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = "",
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(text = labelText)
        TextField(
            modifier = modifier,
            value = inputText.value,
            onValueChange = { inputText.value = it },
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            colors = colors,
            visualTransformation = visualTransformation,
            placeholder = { Text(text = placeholder) },
            leadingIcon = leadingIcon,  // leadingIcon이 null이 아니면 추가
            trailingIcon = trailingIcon  // trailingIcon이 null이 아니면 추가
        )
    }
}

/**
 * 굵은 폰트
 */
@Preview
@Composable
fun BoldFieldLabelPreview() {
    BoldTextField(text = "test", fontSize = 20.sp)
}

@Composable
fun BoldTextField(
    text: String,
    fontSize: TextUnit
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize
    )
}

/**
 * Label & DropDownMenu
 */
@Preview(showBackground = true)
@Composable
fun LabelAndDropdownMenuPreview() {
    LabelAndDropdownMenu(
        fieldText = "test",
        expanded = remember { mutableStateOf(false) },
        selectedItem = remember { mutableStateOf("없음") },
        selectableItems = listOf("hello", "없음")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelAndDropdownMenu(
    fieldText: String,
    expanded: MutableState<Boolean>,
    selectedItem: MutableState<String>,
    selectableItems: List<String>,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
) {
    Column {
        Text(text = fieldText)
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            TextField(
                modifier = Modifier,
                value = selectedItem.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                shape = MaterialTheme.shapes.medium,
                colors = colors
            )
            ExposedDropdownMenu(
                modifier = Modifier.background(Color.White),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                selectableItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            expanded.value = false
                            selectedItem.value = item
                        }
                    )
                }
            }
        }
    }
}

/**
 * Date 입력 부분
 */
@Preview(showBackground = true)
@Composable
fun DatePreview(){
    Date(labelText = "test", inputText = remember{ mutableStateOf("")})
}

@Composable
fun Date(labelText: String, inputText: MutableState<String>) {
    LabelAndInputComposable(
        labelText = labelText,
        inputText = inputText,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "달력"
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(255, 150, 0, 80),
            unfocusedContainerColor = Color(255, 150, 0, 80),
            disabledContainerColor = Color(255, 150, 0, 80),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

/**
 * -------------------------------------------------------------------------------------------------------
 */

@Preview(showBackground = true)
@Composable
fun FunctionButtonPreview() {
    FunctionButton(buttonText = "test")
}

@Composable
fun FunctionButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    content: @Composable RowScope.() -> Unit = {
        Text(
            text = buttonText,
            fontWeight = FontWeight.Bold
        )
    },
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        content = content,
        colors = colors,
        elevation = elevation,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
fun ProgressFunctionButtonPreview() {
    ProgressFunctionButton(buttonText = "test")
}

@Composable
fun ProgressFunctionButton(
    buttonText: String,
    additional: @Composable () -> Unit = { ProgressIcon() },
    onClick: () -> Unit = {}
) {
    FunctionButton(
        modifier = Modifier
            .fillMaxWidth(),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = buttonText, color = Color.Black, fontSize = 22.sp)
                Spacer(modifier = Modifier.weight(1f))
                additional()
            }
        },
        buttonText = buttonText,
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        onClick = onClick
    )
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
    modifier: Modifier = Modifier
) {
    ClickableIcon(
        modifier = modifier.padding(top = 20.dp),
        imageVector = Icons.Default.Menu
    )
}

@Composable
fun BackIcon(
    modifier: Modifier = Modifier
) {
    ClickableIcon(
        modifier = modifier.padding(20.dp),
        imageVector = Icons.Default.ArrowBackIosNew
    )
}

@Composable
fun ProgressIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = null
    )
}

@Composable
fun LabelIcon(
    imageVector: ImageVector,
    color: Color = Color.Black
) {
    Icon(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(40.dp),
        imageVector = imageVector,
        contentDescription = null,
        tint = color
    )
}

@Composable
fun LabelText(
    text: String = "",
    color: Color = Color.Black
) {
    Text(
        modifier = Modifier.padding(end = 10.dp),
        text = text,
        color = color,
        fontSize = 20.sp
    )
}