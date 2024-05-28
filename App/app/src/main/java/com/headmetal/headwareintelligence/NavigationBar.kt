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


sealed class Destinations(val route: String) {
    object Loading : Destinations("loadingScreen")
    object Login : Destinations("loginScreen")
    object Signup: Destinations("signupScreen")
    object Findid : Destinations("findidScreen")
    object Findpw : Destinations("findpwScreen")
    object Main : Destinations("mainScreen")
    object Processing : Destinations("processingScreen")
    object Menu : Destinations("menuScreen")
    object Countermeasures : Destinations("countermeasuresScreen")
    object Map : Destinations("mapScreen")
    object Helmet : Destinations("helmetScreen")
    object NullMap : Destinations("nullmap Screen")
    object CompanyInfo : Destinations("companyinfoScreen")
    object Etc : Destinations("etcScreen")
    object License : Destinations("licenseScreen")
    object Privacy : Destinations("privacyScreen")


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

@Composable
fun BottomBar(navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier) {
    val screens = listOf(
        Destinations.Processing,
        Destinations.Main,
        Destinations.Menu
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                label = { Text(text = screen.route) },
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Destinations.Processing -> Icons.Default.Search
                            Destinations.Main -> Icons.Default.Home
                            Destinations.Menu -> Icons.Default.Info
                            else -> Icons.Default.Home
                        },
                        contentDescription = screen.route
                    )
                },
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
                    unselectedTextColor = Color.Gray, selectedTextColor = Color.Black
                )
            )
        }
    }
}






