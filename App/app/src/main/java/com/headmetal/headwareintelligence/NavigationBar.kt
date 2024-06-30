package com.headmetal.headwareintelligence

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.naver.maps.map.compose.ExperimentalNaverMapApi

sealed class Destinations(val route: String) {
    data object Loading : Destinations("LoadingScreen")
    data object Login : Destinations("LoginScreen")
    data object Signup : Destinations("SignUpScreen")
    data object FindId : Destinations("FindIdScreen")
    data object FindPw : Destinations("FindPwScreen")
    data object Main : Destinations("MainScreen")
    data object Processing : Destinations("ProcessingScreen")
    data object Menu : Destinations("MenuScreen")
    data object Countermeasure : Destinations("CountermeasureScreen")
    data object Map : Destinations("MapScreen")
    data object NullMap : Destinations("NullMapScreen")
    data object Trend : Destinations("TrendScreen")
    data object Helmet : Destinations("HelmetScreen")
    data object CompanyInfo : Destinations("CompanyInfoScreen")
    data object Etc : Destinations("EtcScreen")
    data object License : Destinations("LicenseScreen")
    data object Privacy : Destinations("PrivacyScreen")
    data object WorkList : Destinations("WorkListScreen")
    data object Work : Destinations("WorkScreen/{workId}")
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable(Destinations.Loading.route) {
        Loading(navController)
    }
    composable(Destinations.Login.route) {
        Login(navController)
    }
    composable(Destinations.Signup.route) {
        SignUp(navController)
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
    composable(Destinations.Countermeasure.route) {
        Countermeasure(navController)
    }
    composable(Destinations.Menu.route) {
        Menu(navController)
    }
    composable(Destinations.WorkList.route) {
        WorkList(navController)
    }
    composable(
        route = Destinations.Work.route,
        arguments = listOf(navArgument("workId") { type = NavType.IntType })
    ) { backStackEntry ->
        Work(
            workId = backStackEntry.arguments?.getInt("workId") ?: 0,
            navController = navController
        )
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
