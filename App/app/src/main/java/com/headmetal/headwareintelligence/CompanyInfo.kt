package com.headmetal.headwareintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
fun CompanyCardPreview() {
    CompanyCard(company = "부산 건설")
}

@Preview(showBackground = true)
@Composable
fun CompanyCardListPreview() {
    val companies by remember { mutableStateOf(listOf("부산 건설", "동의 건설")) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(companies) { company ->
            CompanyCard(company)
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
                            networkErrorFinishApp(navController = navController, error = t)
                        }
                    })
            }

            ScreenTitleText(text = "참여 건설 업체")
            LazyColumn(
                modifier = Modifier.padding(top = 10.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(companies) { company ->
                    CompanyCard(company)
                }
            }
        }
    )
}

@Composable
fun CompanyCard(company: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFFFFF)
                ), // 카드 전체 너비에 맞추기

            contentAlignment = Alignment.Center // 가운데 정렬
        ) {
            Text(
                text = company,
                fontSize = 24.sp
            )
        }
    }
}