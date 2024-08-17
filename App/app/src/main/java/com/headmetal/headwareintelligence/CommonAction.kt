package com.headmetal.headwareintelligence

import java.text.SimpleDateFormat
import java.util.Locale
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

/**
 * 계정
 */
fun isIdValid(id: String): Boolean {
    return id.matches("^(?=.*[A-Za-z])[A-Za-z0-9]{6,16}$".toRegex())
}

fun isPasswordValid(pw: String): Boolean {
    return pw.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,16}$".toRegex())
}

fun arePasswordsMatching(pw: String, rePw: String): Boolean {
    return pw == rePw
}

fun isNameValid(name: String): Boolean {
    return name.length <= 4
}

fun isPhoneValid(phone: String): Boolean {
    return phone.matches("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$".toRegex())
}

fun isEmailValid(email: String): Boolean {
    return email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".toRegex())
}

/**
 * 작업 날짜
 */
fun isInvalidStartDate(inputWorkStartDate: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false // 날짜 엄격 검증

        val startDate = dateFormat.parse(inputWorkStartDate)
        val minDate = dateFormat.parse("1970-01-01")

        startDate == null || startDate.before(minDate)
    } catch (e: Exception) {
        true // 날짜 형식이 잘못된 경우
    }
}

fun isInvalidEndDate(inputWorkStartDate: String, inputWorkEndDate: String): Boolean {
    // 종료 날짜가 비어 있는 경우 검증을 하지 않음
    if (inputWorkEndDate.isEmpty()) {
        return false
    }

    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false // 날짜 엄격 검증

        val startDate = dateFormat.parse(inputWorkStartDate)
        val endDate = dateFormat.parse(inputWorkEndDate)

        endDate == null || endDate.before(startDate) // 종료 날짜가 시작 날짜보다 이전이면 오류
    } catch (e: Exception) {
        true // 날짜 형식이 잘못된 경우
    }
}

/**
 * 작업 이름
 */

fun isInvalidWorkName(workName: String): Boolean{
    return workName.length > 16
}

/**
 * 위치 정보 권한 확인
 */

fun hasLocationPermissions(context: Context): Boolean {
    val fineLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fineLocationPermission || coarseLocationPermission
}

/**
 * 애플리케이션 네트워크 및 서버 접속 오류 종료
 */

fun networkErrorFinishApp(navController: NavController, error: Throwable) {
    Log.e("HEAD METAL", "서버 통신 실패: ${error.message}")
    showAlertDialog(
        context = navController.context,
        title = "서버 접속 실패",
        message = "서버 상태 및 네트워크 접속 불안정",
        buttonText = "확인",
        onButtonClick = { (navController.context as Activity).finish() }
    )
}