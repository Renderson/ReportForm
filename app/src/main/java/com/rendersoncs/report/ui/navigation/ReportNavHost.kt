package com.rendersoncs.report.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rendersoncs.report.ui.checklist.ChecklistScreen
import com.rendersoncs.report.ui.dashboard.HomeScreen
import com.rendersoncs.report.ui.newreport.NewReportScreen
import com.rendersoncs.report.ui.profile.AboutScreen
import com.rendersoncs.report.ui.profile.ChangePasswordScreen
import com.rendersoncs.report.ui.profile.DeleteAccountScreen
import com.rendersoncs.report.ui.resume.ResumeScreen

@Composable
fun ReportNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onLoggedOut: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = ReportRoute.DASHBOARD,
        modifier = modifier
    ) {
        composable(ReportRoute.DASHBOARD) {
            HomeScreen(
                onNewReport = { navController.navigate(ReportRoute.newReport()) },
                onOpenReport = { report ->
                    val id = report.id?.toLong() ?: return@HomeScreen
                    val route = if (report.concluded == false) {
                        ReportRoute.checklist(id)
                    } else {
                        ReportRoute.resume(id)
                    }
                    navController.navigate(route)
                },
                onChangePassword = { navController.navigate(ReportRoute.CHANGE_PASSWORD) },
                onDeleteAccount = { navController.navigate(ReportRoute.DELETE_ACCOUNT) },
                onAbout = { navController.navigate(ReportRoute.SETTINGS) },
                onLoggedOut = onLoggedOut
            )
        }
        composable(
            route = ReportRoute.NEW_REPORT,
            arguments = listOf(
                navArgument("reportId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
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
            ChecklistScreen(
                onBack = { navController.popBackStack() },
                onConcluded = { reportId ->
                    navController.navigate(ReportRoute.resume(reportId)) {
                        popUpTo(ReportRoute.DASHBOARD)
                    }
                }
            )
        }
        composable(
            route = ReportRoute.RESUME,
            arguments = listOf(navArgument("reportId") { type = NavType.LongType })
        ) {
            ResumeScreen(
                onBack = { navController.popBackStack() },
                onEdit = { reportId ->
                    navController.navigate(ReportRoute.editReport(reportId)) {
                        popUpTo(ReportRoute.RESUME) { inclusive = true }
                    }
                },
                onDeleted = { navController.popBackStack() }
            )
        }
        composable(ReportRoute.SETTINGS) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(ReportRoute.CHANGE_PASSWORD) {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(ReportRoute.DELETE_ACCOUNT) {
            DeleteAccountScreen(
                onBack = { navController.popBackStack() },
                onDeleted = onLoggedOut
            )
        }
    }
}
