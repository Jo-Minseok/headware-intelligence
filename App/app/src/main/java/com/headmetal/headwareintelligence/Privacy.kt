package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PermContactCalendar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun PrivacyPreview() {
    Privacy(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PrivacyUserPreview() {
    PrivacyUser(text = "아이디", userInfo = "id", imageVector = Icons.Outlined.Person)
}

@Composable
fun Privacy(navController: NavController) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", "")
    val userName = sharedAccount.getString("name", "")
    val userPhone = sharedAccount.getString("phone", "")
    val userEmail = sharedAccount.getString("email", "")

    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            Column {
                ScreenTitleText(text = "사용자 정보")
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrivacyUser(
                        text = "아이디",
                        userInfo = userId!!,
                        imageVector = Icons.Outlined.Person
                    )
                    PrivacyUser(
                        text = "이름",
                        userInfo = userName!!,
                        imageVector = Icons.Outlined.PermContactCalendar
                    )
                    PrivacyUser(
                        text = "전화번호",
                        userInfo = userPhone!!,
                        imageVector = Icons.Outlined.Call
                    )
                    PrivacyUser(
                        text = "이메일",
                        userInfo = userEmail!!,
                        imageVector = Icons.Outlined.Mail
                    )
                    PrivacyUser(
                        text = "비밀번호",
                        userInfo = "****",
                        imageVector = Icons.Outlined.Lock
                    )
                    PrivacyUser(
                        text = "건설업체",
                        userInfo = "없음",
                        imageVector = Icons.Outlined.Business
                    ) // 추후 구현 필요
                    Button(
                        onClick = { /**TODO**/ },
                        content = {
                            Text(text = "개인 정보 변경")
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(Color(0xFF372A1F))
                    )
                }
            }
        }
    )
}

@Composable
fun PrivacyUser(
    text: String,
    userInfo: String,
    imageVector: ImageVector
) {
    Column(
        modifier = Modifier
    ) {
        Row {
            Icon(
                imageVector = imageVector,
                contentDescription = null)
            LabelText(text = text)
        }
        PrivacyUserTextField(userInfo = userInfo)
    }
}

@Composable
fun PrivacyUserTextField(
    userInfo: String
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.6f),
        value = userInfo,
        onValueChange = {},
        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        readOnly = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(255, 190, 0, 150),
            unfocusedContainerColor = Color(255, 190, 0, 150),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}
