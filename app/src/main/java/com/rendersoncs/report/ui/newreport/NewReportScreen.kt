package com.rendersoncs.report.ui.newreport

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.login.components.AuthTextField
import com.rendersoncs.report.ui.theme.PillShape
import com.rendersoncs.report.ui.theme.ReportShapes
import com.rendersoncs.report.ui.theme.ReportTheme
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun NewReportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onStarted: (Int) -> Unit,
    viewModel: NewReportViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveError = stringResource(R.string.txt_error_save)

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is NewReportEvent.Started -> onStarted(event.reportId)
                NewReportEvent.SaveFailed -> snackbarHostState.showSnackbar(saveError)
                NewReportEvent.LoadFailed -> onBack()
            }
        }
    }

    NewReportContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onCompanyChange = viewModel::onCompanyChange,
        onEmailChange = viewModel::onEmailChange,
        onDateChange = viewModel::onDateChange,
        onControllerChange = viewModel::onControllerChange,
        onStart = viewModel::startReport,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewReportContent(
    modifier: Modifier = Modifier,
    state: NewReportUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onCompanyChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onControllerChange: (String) -> Unit,
    onStart: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = ddMMyyyyToUtcMillis(state.date)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateChange(utcMillisToDdMMyyyy(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (state.isEdit) {
                                R.string.title_edit_report
                            } else {
                                R.string.label_menu_new_report
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(ReportShapes.large)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = ReportShapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.new_report_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.new_report_section_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AuthTextField(
                        value = state.company,
                        onValueChange = onCompanyChange,
                        label = stringResource(R.string.new_report_company),
                        placeholder = stringResource(R.string.new_report_company_placeholder),
                        leadingIcon = Icons.Outlined.Apartment,
                        isError = state.companyError,
                        errorText = stringResource(R.string.txt_enter_name_company),
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = stringResource(R.string.new_report_email),
                        placeholder = stringResource(R.string.new_report_email_placeholder),
                        leadingIcon = Icons.Outlined.Email,
                        isError = state.emailError,
                        errorText = stringResource(R.string.txt_email),
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box {
                        AuthTextField(
                            value = state.date,
                            onValueChange = {},
                            label = stringResource(R.string.new_report_date),
                            placeholder = stringResource(R.string.new_report_date),
                            leadingIcon = Icons.Outlined.CalendarMonth,
                            keyboardType = KeyboardType.Text,
                            readOnly = true
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthTextField(
                        value = state.controller,
                        onValueChange = onControllerChange,
                        label = stringResource(R.string.new_report_auditor),
                        placeholder = stringResource(R.string.new_report_auditor_placeholder),
                        leadingIcon = Icons.Outlined.Badge,
                        isError = state.controllerError,
                        errorText = stringResource(R.string.txt_enter_name_controller),
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words,
                        onImeAction = onStart
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onStart,
                enabled = state.isValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.start).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun ddMMyyyyToUtcMillis(date: String): Long? {
    val parts = date.split("/")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.clear()
    calendar.set(year, month - 1, day)
    return calendar.timeInMillis
}

private fun utcMillisToDdMMyyyy(millis: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = millis
    return String.format(
        Locale.getDefault(),
        "%02d/%02d/%04d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR)
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NewReportContentPreview() {
    ReportTheme {
        NewReportContent(
            state = NewReportUiState(
                date = "14/08/2026",
                controller = "João Silva"
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onCompanyChange = {},
            onEmailChange = {},
            onDateChange = {},
            onControllerChange = {},
            onStart = {}
        )
    }
}
