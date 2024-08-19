package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class FindAccount(
    val id: String
)

data class UpdateAccount(
    val password: String,
    val rePassword: String,
    val name: String,
    val email: String,
    val phoneNo: String,
    val company: String?,
    val type: String
)

data class PrivacyRequest(
    val findKey: FindAccount,
    val inputData: UpdateAccount
)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Privacy(navController: NavController) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userType: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("type", "") ?: "") }
    val userId: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("userid", "") ?: "") }
    val userName: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("name", "") ?: "") }
    val userPhone: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("phone", "") ?: "") }
    val userEmail: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("email", "") ?: "") }
    val password: MutableState<String> = remember { mutableStateOf("") }
    val rePassword: MutableState<String> = remember { mutableStateOf("") }
    val selectedCompany: MutableState<String> =
        remember { mutableStateOf(sharedAccount.getString("company", "없음") ?: "없음") }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }

    val selectableCompany = remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        companyListGET(
            companyList = selectableCompany,
            navController = navController,
            onDismissRequest = { navController.navigateUp() },
            defaultValue = listOf("없음")
        )
    }
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                        imageVector = Icons.Outlined.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    PrivacyUser(
                        text = "새 비밀번호 확인",
                        userInfo = rePassword,
                        imageVector = Icons.Outlined.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Column {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Business,
                                contentDescription = null
                            )
                            LabelText(text = "건설업체")
                        }
                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = !expanded.value }
                        ) {
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                value = selectedCompany.value,
                                onValueChange = { selectedCompany.value = it },
                                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                shape = MaterialTheme.shapes.medium,
                                singleLine = true,
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(255, 236, 186, 255),
                                    unfocusedContainerColor = Color(255, 236, 186, 255),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )
                            ExposedDropdownMenu(
                                modifier = Modifier.background(Color.White),
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                selectableCompany.value.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            expanded.value = false
                                            selectedCompany.value = item
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            updatePrivacyVerify(
                                userId = userId.value,
                                userName = userName.value,
                                userPhone = userPhone.value,
                                userEmail = userEmail.value,
                                userPassword = password.value,
                                userRePassword = rePassword.value,
                                userCompany = selectedCompany.value,
                                userType = userType.value,
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
    readOnly: Boolean = false,
    textFieldColor: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(255, 236, 186, 255),
        unfocusedContainerColor = Color(255, 236, 186, 255),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column {
        Row {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
            LabelText(text = text)
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = userInfo.value,
            onValueChange = { userInfo.value = it },
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            readOnly = readOnly,
            colors = textFieldColor,
            visualTransformation = visualTransformation
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
    userCompany: String,
    userType: String,
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
            userCompany = userCompany,
            userType = userType,
            navController = navController
        )
    }
}