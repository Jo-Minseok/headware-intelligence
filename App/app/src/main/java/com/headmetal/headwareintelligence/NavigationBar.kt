package com.headmetal.headwareintelligence

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

//@Composable
//fun isItemSelected(navController: NavController, destination: String): Boolean {
//    val currentBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
//    return currentBackStackEntry?.destination?.route == destination
//}
//@Composable
//fun NavigationBar(navController: NavController) {
//    BottomNavigation(
//        modifier = Modifier.fillMaxSize(),
//        backgroundColor = Color.Transparent // 네비게이션 바의 배경색을 투명하게 설정
//    ) {
//        BottomNavigationItem(
//            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
//            label = { Text("검색") },
//            selected = isItemSelected(navController, "processingScreen"),
//            onClick = { navController.navigate("processingScreen") },
//            selectedContentColor = Color.Black // 선택된 상태에서 아이콘과 텍스트의 색상
//        )
//        BottomNavigationItem(
//            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//            label = { Text("홈") },
//            selected = isItemSelected(navController, "mainScreen"),
//            onClick = { navController.navigate("mainScreen") },
//            selectedContentColor = Color.Black // 선택된 상태에서 아이콘과 텍스트의 색상
//        )
//        BottomNavigationItem(
//            icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
//            label = { Text("정보") },
//            selected = isItemSelected(navController, "menuScreen"),
//            onClick = { navController.navigate("menuScreen") },
//            selectedContentColor = Color.Black // 선택된 상태에서 아이콘과 텍스트의 색상
//        )
//    }
//}

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object processingScreen : Destinations(
        route = "processingScreen",
        title = "검색",
        icon = Icons.Default.Search
    )

    object mainScreen : Destinations(
        route = "mainScreen",
        title = "메인",
        icon = Icons.Default.Home
    )

    object menuScreen : Destinations(
        route = "menuScreen",
        title = "메뉴",
        icon = Icons.Default.Info
    )

}
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "loadingScreen") {
        composable("loadingScreen") {
            Loading(navController)
        }
        composable("loginScreen") {
            Login(navController)
        }
        composable("signupScreen") {
            Signup(navController)
        }
        composable("mainScreen") {
            Main(navController)
        }
        composable("processingScreen") {
            Processing(navController)
        }
        composable("menuScreen") {
            Menu(navController)
        }
        composable("findidScreen") {
            Findid(navController)
        }
        composable("findpwScreen") {
            Findpw(navController)
        }
        composable("etcScreen") {
            Etc(navController)
        }
        composable("licenseScreen") {
            License(navController)
        }
        composable("helmetScreen") {
            Helmet()
        }
        composable("privacyScreen") {
            Privacy(navController)
        }
        composable("privacychangeScreen") {
            PrivacyChange(navController)
        }
        composable("companyinfoScreen") {
            CompanyInfo(navController)
        }
        composable("trendScreen") {
            Trend(navController)
        }
        composable("countermeasuresScreen") {
            Countermeasures(navController)
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    val screens = listOf(
        Destinations.processingScreen,
        Destinations.mainScreen,
        Destinations.menuScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                label = { Text(text = screen.title!!) },
                icon = { Icon(imageVector = screen.icon!!, contentDescription = "") },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedTextColor = Color.White, selectedTextColor = Color.LightGray
                )
            )
        }
    }
}





