package com.headmetal.headwareintelligence

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.naver.maps.map.compose.ExperimentalNaverMapApi

sealed class Destinations(val route: String) {
    data object Loading : Destinations("loadingScreen")
    data object Login : Destinations("loginScreen")
    data object Signup : Destinations("signupScreen")
    data object FindId : Destinations("findidScreen")
    data object FindPw : Destinations("findpwScreen")
    data object Main : Destinations("mainScreen")
    data object Processing : Destinations("processingScreen")
    data object Menu : Destinations("menuScreen")
    data object Countermeasures : Destinations("countermeasuresScreen")
    data object Map : Destinations("mapScreen")
    data object Trend : Destinations("trendScreen")
    data object Helmet : Destinations("helmetScreen")
    data object NullMap : Destinations("nullmapScreen")
    data object CompanyInfo : Destinations("companyinfoScreen")
    data object Etc : Destinations("etcScreen")
    data object License : Destinations("licenseScreen")
    data object Privacy : Destinations("privacyScreen")

    data object Worklist : Destinations("worklistScreen")
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable(Destinations.Loading.route) {
        Loading(navController)
    }
    composable(Destinations.Login.route) {
        Login(navController)
    }
    composable(Destinations.Signup.route) {
        Signup(navController)
    }
    composable(Destinations.FindId.route) {
        FindId(navController)
    }
    composable(Destinations.FindPw.route) {
        FindPw(navController)
    }
}

@OptIn(ExperimentalNaverMapApi::class)
fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(Destinations.Main.route) {
        Main(navController)
    }
    composable(Destinations.Processing.route) {
        Processing(navController)
    }
    composable(Destinations.Map.route) {
        Map(navController)
    }
    composable(Destinations.Trend.route) {
        Trend(navController)
    }
    composable(Destinations.Helmet.route) {
        Helmet(navController)
    }
    composable(Destinations.NullMap.route) {
        NullMap(navController)
    }
    composable(Destinations.Countermeasures.route) {
        Countermeasures(navController)
    }
    composable(Destinations.Menu.route) {
        Menu(navController)
    }
    composable(Destinations.Worklist.route) {
        Worklist(navController)
    }
}

fun NavGraphBuilder.menuNavGraph(navController: NavHostController) {
    composable(Destinations.Menu.route) {
        Menu(navController)
    }
    composable(Destinations.CompanyInfo.route) {
        CompanyInfo(navController)
    }
    composable(Destinations.Etc.route) {
        Etc(navController)
    }
    composable(Destinations.Privacy.route) {
        Privacy(navController)
    }
    composable(Destinations.License.route) {
        License(navController)
    }
}

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "auth") {
        navigation(startDestination = Destinations.Loading.route, route = "auth") {
            authNavGraph(navController)
        }
        navigation(startDestination = Destinations.Main.route, route = "main") {
            mainNavGraph(navController)
        }
        navigation(startDestination = Destinations.Menu.route, route = "menu") {
            menuNavGraph(navController)
        }
    }
}
