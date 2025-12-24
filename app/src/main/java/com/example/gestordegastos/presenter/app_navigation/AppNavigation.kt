package com.example.gestordegastos.presenter.app_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.gestordegastos.presenter.export.ExportScreen
import com.example.gestordegastos.presenter.home_screen.HomeScreen
import com.example.gestordegastos.presenter.new_operation.NewOperationScreen
import com.example.gestordegastos.presenter.statistics.StatisticsScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class NewOperationRoute(val operationId: Int = -1)

@Serializable
object StatisticsRoute

@Serializable
object ExportRoute

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
                onNewOperation = { operationId ->
                    navController.navigate(NewOperationRoute(operationId = operationId))
                },
                onStatistics = {
                    navController.navigate(StatisticsRoute)
                },
                onExport = {
                    navController.navigate(ExportRoute)
                }
            )
        }

        composable<NewOperationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<NewOperationRoute>()
            NewOperationScreen(
                navController = navController,
                operationId = route.operationId
            )
        }

        composable<StatisticsRoute> {
            StatisticsScreen(
                navController = navController,
            )
        }

        composable<ExportRoute> {
            ExportScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}

