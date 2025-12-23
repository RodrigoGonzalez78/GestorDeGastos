package com.example.gestordegastos.presenter.app_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestordegastos.presenter.home_screen.HomeScreen
import com.example.gestordegastos.presenter.new_operation.NewOperationScreen
import com.example.gestordegastos.presenter.statistics.StatisticsScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object NewOperationRoute

@Serializable
object StatisticsRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(
                navController = navController,
                onNewOperation = {
                    navController.navigate(NewOperationRoute)
                }
            )
        }

        composable<NewOperationRoute> {
            NewOperationScreen(
                navController = navController,
            )
        }

        composable<StatisticsRoute> {
            StatisticsScreen(
                navController = navController,
            )
        }

    }
}
