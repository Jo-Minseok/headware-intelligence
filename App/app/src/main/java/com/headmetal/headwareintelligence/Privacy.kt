package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PermContactCalendar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
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

@Composable
fun Privacy(navController: NavController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) { PrivacyComposable(navController = navController) }
}

@Composable
fun PrivacyComposable(navController: NavController = rememberNavController()) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        BackIcon(modifier = Modifier.clickable { navController.navigateUp() })
        ScreenTitleText(text = "사용자 정보")
        PrivacyUserContents()
        PrivacyChangeButton()
    }
}

@Composable
fun PrivacyUserContents() {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", "")
    val userName = sharedAccount.getString("name", "")
    val userPhone = sharedAccount.getString("phone", "")
    val userEmail = sharedAccount.getString("email", "")

    Column {
        PrivacyUser(text = "아이디", userInfo = userId!!, imageVector = Icons.Outlined.Person)
        PrivacyUser(
            text = "이름",
            userInfo = userName!!,
            imageVector = Icons.Outlined.PermContactCalendar
        )
        PrivacyUser(text = "전화번호", userInfo = userPhone!!, imageVector = Icons.Outlined.Call)
        PrivacyUser(text = "이메일", userInfo = userEmail!!, imageVector = Icons.Outlined.Mail)
        PrivacyUser(text = "비밀번호", userInfo = "****", imageVector = Icons.Outlined.Lock)
        PrivacyUser(
            text = "건설업체",
            userInfo = "없음",
            imageVector = Icons.Outlined.Business
        ) // 추후 구현 필요
    }
}

@Composable
fun PrivacyUser(
    text: String = "",
    userInfo: String = "",
    imageVector: ImageVector
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(top = 30.dp)
    ) {
        Row {
            LabelIcon(imageVector = imageVector)
            LabelText(text = text)
        }
        PrivacyUserTextField(userInfo = userInfo)
    }
}

@Composable
fun PrivacyUserTextField(
    userInfo: String = ""
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

@Composable
fun PrivacyChangeButton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FunctionButton(
            buttonText = "개인 정보 변경",
            colors = ButtonDefaults.buttonColors(Color(0xFF372A1F))
        ) {} // 추후 구현 필요
    }
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun PrivacyPreview() {
    Privacy()
}

@Preview(showBackground = true)
@Composable
fun PrivacyComposablePreview() {
    PrivacyComposable()
}

@Preview(showBackground = true)
@Composable
fun PrivacyBackIconPreview() {
    BackIcon()
}

@Preview(showBackground = true)
@Composable
fun PrivacyTitleTextPreview() {
    ScreenTitleText(text = "사용자 정보")
}

@Preview(showBackground = true)
@Composable
fun PrivacyUserContentsPreview() {
    PrivacyUserContents()
}

@Preview(showBackground = true)
@Composable
fun PrivacyUserPreview() {
    PrivacyUser(text = "아이디", userInfo = "id", imageVector = Icons.Outlined.Person)
}

@Preview(showBackground = true)
@Composable
fun PrivacyLabelIconPreview() {
    LabelIcon(imageVector = Icons.Outlined.Person)
}

@Preview(showBackground = true)
@Composable
fun PrivacyLabelTextPreview() {
    LabelText(text = "아이디")
}

@Preview(showBackground = true)
@Composable
fun PrivacyUserTextFieldPreview() {
    PrivacyUserTextField(userInfo = "id")
}

@Preview(showBackground = true)
@Composable
fun PrivacyChangeButtonPreview() {
    PrivacyChangeButton()
}
