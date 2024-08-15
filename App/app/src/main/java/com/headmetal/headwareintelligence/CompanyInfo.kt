package com.headmetal.headwareintelligence

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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

@Composable
fun CompanyInfo(navController: NavController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) { CompanyInfoComposable(navController = navController) }
}

@Composable
fun CompanyInfoComposable(navController: NavController = rememberNavController()) {
    Column {
        BackIcon(onClick = { navController.navigateUp() })
        ScreenTitleText(text = "참여 건설 업체")
        CompanyListComposable()
    }
}

@Composable
fun CompanyListComposable() {
    var companies by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    response.body()?.let { companies = it.companies }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                Log.e("HEAD METAL", "서버 통신 실패: ${t.message}")
            }
        })
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(top = 10.dp)
    ) {
        items(companies) { company ->
            CompanyCard { Text(text = company, fontSize = 24.sp) }
        }
    }
}

@Composable
fun CompanyCard(
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(255, 190, 0, 150)),
        content = content
    )
}


// 프리뷰
@Preview(showBackground = true)
@Composable
fun CompanyInfoPreview() {
    CompanyInfo()
}

@Preview(showBackground = true)
@Composable
fun CompanyInfoComposablePreview() {
    CompanyInfoComposable()
}

@Preview(showBackground = true)
@Composable
fun CompanyInfoBackIconPreview() {
    BackIcon()
}

@Preview(showBackground = true)
@Composable
fun CompanyInfoTitleTextPreview() {
    ScreenTitleText(text = "참여 견설 업체")
}

@Preview(showBackground = true)
@Composable
fun CompanyListComposablePreview() {
    CompanyListComposable()
}

@Preview(showBackground = true)
@Composable
fun CompanyCardPreview() {
    CompanyCard { Text(text = "부산건설", fontSize = 24.sp) }
}
