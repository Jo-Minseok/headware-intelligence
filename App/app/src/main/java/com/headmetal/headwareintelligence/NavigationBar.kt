package com.headmetal.headwareintelligence

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun isItemSelected(navController: NavController, destination: String): Boolean {
    val currentBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    return currentBackStackEntry?.destination?.route == destination
}
@Composable
fun NavigationBar(navController: NavController) {
    BottomNavigation(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent // 네비게이션 바의 배경색을 투명하게 설정
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("검색") },
            selected = isItemSelected(navController, "processingScreen"),
            onClick = { navController.navigate("processingScreen") },
            selectedContentColor = Color.Black // 선택된 상태에서 아이콘과 텍스트의 색상
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("홈") },
            selected = isItemSelected(navController, "mainScreen"),
            onClick = { navController.navigate("mainScreen") },
            selectedContentColor = Color.Black // 선택된 상태에서 아이콘과 텍스트의 색상
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
            label = { Text("정보") },
            selected = isItemSelected(navController, "menuScreen"),
            onClick = { navController.navigate("menuScreen") },
            selectedContentColor = Color.Black // 선택된 상태에서 아이콘과 텍스트의 색상
        )
    }
}
