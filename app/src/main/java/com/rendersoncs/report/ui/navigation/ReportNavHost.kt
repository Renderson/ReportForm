package com.rendersoncs.report.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.screens.dashboard.DashboardScreen
import com.rendersoncs.report.ui.screens.placeholder.PlaceholderScreen

@Composable
fun ReportNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ReportRoute.DASHBOARD,
        modifier = modifier
    ) {
        composable(ReportRoute.DASHBOARD) {
            DashboardScreen(
                onNewReport = { navController.navigate(ReportRoute.NEW_REPORT) }
            )
        }
        composable(ReportRoute.NEW_REPORT) {
            PlaceholderScreen(
                title = stringResource(R.string.label_menu_new_report),
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = ReportRoute.CHECKLIST,
            arguments = listOf(navArgument("reportId") { type = NavType.LongType })
        ) {
            PlaceholderScreen(
                title = stringResource(R.string.label_menu_new_report),
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = ReportRoute.RESUME,
            arguments = listOf(navArgument("reportId") { type = NavType.LongType })
        ) {
            PlaceholderScreen(
                title = stringResource(R.string.summary),
                onBack = { navController.popBackStack() }
            )
        }
        composable(ReportRoute.SETTINGS) {
            PlaceholderScreen(
                title = stringResource(R.string.label_menu_about),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
