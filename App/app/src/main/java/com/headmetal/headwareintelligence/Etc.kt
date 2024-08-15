package com.headmetal.headwareintelligence

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun Etc(navController: NavController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) { EtcComposable(navController = navController) }
}

@Composable
fun EtcComposable(navController: NavController = rememberNavController()) {
    Column {
        BackIcon(onClick = { navController.navigateUp() })
        ScreenTitleText(text = "기타")
        EtcFunctions(navController = navController)
    }
}

@Composable
fun EtcFunctions(navController: NavController = rememberNavController()) {
    var showDeveloperDialog by remember { mutableStateOf(false) }

    if (showDeveloperDialog) {
        DevelopersAlertDialog { showDeveloperDialog = false }
    }

    Column(modifier = Modifier.padding(horizontal = 30.dp)) {
        LabelWithNextIcon(onClick = { showDeveloperDialog = true}, text = "개발자")
        LabelWithNextIcon(onClick = { navController.navigate("LicenseScreen")}, text = "라이센스")
        LabelWithNextIcon(onClick = {}, text = "버전 정보\n1.0.0")
        AppInfoText()
    }
}

@Composable
fun AppInfoText() {
    val uriHandler = LocalUriHandler.current

    Column {
        Text(
            text = "App Info",
            color = Color.Black,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
        )
        Text(
            text = "Team : Head-Metal",
            color = Color.Black,
            fontSize = 12.sp
        )
        Text(
            modifier = Modifier.clickable {
                uriHandler.openUri("https://github.com/Jo-Minseok/headware-intelligence")
            },
            text = "Site : https://github.com/Jo-Minseok/headware-intelligence",
            color = Color.Black,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DevelopersAlertDialog(
    dismissButton: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    OnlyYesAlertDialog(
        title = "Developers",
        textComposable = {
            Column {
                Text(text = "조민석 (PM, APP, BE, HW)")
                HyperlinkText(text = "https://github.com/Jo-Minseok\n") {
                    uriHandler.openUri("https://github.com/Jo-Minseok")
                }
                Text(text = "전진호 (BE, APP)")
                HyperlinkText(text = "https://github.com/right5625\n") {
                    uriHandler.openUri("https://github.com/right5625")
                }
                Text(text = "채승룡 (APP)")
                HyperlinkText(text = "https://github.com/chaeseungryong") {
                    uriHandler.openUri("https://github.com/chaeseungryong")
                }
            }
        },
        dismissButton = dismissButton,
        yesButton = "확인"
    )
}

@Composable
fun HyperlinkText(
    text: String = "",
    onClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation("URL", "clickable")
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) { append(text) }
        pop()
    }

    ClickableText(text = annotatedString, onClick = { onClick() })
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun EtcPreview() {
    Etc()
}

@Preview(showBackground = true)
@Composable
fun EtcComposablePreview() {
    EtcComposable()
}

@Preview(showBackground = true)
@Composable
fun EtcInfoBackIconPreview() {
    BackIcon()
}

@Preview(showBackground = true)
@Composable
fun EtcInfoTitleTextPreview() {
    ScreenTitleText(text = "기타")
}

@Preview(showBackground = true)
@Composable
fun EtcFunctionsPreview() {
    EtcFunctions()
}

@Preview(showBackground = true)
@Composable
fun EtcLabelTextPreview() {
    LabelText(text = "개발자")
}

@Preview(showBackground = true)
@Composable
fun EtcAppInfoTextPreview() {
    AppInfoText()
}

@Preview(showBackground = true)
@Composable
fun EtcDevelopersAlertDialog() {
    DevelopersAlertDialog()
}
