package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 프리뷰
@Preview(showBackground = true)
@Composable
fun EtcPreview() {
    Etc(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun EtcLabelTextPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
        LabelWithNextIcon(onClick = {}, text = "개발자")
        LabelWithNextIcon(onClick = {}, text = "라이센스")
        LabelWithNextIcon(onClick = {}, text = "버전 정보 : 1.0.0")
    }
}

@Preview(showBackground = true)
@Composable
fun EtcAppInfoTextPreview() {
    AppInfoText()
}

@Preview(showBackground = true)
@Composable
fun EtcDevelopersAlertDialog() {
    DevelopersAlertDialog(onConfirmButton = {})
}

@Composable
fun Etc(navController: NavController) {
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            var companies by remember { mutableStateOf(listOf<String>()) }
            var showDeveloperDialog by remember { mutableStateOf(false) }

            if (showDeveloperDialog) {
                DevelopersAlertDialog(onConfirmButton = { showDeveloperDialog = false })
            }

            LaunchedEffect(Unit) {
                RetrofitInstance.apiService.getCompanyList()
                    .enqueue(object : Callback<CompanyList> {
                        override fun onResponse(
                            call: Call<CompanyList>,
                            response: Response<CompanyList>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let { companies = it.companies }
                            }
                        }

                        override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                            Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
                        }
                    })
            }
            ScreenTitleText(text = "기타")
            Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                LabelWithNextIcon(
                    onClick = { showDeveloperDialog = true },
                    text = "개발자"
                )
                LabelWithNextIcon(
                    onClick = { navController.navigate("LicenseScreen") },
                    text = "라이센스"
                )
                LabelWithNextIcon(
                    onClick = {},
                    text = "버전 정보 : 1.0.0"
                )
                AppInfoText()
            }
        }
    )
}

@Composable
fun AppInfoText() {
    val uriHandler: UriHandler = LocalUriHandler.current

    Column {
        Text(
            text = "App Info",
            color = Color.Black,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Team : Head-Metal",
            color = Color.Black,
            fontSize = 12.sp
        )
        HyperlinkText(text = "https://github.com/Jo-Minseok/headware-intelligence") {
            uriHandler.openUri("https://github.com/Jo-Minseok/headware-intelligence")
        }
    }
}

@Composable
fun DevelopersAlertDialog(
    onConfirmButton: () -> Unit,
) {
    val uriHandler: UriHandler = LocalUriHandler.current

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
        dismissButton = {},
        confirmButton = onConfirmButton,
        yesButton = "확인"
    )
}


