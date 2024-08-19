package com.headmetal.headwareintelligence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class RegisterInputModel(
    val id: String,
    val password: String,
    val rePassword: String,
    val name: String,
    val email: String,
    val phoneNo: String,
    val company: String?,
    val type: String
)

data class CompanyList(
    val companies: List<String>
)

// 프리뷰
@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUp(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun SignUpHelmetImagePreview() {
    HelmetImage()
}

@Composable
fun SignUp(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9C94C)
    ) {
        LoadingScreen()
        SignUpComposable(navController = navController)
    }
}

@Composable
fun SignUpComposable(navController: NavController) {
    val id: MutableState<String> = remember { mutableStateOf("") }
    val pw: MutableState<String> = remember { mutableStateOf("") }
    val rePw: MutableState<String> = remember { mutableStateOf("") }
    val name: MutableState<String> = remember { mutableStateOf("") }
    val phone: MutableState<String> = remember { mutableStateOf("") }
    val email: MutableState<String> = remember { mutableStateOf("") }
    val selectedCompany: MutableState<String> = remember { mutableStateOf("없음") }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isEmployee: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isManager: MutableState<Boolean> = remember { mutableStateOf(false) }

    val selectableCompany = remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        companyListGET(
            companyList = selectableCompany,
            navController = navController,
            onDismissRequest = { navController.navigateUp() },
            defaultValue = listOf("없음")
        )
    }
    Surface(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelmetImage()
            LabelAndInputComposable(
                labelText = "아이디",
                inputText = id,
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "비밀번호",
                inputText = pw,
                visualTransformation = PasswordVisualTransformation(),
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "비밀번호 확인",
                inputText = rePw,
                visualTransformation = PasswordVisualTransformation(),
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "이름",
                inputText = name,
                placeholder = "4글자 이내",
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "전화번호",
                inputText = phone,
                placeholder = "XXX-XXXX-XXXX",
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndInputComposable(
                labelText = "이메일",
                inputText = email,
                placeholder = "'@' 를 포함한 이메일 형식",
                textFieldmodifier = Modifier.alpha(0.6f)
            )
            LabelAndDropdownMenu(
                fieldText = "건설업체",
                expanded = expanded,
                selectedItem = selectedCompany,
                selectableItems = selectableCompany.value,
                modifier = Modifier.alpha(0.6f)
            )
            LabelAndRadioButtonComposable(
                labelText = "직무",
                firstButtonSwitch = isEmployee,
                secondButtonSwitch = isManager,
                firstButtonText = "일반직",
                secondButtonText = "관리직"
            )
            Button(
                onClick = {
                    registerVerify(
                        id = id.value,
                        pw = pw.value,
                        rePw = rePw.value,
                        name = name.value,
                        email = email.value,
                        phone = phone.value,
                        selectCompany = selectedCompany.value,
                        isManager = isManager.value,
                        navController = navController
                    )
                },
                shape = MaterialTheme.shapes.small,
                content = { Text("회원가입") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x59000000))
            )
        }
    }
}

fun registerVerify(
    id: String,
    pw: String,
    rePw: String,
    name: String,
    email: String,
    phone: String,
    selectCompany: String,
    isManager: Boolean,
    navController: NavController
) {
    when {
        !isIdValid(id) -> showAlertDialog(
            context = navController.context,
            title = "아이디 형식 불일치",
            message = "아이디는 최소 1개의 알파벳이 포함되어야 하며, 6자리 이상 16자리 이하이어야 합니다. 특수문자는 포함될 수 없습니다.",
            buttonText = "확인"
        )

        !isPasswordValid(pw) -> showAlertDialog(
            context = navController.context,
            title = "비밀번호 형식 불일치",
            message = "비밀번호는 최소 1개의 알파벳, 1개의 숫자, 1개의 특수문자가 포함되어야 하며, 6자리 이상 16자리 이하이어야 합니다.\n" +
                    "사용가능 특수 문자: @\$!%*?&",
            buttonText = "확인"
        )

        !arePasswordsMatching(pw, rePw) -> showAlertDialog(
            context = navController.context,
            title = "비밀번호 불일치",
            message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
            buttonText = "확인"
        )

        !isNameValid(name) -> showAlertDialog(
            context = navController.context,
            title = "이름 글자 수 불일치",
            message = "이름을 4자리 이하로 작성바랍니다.",
            buttonText = "확인"
        )

        !isPhoneValid(phone) -> showAlertDialog(
            context = navController.context,
            title = "전화번호 형식 불일치",
            message = "전화번호 형식이 일치하지 않습니다.\nex)XXX-XXXX-XXXX",
            buttonText = "확인"
        )

        !isEmailValid(email) -> showAlertDialog(
            context = navController.context,
            title = "이메일 형식 불일치",
            message = "이메일 형식이 일치하지 않습니다.\nex)XXX@XXX.XXX(공백 제외)",
            buttonText = "확인"
        )

        else -> registerPOST(
            id = id,
            pw = pw,
            rePw = rePw,
            name = name,
            email = email,
            phone = phone,
            selectCompany = selectCompany,
            isManager = isManager,
            navController = navController
        )
    }
}