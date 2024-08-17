package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PermContactCalendar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    PrivacyUser(text = "아이디", userInfo = remember {
        mutableStateOf("회사")
    }, imageVector = Icons.Outlined.Person)
}

@Composable
fun Privacy(navController: NavController) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("userid", "") ?: "") }
    val userName: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("name", "") ?: "") }
    val userPhone: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("phone", "") ?: "") }
    val userEmail: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("email", "") ?: "") }
    val password: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("password", "") ?: "") }
    val rePassword: MutableState<String> = remember { mutableStateOf("") }
    val company: MutableState<String> = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        /**TODO*/ // 회사 리스트, 유저 회사 이름 받아오기
    }
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
                        userInfo = userId,
                        imageVector = Icons.Outlined.Person,
                        readOnly = true,
                        textFieldColor = TextFieldDefaults.colors(
                            focusedContainerColor = Color(121, 121, 121, 80),
                            unfocusedContainerColor = Color(121, 121, 121, 80),
                            disabledContainerColor = Color(121, 121, 121, 80),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    PrivacyUser(
                        text = "이름",
                        userInfo = userName,
                        imageVector = Icons.Outlined.PermContactCalendar
                    )
                    PrivacyUser(
                        text = "전화번호",
                        userInfo = userPhone,
                        imageVector = Icons.Outlined.Call
                    )
                    PrivacyUser(
                        text = "이메일",
                        userInfo = userEmail,
                        imageVector = Icons.Outlined.Mail
                    )
                    PrivacyUser(
                        text = "새 비밀번호",
                        userInfo = password,
                        placeholder = "****",
                        imageVector = Icons.Outlined.Lock
                    )
                    PrivacyUser(
                        text = "새 비밀번호 확인",
                        userInfo = rePassword,
                        placeholder = "****",
                        imageVector = Icons.Outlined.Lock
                    )
                    PrivacyUser(
                        text = "건설업체",
                        userInfo = remember { mutableStateOf("없음") }, // 선택한 건설 회사 이름이 들어와야함.
                        imageVector = Icons.Outlined.Business
                    )
                    Button(
                        onClick = {
                            updatePrivacyVerify(
                                userId = userId.value,
                                userName = userName.value,
                                userPhone = userPhone.value,
                                userEmail = userEmail.value,
                                userPassword = password.value,
                                userRePassword = rePassword.value,
                                company = company.value,
                                navController = navController
                            )
                        },
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
    userInfo: MutableState<String>,
    imageVector: ImageVector,
    placeholder: String = "",
    readOnly: Boolean = false,
    textFieldColor: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(255, 190, 0, 150),
        unfocusedContainerColor = Color(255, 190, 0, 150),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
) {
    Column(
        modifier = Modifier
    ) {
        Row {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
            LabelText(text = text)
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.6f),
            value = userInfo.value,
            onValueChange = { userInfo.value = it },
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            readOnly = readOnly,
            colors = textFieldColor,
            placeholder = { Text(text = placeholder) }
        )
    }
}

fun updatePrivacyVerify(
    userId: String,
    userName: String,
    userPhone: String,
    userEmail: String,
    userPassword: String,
    userRePassword: String,
    company: String,
    navController: NavController
) {
    when {
        !isPasswordValid(userPassword) -> showAlertDialog(
            context = navController.context,
            title = "비밀번호 형식 불일치",
            message = "비밀번호는 최소 1개의 알파벳, 1개의 숫자, 1개의 특수문자가 포함되어야 하며, 6자리 이상 16자리 이하이어야 합니다.\n" +
                    "사용가능 특수 문자: @\$!%*?&",
            buttonText = "확인"
        )

        !arePasswordsMatching(userPassword, userRePassword) -> showAlertDialog(
            context = navController.context,
            title = "비밀번호 불일치",
            message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
            buttonText = "확인"
        )

        !isNameValid(userName) -> showAlertDialog(
            context = navController.context,
            title = "이름 글자 수 불일치",
            message = "이름을 4자리 이하로 작성바랍니다.",
            buttonText = "확인"
        )

        !isPhoneValid(userPhone) -> showAlertDialog(
            context = navController.context,
            title = "전화번호 형식 불일치",
            message = "전화번호 형식이 일치하지 않습니다.\nex)XXX-XXXX-XXXX",
            buttonText = "확인"
        )

        !isEmailValid(userEmail) -> showAlertDialog(
            context = navController.context,
            title = "이메일 형식 불일치",
            message = "이메일 형식이 일치하지 않습니다.\nex)XXX@XXX.XXX(공백 제외)",
            buttonText = "확인"
        )

        else -> updatePrivacy(
            userId = userId,
            userName = userName,
            userPhone = userPhone,
            userEmail = userEmail,
            userPassword = userPassword,
            userRePassword = userRePassword,
            company = company,
            navController = navController
        )
    }
}

fun updatePrivacy(
    userId: String,
    userName: String,
    userPhone: String,
    userEmail: String,
    userPassword: String,
    userRePassword: String,
    company: String,
    navController: NavController
) {
    /** TODO*/ // API 호출
}