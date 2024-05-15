package com.headmetal.headwareintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CompanyInfo(navController: NavController) {
    var companies by remember { mutableStateOf(listOf<String>()) }

    val apiService = RetrofitInstance.apiService
    LaunchedEffect(Unit) {
        val call = apiService.getCompanyList()
        call.enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val companyList: CompanyList? = response.body()
                    companyList?.let {
                        companies = it.companies
                    }
                }
            }
            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                println("서버 통신 실패: ${t.message}")
            }
        })
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        Column {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() }
            )
            Row {
                Text(
                    text = "참여 건설 업체",
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
            ) {
                items(companies) { company ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(text = company, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
