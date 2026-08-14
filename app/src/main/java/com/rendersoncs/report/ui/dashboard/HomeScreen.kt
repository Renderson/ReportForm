package com.rendersoncs.report.ui.dashboard

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendersoncs.report.R
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.ui.components.ReportExtendedFab
import com.rendersoncs.report.ui.components.SnackbarBottomOverlay
import com.rendersoncs.report.ui.components.paddingAboveSnackbar
import com.rendersoncs.report.ui.components.rememberSnackbarFabState
import com.rendersoncs.report.ui.profile.ProfileScreen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNewReport: () -> Unit,
    onOpenReport: (Report) -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    onAbout: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.AUDITS) }
    val listState = rememberLazyListState()
    val fabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackbarFab = rememberSnackbarFabState()
    var reportToDelete by remember { mutableStateOf<Report?>(null) }
    val pdfMissing = stringResource(R.string.resume_pdf_unavailable)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadReports()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DashboardEvent.SharePdf -> sharePdf(context, event)
                DashboardEvent.PdfMissing -> snackbarFab.hostState.showSnackbar(pdfMissing)
            }
        }
    }

    reportToDelete?.let { report ->
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            title = { Text(stringResource(R.string.alert_remove_report)) },
            text = {
                Text(
                    stringResource(
                        R.string.dashboard_delete_confirm,
                        report.company.orEmpty().ifBlank { stringResource(R.string.title_report) }
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReport(report)
                        reportToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == HomeTab.PROFILE) {
                            stringResource(R.string.dashboard_tab_profile)
                        } else {
                            stringResource(R.string.title_report)
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == HomeTab.AUDITS) {
                ReportExtendedFab(
                    text = stringResource(R.string.label_menu_new_report),
                    icon = Icons.Rounded.Add,
                    onClick = onNewReport,
                    expanded = fabExpanded,
                    modifier = Modifier.paddingAboveSnackbar(snackbarFab)
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == HomeTab.AUDITS,
                    onClick = { selectedTab = HomeTab.AUDITS },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Assignment,
                            contentDescription = stringResource(R.string.dashboard_tab_audits)
                        )
                    },
                    label = { Text(stringResource(R.string.dashboard_tab_audits)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.PROFILE,
                    onClick = { selectedTab = HomeTab.PROFILE },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = stringResource(R.string.dashboard_tab_profile)
                        )
                    },
                    label = { Text(stringResource(R.string.dashboard_tab_profile)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { innerPadding ->
        SnackbarBottomOverlay(
            state = snackbarFab,
            modifier = Modifier.padding(innerPadding)
        ) {
            when (selectedTab) {
                HomeTab.AUDITS -> AuditoriasScreen(
                    state = state,
                    onQueryChange = viewModel::onQueryChange,
                    onFilterChange = viewModel::onFilterChange,
                    onOpenReport = onOpenReport,
                    onShareReport = viewModel::shareReport,
                    onDeleteReport = { reportToDelete = it },
                    listState = listState
                )
                HomeTab.PROFILE -> ProfileScreen(
                    onChangePassword = onChangePassword,
                    onDeleteAccount = onDeleteAccount,
                    onAbout = onAbout,
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}

private fun sharePdf(context: android.content.Context, event: DashboardEvent.SharePdf) {
    val body = context.getString(R.string.label_attach_report, event.company) + " " + event.date
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, event.uri)
        type = "application/pdf"
        putExtra(Intent.EXTRA_SUBJECT, event.subject)
        putExtra(Intent.EXTRA_EMAIL, arrayOf(event.email))
        putExtra(Intent.EXTRA_TEXT, body)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
}
