package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Accident_Processing 테이블의 데이터를 수신하는 데이터 클래스(Response)
data class AccidentProcessingResponse(
    val no: Int, // 사고 번호
    val situation: String?, // 처리 상황
    val detail: String?, // 사고 처리 세부 내역
    val victimId: String,
    val victimName: String // 사고자 이름
)

// Accident_Processing 테이블 데이터를 갱신하기 위한 데이터 클래스(Request)
data class AccidentProcessingUpdateRequest(
    val detail: String? // 사고 처리 세부 내역
)

// Accident_Processing 테이블의 사고 상황과 세부 처리 내역 데이터를 업데이트
fun updateAccidentSituation(no: Int, situationCode: String, detail: String?) {
    val call = RetrofitInstance.apiService.updateAccidentSituation(
        no, situationCode, AccidentProcessingUpdateRequest(detail)
    )
    call.enqueue(object : Callback<AccidentProcessingUpdateRequest> {
        override fun onResponse(
            call: Call<AccidentProcessingUpdateRequest>,
            response: Response<AccidentProcessingUpdateRequest>
        ) {
            if (response.isSuccessful) {
                Log.i("HEAD METAL", "사고 상황 업데이트 성공")
            } else {
                Log.e("HEAD METAL", "서버에서 오류 응답을 받음")
            }
        }

        override fun onFailure(call: Call<AccidentProcessingUpdateRequest>, t: Throwable) {
            Log.e("HEAD METAL", "네트워크 오류 또는 예외 발생: ${t.message}")
        }
    })
}

// 알림창 Composable
@Composable
fun AlertDialog(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose, content = {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "알림")
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "사고 처리 세부 내역은 최소\n한 글자 이상 입력해야 합니다.", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onClose, colors = ButtonDefaults.buttonColors(Color.Gray)
                ) { Text(text = "닫기") }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    })
}

// 종료 알림창 Composable
@Composable
fun EndDialog(onEnd: () -> Unit, message: String) {
    Dialog(onDismissRequest = onEnd, content = {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "알림")
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onEnd, colors = ButtonDefaults.buttonColors(Color.Gray)
                ) { Text(text = "종료") }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    })
}
