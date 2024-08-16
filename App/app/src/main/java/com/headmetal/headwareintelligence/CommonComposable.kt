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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
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
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        content = {
            ScreenTitleText(text = "제목")
        }
    )
}

@Composable
fun IconScreen(
    content: @Composable () -> Unit = {},
    imageVector: ImageVector,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column(modifier = Modifier.padding(top = 30.dp, start = 20.dp, end = 20.dp)) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier
                    .clickable { onClick() }
            )
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
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
        title = { AlertTitleText(text = title) },
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
        title = { AlertTitleText(text = title) },
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
    labelFontWeight: FontWeight? = null,
    labelFontSize: TextUnit = 16.sp,
    textFieldmodifier: Modifier = Modifier,
    inputText: MutableState<String>,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = "",
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly:Boolean = false
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = labelText,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = labelFontWeight,
            fontSize = labelFontSize
        )
        TextField(
            modifier = textFieldmodifier.fillMaxWidth(),
            value = inputText.value,
            onValueChange = { inputText.value = it },
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            readOnly = readOnly,
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
    modifier: Modifier = Modifier,
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
                modifier = modifier.fillMaxWidth(),
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
fun DatePreview() {
    Date(labelText = "test", inputText = remember { mutableStateOf("") })
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
 * Font 20Size
 */
@Preview(showBackground = true)
@Composable
fun LabelTextPreview() {
    LabelText(text = "test")
}

@Composable
fun LabelText(
    text: String = "",
    color: Color = Color.Black
) {
    Text(
        modifier = Modifier,
        text = text,
        color = color,
        fontSize = 20.sp
    )
}

/**
 * IconWithLabelButton
 */
@Preview(showBackground = true)
@Composable
fun IconWithLabelButtonPreview() {
    IconWithLabelButton(
        leadIcon = Icons.Default.Description,
        text = "테스트용",
        trailingIcon = Icons.Default.ArrowForwardIos,
        onClick = {})
}

@Composable
fun IconWithLabelButton(
    leadIcon: ImageVector,
    text: String,
    trailingIcon: ImageVector = Icons.Default.ArrowForwardIos,
    color: Color = Color.Black,
    onClick: () -> Unit,
    fontSize: TextUnit = 20.sp
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        content = {
            Icon(
                imageVector = leadIcon, contentDescription = null, tint = color, modifier = Modifier
                    .padding(end = 10.dp)
                    .size(30.dp)
            )
            Text(text = text, color = color, fontSize = fontSize)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = trailingIcon, contentDescription = null, tint = color)
        },
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    )
}

/**
 * 메뉴 아이콘
 */
@Preview(showBackground = true)
@Composable
fun MenuIconPreview() {
    MenuIcon()
}

@Composable
fun MenuIcon(
    onClick: () -> Unit = {}
) {
    Icon(
        imageVector = Icons.Default.Menu,
        contentDescription = null,
        modifier = Modifier.clickable { onClick() }
    )
}

/**
 * 뒤로가기 아이콘
 */

@Preview(showBackground = true)
@Composable
fun BackIconPreview() {
    BackIcon()
}

@Composable
fun BackIcon(
    onClick: () -> Unit = {}
) {
    Icon(
        contentDescription = null,
        imageVector = Icons.Default.ArrowBackIosNew,
        modifier = Modifier.clickable { onClick() }
    )
}

/**
 * 다음 아이콘
 */
@Preview(showBackground = true)
@Composable
fun NextIconPreview() {
    NextIcon()
}

@Composable
fun NextIcon(
    onClick: () -> Unit = {}
) {
    Icon(
        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
        contentDescription = null,
        modifier = Modifier.clickable { onClick() }
    )
}

/**
 * RadioButton 값
 */

@Preview(showBackground = true)
@Composable
fun RadioButtonSinglePreview() {
    RadioButtonSingle(
        buttonText = "test",
        firstButtonSwitch = remember { mutableStateOf(false) },
        secondButtonSwitch = remember { mutableStateOf(true) }
    )
}

@Composable
fun RadioButtonSingle(
    modifier: Modifier = Modifier,
    buttonText: String,
    firstButtonSwitch: MutableState<Boolean>,
    secondButtonSwitch: MutableState<Boolean>
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

/**
 * RadioButton 2개
 */

@Preview(showBackground = true)
@Composable
fun LabelAndRadioButtonPreview() {
    LabelAndRadioButtonComposable(
        labelText = "test",
        firstButtonSwitch = remember { mutableStateOf(true) },
        secondButtonSwitch = remember { mutableStateOf(false) },
        firstButtonText = "1",
        secondButtonText = "2"
    )
}

@Composable
fun LabelAndRadioButtonComposable(
    labelText: String,
    labelFontWeight: FontWeight? = null,
    labelFontSize: TextUnit = 16.sp,
    firstButtonSwitch: MutableState<Boolean>,
    secondButtonSwitch: MutableState<Boolean>,
    firstButtonText: String,
    secondButtonText: String
) {
    Column {
        Text(
            text = labelText,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = labelFontWeight,
            fontSize = labelFontSize
        )
        Row (horizontalArrangement = Arrangement.spacedBy(20.dp)){
            RadioButtonSingle(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                buttonText = firstButtonText,
                firstButtonSwitch = firstButtonSwitch,
                secondButtonSwitch = secondButtonSwitch,
            )
            RadioButtonSingle(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                buttonText = secondButtonText,
                firstButtonSwitch = secondButtonSwitch,
                secondButtonSwitch = firstButtonSwitch,
            )
        }
    }
}

/**
 * LoginFunction Button
 */

@Preview(showBackground = true)
@Composable
fun LoginFunctionButtonPreview() {
    LoginFunctionButton(
        buttonText = "test",
        onClick = {}
    )
}

@Composable
fun LoginFunctionButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        content = { Text(text = buttonText) },
        colors = ButtonDefaults.buttonColors(Color(0x59000000)),
        onClick = onClick
    )
}

/**
 * Login Screen
 */

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

@Composable
fun LoginScreen(content: @Composable () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        Surface(
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
            color = Color.Transparent
        ) {
            content()
        }
    }
}

/**
 * Helmet Image
 */
@Preview(showBackground = true)
@Composable
fun HelmetImagePreview(){
    HelmetImage()
}

@Composable
fun HelmetImage() {
    Image(
        painter = painterResource(id = R.drawable.helmet),
        contentDescription = null
    )
}

/**
 * App Name
 */

@Preview(showBackground = true)
@Composable
fun AppNameTextPreview(){
    AppNameText()
}

@Composable
fun AppNameText() {
    Text(
        text = stringResource(id = R.string.app_name),
        fontWeight = FontWeight.Bold
    )
}

/**
 * LabelWithNextIcon
 */
@Preview(showBackground = true)
@Composable
fun LabelWithNextIconPreview() {
    LabelWithNextIcon(
        onClick = { /*TODO*/ },
        text = "test"
    )
}

@Composable
fun LabelWithNextIcon(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, modifier = Modifier.weight(1f), fontSize = 24.sp)
        NextIcon()
    }
}

/**
 * Rounded Button
 */

@Preview(showBackground = true)
@Composable
fun RoundedButtonPreview() {
    RoundedButton(
        buttonText = "test",
        colors = Color.Black
    )
}

@Composable
fun RoundedButton(
    buttonText: String,
    colors: Color,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        content = { Text(text = buttonText) },
        colors = ButtonDefaults.buttonColors(colors),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
        onClick = onClick
    )
}

/**
 * IconWithLabel
 */

@Preview(showBackground = true)
@Composable
fun IconWithLabelPreview() {
    IconWithLabel(icon = Icons.Default.NewLabel,
        iconColor = Color.Black,
        textColor = Color.Black,
        text = "test",
        onClick = {})
}

@Composable
fun IconWithLabel(
    icon: ImageVector,
    iconColor: Color,
    textColor: Color,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = textColor
        )
    }
}