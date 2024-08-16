package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 프리뷰
@Preview(showBackground = true)
@Composable
fun CompanyInfoPreview() {
    CompanyInfo()
}

@Preview(showBackground = true)
@Composable
fun CompanyCardListPreview() {
    val companies by remember { mutableStateOf(listOf("부산 건설", "동의 건설")) }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(top = 10.dp)
    ) {
        items(companies) { company ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(255, 190, 0, 150)),
                content = { Text(text = company, fontSize = 24.sp) }
            )
        }
    }
}

@Composable
fun CompanyInfo(navController: NavController = rememberNavController()) {
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            var companies by remember { mutableStateOf(listOf<String>()) }

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

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ScreenTitleText(text = "참여 건설 업체")
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .padding(top = 10.dp)
                ) {
                    items(companies) { company ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(
                                    255,
                                    190,
                                    0,
                                    150
                                )
                            ),
                            content = { Text(text = company, fontSize = 24.sp) }
                        )
                    }
                }
            }
        }
    )
}
