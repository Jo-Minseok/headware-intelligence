package com.headmetal.headwareintelligence

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelmetImage() {
    Image(
        painter = painterResource(id = R.drawable.helmet),
        contentDescription = null
    )
}

@Composable
fun AppNameText() {
    Text(
        text = stringResource(id = R.string.app_name),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LoginFieldLabel(text: String = "") {
    BoldFieldLabel(text = text, fontSize = 16.sp)
}

@Composable
fun LoginTextFieldComposable(
    fieldLabel: @Composable () -> Unit,
    inputTextField: @Composable () -> Unit
) {
    TextFieldComposable(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp),
        fieldLabel = fieldLabel,
        inputTextField = inputTextField
    )
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
fun LoginFunctionButtonComposable(
    vararg loginFunctionButtons: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
    ) {
        Row {
            loginFunctionButtons.forEach { loginFunctionButton ->
                loginFunctionButton()
            }
        }
    }
}

@Composable
fun CustomRadioButtonSingle(
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
        CustomRadioButtonSingle(
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            buttonText = "일반직",
            firstButtonSwitch = firstButtonSwitch,
            secondButtonSwitch = secondButtonSwitch,
        )
        Spacer(modifier = Modifier.width(20.dp))
        CustomRadioButtonSingle(
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
fun CustomRadioButtonComposable(
    fieldLabel: @Composable () -> Unit,
    customRadioButtonGroup: @Composable () -> Unit
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
