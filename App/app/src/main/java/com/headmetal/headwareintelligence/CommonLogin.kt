package com.headmetal.headwareintelligence

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

@Preview(showBackground = true)
@Composable
fun HelmetImagePreview() {
    HelmetImage()
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

@Composable
fun HelmetImage() {
    Image(
        painter = painterResource(id = R.drawable.helmet),
        contentDescription = null
    )
}

//

@Composable
fun AppNameText() {
    Text(
        text = stringResource(id = R.string.app_name),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LoginFieldLabel(text: String = "") {
    BoldTextField(text = text, fontSize = 16.sp)
}

@Composable
fun LoginFunctionButtonComposable(
    vararg loginFunctionButtons: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
    ) {
        Row {
            loginFunctionButtons.forEach { loginFunctionButton ->
                loginFunctionButton()
            }
        }
    }
}
