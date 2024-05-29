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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.naver.maps.map.compose.ExperimentalNaverMapApi


sealed class Destinations(val route: String,val title: String) {
    object Loading : Destinations("loadingScreen","로딩")
    object Login : Destinations("loginScreen","로그인")
    object Signup: Destinations("signupScreen","회원가입")
    object Findid : Destinations("findidScreen","아이디찾기")
    object Findpw : Destinations("findpwScreen","비밀번호찾기")
    object Main : Destinations("mainScreen","홈")
    object Processing : Destinations("processingScreen","처리내역")
    object Menu : Destinations("menuScreen","메뉴")
    object Countermeasures : Destinations("countermeasuresScreen","행동요령")
    object Map : Destinations("mapScreen","사고현장")
    object Trend : Destinations("trendScreen","추세선")
    object Helmet : Destinations("helmetScreen","헬멧등록")
    object NullMap : Destinations("nullmapScreen","")
    object CompanyInfo : Destinations("companyinfoScreen","회사정보")
    object Etc : Destinations("etcScreen","기타")
    object License : Destinations("licenseScreen","라이센스")
    object Privacy : Destinations("privacyScreen","개인정보")


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
    composable(Destinations.Findid.route) {
        Findid(navController)
    }
    composable(Destinations.Findpw.route) {
        Findpw(navController)
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






