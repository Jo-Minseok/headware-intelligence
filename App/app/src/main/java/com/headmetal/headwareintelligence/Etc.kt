package com.headmetal.headwareintelligence

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController

@Composable
fun HyperlinkText(text: String, onClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation("URL", "clickable")
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(text)
        }
        pop()
    }

    ClickableText(text = annotatedString, onClick = {
        onClick()
    })
}

@Composable
fun AlertDialogWithHyperlinks(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable (() -> Unit)? = null
) {
    if (confirmButton != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = title,
            text = text,
            confirmButton = confirmButton
        )
    }
}

@Composable
fun Etc(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    var infoDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() })
            Text(
                text = "기타",
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Spacer(modifier = Modifier.width(125.dp))
            Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                Button(
                    onClick = { infoDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 30.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Row {
                        Text(
                            text = "개발자",
                            color = Color.Black,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null
                        )
                    }
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 30.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Row {
                        Text(
                            text = "버전 정보",
                            color = Color.Black,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "1.0.0",
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                }
                Button(
                    onClick = { navController.navigate("LicenseScreen") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 30.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Row {
                        Text(
                            text = "라이센스",
                            color = Color.Black,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null
                        )
                    }
                }
                Text(
                    text = "App Info",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
                Text(
                    text = "Team : Head-Metal", color = Color.Black, fontSize = 12.sp
                )
                Text(
                    text = "Site : https://github.com/Jo-Minseok/headware-intelligence\n",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/Jo-Minseok/headware-intelligence")
                    }
                )

                if (infoDialog) {
                    AlertDialogWithHyperlinks(
                        onDismissRequest = { infoDialog = false },
                        title = { Text(text = "Developers") },
                        text = {
                            Column {
                                Text(text = "조민석 (PM, APP, BE, HW)")
                                HyperlinkText("https://github.com/Jo-Minseok") {
                                    uriHandler.openUri(
                                        "https://github.com/Jo-Minseok"
                                    )
                                }
                                Text(text = "")
                                Text(text = "전진호 (BE, APP)")
                                HyperlinkText("https://github.com/right5625") {
                                    uriHandler.openUri(
                                        "https://github.com/right5625"
                                    )
                                }
                                Text(text = "")
                                Text(text = "채승룡 (APP)")
                                HyperlinkText("https://github.com/chaeseungryong") {
                                    uriHandler.openUri(
                                        "https://github.com/chaeseungryong"
                                    )
                                }

                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { infoDialog = false }) {
                                Text(text = "확인")
                            }
                        }
                    )
                }
            }
        }
    }
}