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
import com.rendersoncs.report.ui.dashboard.HomeScreen
import com.rendersoncs.report.ui.newreport.NewReportScreen
import com.rendersoncs.report.ui.resume.ResumeScreen
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
            HomeScreen(
                onNewReport = { navController.navigate(ReportRoute.NEW_REPORT) },
                onOpenReport = { report ->
                    val id = report.id?.toLong() ?: return@HomeScreen
                    val route = if (report.concluded == false) {
                        ReportRoute.checklist(id)
                    } else {
                        ReportRoute.resume(id)
                    }
                    navController.navigate(route)
                }
            )
        }
        composable(ReportRoute.NEW_REPORT) {
            NewReportScreen(
                onBack = { navController.popBackStack() },
                onStarted = { reportId ->
                    navController.navigate(ReportRoute.checklist(reportId.toLong())) {
                        popUpTo(ReportRoute.NEW_REPORT) { inclusive = true }
                    }
                }
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
            ResumeScreen(
                onBack = { navController.popBackStack() },
                onEdit = { reportId -> navController.navigate(ReportRoute.checklist(reportId)) },
                onDeleted = { navController.popBackStack() }
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
