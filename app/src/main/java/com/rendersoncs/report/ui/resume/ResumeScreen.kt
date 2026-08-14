package com.rendersoncs.report.ui.resume

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.ui.components.ReportExtendedFab
import com.rendersoncs.report.ui.components.SnackbarBottomOverlay
import com.rendersoncs.report.ui.components.SnackbarFabState
import com.rendersoncs.report.ui.components.paddingAboveSnackbar
import com.rendersoncs.report.ui.components.rememberSnackbarFabState
import com.rendersoncs.report.ui.resume.components.ResumeDonutChart
import com.rendersoncs.report.ui.resume.components.ResumeItemCard
import com.rendersoncs.report.ui.resume.components.ResumePhoto
import com.rendersoncs.report.ui.theme.ConformGreen
import com.rendersoncs.report.ui.theme.NonConformRed
import com.rendersoncs.report.ui.theme.NotApplicable
import com.rendersoncs.report.ui.theme.ReportShapes
import com.rendersoncs.report.ui.theme.ReportTheme
import com.rendersoncs.report.ui.theme.ResumeWarningContainer
import com.rendersoncs.report.ui.theme.ResumeWarningOn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ResumeScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarFab = rememberSnackbarFabState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pdfMissing = stringResource(R.string.resume_pdf_unavailable)
    val pdfNoApp = stringResource(R.string.resume_pdf_no_app)
    val noPhoto = stringResource(R.string.label_nothing_image)
    val lifecycleOwner = LocalLifecycleOwner.current
    var selectedItem by remember { mutableStateOf<ReportResumeItems?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ResumeEvent.OpenPdf -> openPdf(context, event.uri, pdfNoApp, snackbarFab.hostState)
                is ResumeEvent.SharePdf -> sharePdf(context, event)
                ResumeEvent.PdfMissing -> snackbarFab.hostState.showSnackbar(pdfMissing)
                ResumeEvent.Deleted -> onDeleted()
                ResumeEvent.LoadFailed -> onBack()
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.alert_remove_report)) },
            text = { Text(stringResource(R.string.alert_remove_report_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteReport()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    selectedItem?.let { item ->
        ResumePhotoSheet(
            item = item,
            onDismiss = { selectedItem = null }
        )
    }

    ResumeContent(
        state = state,
        snackbarFab = snackbarFab,
        onBack = onBack,
        onShare = viewModel::sharePdf,
        onDelete = { showDeleteDialog = true },
        onOpenPdf = viewModel::openPdf,
        onEdit = {
            val id = state.report?.id?.toLong() ?: return@ResumeContent
            onEdit(id)
        },
        onPhotoClick = { item -> selectedItem = item },
        onNoPhoto = { scope.launch { snackbarFab.hostState.showSnackbar(noPhoto) } },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResumeContent(
    modifier: Modifier = Modifier,
    state: ResumeUiState,
    snackbarFab: SnackbarFabState,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onOpenPdf: () -> Unit,
    onEdit: () -> Unit,
    onPhotoClick: (ReportResumeItems) -> Unit,
    onNoPhoto: () -> Unit
) {
    val report = state.report
    val listState = rememberLazyListState()
    val fabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.summary),
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
                actions = {
                    if (state.isConcluded) {
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = stringResource(R.string.share)
                            )
                        }
                    }
                    IconButton(onClick = onDelete, enabled = report != null) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.remove)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (report != null) {
                ReportExtendedFab(
                    text = stringResource(R.string.edit),
                    icon = Icons.Outlined.Edit,
                    onClick = onEdit,
                    expanded = fabExpanded,
                    modifier = Modifier.paddingAboveSnackbar(snackbarFab)
                )
            }
        }
    ) { innerPadding ->
        SnackbarBottomOverlay(
            state = snackbarFab,
            modifier = Modifier.padding(innerPadding)
        ) {
            when {
                state.isLoading && report == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                report == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dashboard_no_results),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ResumeInfoColumn(report = report)
                    }
                    item {
                        SignedByText(controller = report.controller.orEmpty())
                    }
                    item {
                        PdfButton(
                            enabled = state.isConcluded,
                            onClick = onOpenPdf
                        )
                    }
                    if (!state.isConcluded) {
                        item { PdfLockedBanner() }
                    }
                    item {
                        ResumeStatsCard(
                            accordingCount = state.accordingCount,
                            notApplicableCount = state.notApplicableCount,
                            notAccordingCount = state.notAccordingCount,
                            totalItems = state.items.size
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.label_list_selected),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    itemsIndexed(
                        items = state.items,
                        key = { index, item -> "${item.key}-$index" }
                    ) { _, item ->
                        ResumeItemCard(
                            item = item,
                            onPhotoClick = {
                                val hasPhoto = item.photo.isNotBlank() &&
                                    item.photo != ReportConstants.PHOTO.NOT_PHOTO
                                if (hasPhoto) onPhotoClick(item) else onNoPhoto()
                            }
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ResumeInfoColumn(report: Report) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoCard(
            label = stringResource(R.string.company),
            value = report.company.orEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        InfoCard(
            label = stringResource(R.string.label_date),
            value = report.date.orEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        InfoCard(
            label = stringResource(R.string.mail),
            value = report.email.orEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = ReportShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Text(
                text = label.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SignedByText(controller: String) {
    val prefix = stringResource(R.string.label_report, "").trimEnd()
    Text(
        text = buildAnnotatedString {
            append(prefix)
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(controller)
            }
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun PdfButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = ReportShapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
            disabledContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f)
        )
    ) {
        Text(
            text = stringResource(R.string.resume_view_pdf).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun PdfLockedBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ReportShapes.medium,
        colors = CardDefaults.cardColors(containerColor = ResumeWarningContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ResumeWarningOn),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.resume_pdf_locked),
                style = MaterialTheme.typography.bodyMedium,
                color = ResumeWarningOn
            )
        }
    }
}

@Composable
private fun ResumeStatsCard(
    accordingCount: Int,
    notApplicableCount: Int,
    notAccordingCount: Int,
    totalItems: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ReportShapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.total_items),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (accordingCount > 0) {
                    LegendRow(
                        color = ConformGreen,
                        text = "${stringResource(R.string.according)}: $accordingCount"
                    )
                }
                if (notApplicableCount > 0) {
                    LegendRow(
                        color = NotApplicable,
                        text = "${stringResource(R.string.not_applicable)}: $notApplicableCount"
                    )
                }
                if (notAccordingCount > 0) {
                    LegendRow(
                        color = NonConformRed,
                        text = "${stringResource(R.string.not_according)}: $notAccordingCount"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ResumeDonutChart(
                accordingCount = accordingCount,
                notApplicableCount = notApplicableCount,
                notAccordingCount = notAccordingCount,
                totalItems = totalItems
            )
        }
    }
}

@Composable
private fun LegendRow(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResumePhotoSheet(
    item: ReportResumeItems,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            ResumePhoto(
                path = item.photo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.note.ifBlank { stringResource(R.string.label_not_observation) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private suspend fun openPdf(
    context: android.content.Context,
    uri: Uri,
    noAppMessage: String,
    snackbarHostState: SnackbarHostState
) {
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    } catch (_: ActivityNotFoundException) {
        snackbarHostState.showSnackbar(noAppMessage)
    }
}

private fun sharePdf(context: android.content.Context, event: ResumeEvent.SharePdf) {
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ResumeContentPreview() {
    ReportTheme {
        ResumeContent(
            state = ResumeUiState(
                isLoading = false,
                report = Report(
                    id = 1,
                    company = "dgdfd",
                    email = "renderson@teste.com",
                    date = "14/08/2026",
                    controller = "Renderson Cerqueira",
                    concluded = false
                ),
                items = listOf(
                    ReportResumeItems(
                        key = "1",
                        title = "1.1.2 Área externa",
                        description = "O estabelecimento possui acesso de entrada e saída de insumos e lixo?",
                        conformity = 1,
                        note = "",
                        photo = "notPhoto"
                    )
                ),
                accordingCount = 1,
                notApplicableCount = 1,
                notAccordingCount = 1
            ),
            snackbarFab = rememberSnackbarFabState(),
            onBack = {},
            onShare = {},
            onDelete = {},
            onOpenPdf = {},
            onEdit = {},
            onPhotoClick = {},
            onNoPhoto = {}
        )
    }
}
