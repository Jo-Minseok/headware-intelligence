package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
fun notificationSettingOreo(context: Context): Intent {
    return Intent().also { intent ->
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}

fun notificationSettingOreoLess(context: Context): Intent {
    return Intent().also { intent ->
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", context.packageName)
        intent.putExtra("app_uid", context.applicationInfo?.uid)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}

@Composable
fun Menu(navController: NavController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) { MenuComposable(navController = navController) }
}

@Composable
fun MenuComposable(navController: NavController = rememberNavController()) {
    Column {
        BackIcon(modifier = Modifier.clickable { navController.navigateUp() })
        TitleText(text = "메뉴")
        MenuContents(navController = navController)
    }
}

@Composable
fun MenuContents(navController: NavController = rememberNavController()) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val type = sharedAccount.getString("type", "")
    val userName = sharedAccount.getString("name", "")

    Column(modifier = Modifier.padding(horizontal = 30.dp)) {
        PrivacyUserButton(type = type!!, userName = userName!!, navController = navController)
        MenuFunctions(type = type, navController = navController)
    }
}

@Composable
fun PrivacyUserButton(
    type: String = "employee",
    userName: String = "근로자",
    navController: NavController = rememberNavController()
) {
    FunctionButton(
        modifier = Modifier.fillMaxWidth(),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabelIcon(imageVector = Icons.Outlined.Person)
                UserInfo(type = type, userName = userName)
            }
        },
        colors = ButtonDefaults.buttonColors(Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
    ) { navController.navigate("PrivacyScreen") }
}

@Composable
fun UserInfo(
    type: String = "employee",
    userName: String = "근로자"
) {
    Column(verticalArrangement = Arrangement.Center) {
        UserDistinguish(type = type)
        UserName(userName = userName)
    }
}

@Composable
fun UserDistinguish(
    type: String = "employee"
) {
    Text(
        text = if (type == "manager") "관리자" else "근로자",
        color = Color.Gray,
        fontSize = 16.sp
    )
}

@Composable
fun UserName(
    userName: String = "근로자"
) {
    Text(
        text = userName,
        color = Color.Black,
        fontSize = 20.sp
    )
}

@Composable
fun MenuFunctions(
    type: String = "employee",
    navController: NavController = rememberNavController()
) {
    val showLogoutDialog: MutableState<Boolean> = remember { mutableStateOf(false) }

    if (showLogoutDialog.value) {
        LogoutAlertDialog(
            showAlertDialog = showLogoutDialog,
            dismissButton = { showLogoutDialog.value = false },
            navController = navController
        )
    }

    Column(modifier = Modifier.padding(top = 10.dp)) {
        MenuFunctionButton(
            menuIcon = { LabelIcon(imageVector = Icons.Outlined.Description) },
            menuText = { LabelText(text = "참여 건설 업체") },
            progressIcon = { ProgressIcon() }
        ) { navController.navigate("CompanyInfoScreen") }

        if (type == "manager") {
            MenuFunctionButton(
                menuIcon = { LabelIcon(imageVector = Icons.Outlined.Notifications) },
                menuText = { LabelText(text = "알림 설정") },
                progressIcon = { ProgressIcon() }
            ) {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationSettingOreo(navController.context)
                } else {
                    notificationSettingOreoLess(navController.context)
                }
                try {
                    navController.context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        MenuFunctionButton(
            menuIcon = { LabelIcon(imageVector = Icons.Outlined.Info) },
            menuText = { LabelText(text = "기타") },
            progressIcon = { ProgressIcon() }
        ) { navController.navigate("EtcScreen") }
        MenuFunctionButton(
            menuIcon = {
                LabelIcon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    color = Color(0xFFFF6600)
                )
            },
            menuText = { LabelText(text = "로그아웃", color = Color(0xFFFF6600)) }
        ) { showLogoutDialog.value = true }
    }
}

@Composable
fun MenuFunctionButton(
    menuIcon: @Composable () -> Unit,
    menuText: @Composable () -> Unit,
    progressIcon: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    FunctionButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .height(60.dp),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                menuIcon()
                menuText()
                Spacer(modifier = Modifier.weight(1f))
                progressIcon()
            }
        },
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        onClick = onClick
    )
}

@Composable
fun LogoutAlertDialog(
    showAlertDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
    dismissButton: () -> Unit = {},
    navController: NavController = rememberNavController()
) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val sharedConfigure: SharedPreferences =
        LocalContext.current.getSharedPreferences("Configure", Activity.MODE_PRIVATE)
    val sharedAlert: SharedPreferences =
        LocalContext.current.getSharedPreferences("Alert", Activity.MODE_PRIVATE)
    val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()
    val sharedConfigureEdit: SharedPreferences.Editor = sharedConfigure.edit()

    YesNoAlertDialog(
        title = "로그아웃",
        textComposable = { Text(text = "로그아웃 하시겠습니까?") },
        confirmButton = {
            if (sharedAccount.getString("type", null) == "manager") {
                RetrofitInstance.apiService.apiLogout(
                    id = sharedAccount.getString("userid", null).toString(),
                    alertToken = sharedAlert.getString("alert_token", null).toString()
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(p0: Call<Void>, p1: Response<Void>) {
                        showAlertDialog.value = false
                        Toast.makeText(
                            navController.context,
                            "로그아웃을 성공하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedAccountEdit.clear()
                        sharedAccountEdit.apply()
                        sharedConfigureEdit.clear()
                        sharedConfigureEdit.apply()
                        navController.navigate("LoginScreen")
                    }

                    override fun onFailure(p0: Call<Void>, p1: Throwable) {
                        Toast.makeText(
                            navController.context,
                            "로그아웃을 실패하였습니다. 인터넷을 확인하세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                showAlertDialog.value = false
                Toast.makeText(
                    navController.context,
                    "로그아웃을 성공하였습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                sharedAccountEdit.clear()
                sharedAccountEdit.apply()
                sharedConfigureEdit.clear()
                sharedConfigureEdit.apply()
                navController.navigate("LoginScreen")
            }
        },
        dismissButton = dismissButton
    )
}


// 프리뷰
@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    Menu()
}

@Preview(showBackground = true)
@Composable
fun MenuComposablePreview() {
    MenuComposable()
}

@Preview(showBackground = true)
@Composable
fun MenuBackIconPreview() {
    BackIcon()
}

@Preview(showBackground = true)
@Composable
fun MenuTitleTextPreview() {
    TitleText(text = "메뉴")
}

@Preview(showBackground = true)
@Composable
fun MenuContentsPreview() {
    MenuContents()
}

@Preview(showBackground = true)
@Composable
fun MenuUserPrivacyButtonPreview() {
    PrivacyUserButton(type = "manager", userName = "관리자")
}

@Preview(showBackground = true)
@Composable
fun MenuUserInfoPreview() {
    UserInfo(type = "manager", "관리자")
}

@Preview(showBackground = true)
@Composable
fun MenuUserDistinguishPreview() {
    UserDistinguish(type = "manager")
}

@Preview(showBackground = true)
@Composable
fun MenuUserNamePreview() {
    UserName(userName = "관리자")
}

@Preview(showBackground = true)
@Composable
fun MenuFunctionsPreview() {
    MenuFunctions(type = "manager")
}

@Preview(showBackground = true)
@Composable
fun MenuFunctionButtonPreview() {
    MenuFunctionButton(
        menuIcon = { LabelIcon(imageVector = Icons.Outlined.Description) },
        menuText = { LabelText(text = "참여 건설 업체") },
        progressIcon = { ProgressIcon() }
    )
}

@Preview(showBackground = true)
@Composable
fun MenuLabelIconPreview() {
    LabelIcon(imageVector = Icons.Outlined.Description)
}

@Preview(showBackground = true)
@Composable
fun MenuLabelTextPreview() {
    LabelText(text = "참여 건설 업체")
}

@Preview(showBackground = true)
@Composable
fun MenuProgressIconPreview() {
    ProgressIcon()
}

@Preview(showBackground = true)
@Composable
fun MenuLogoutAlertDialogPreview() {
    LogoutAlertDialog()
}
