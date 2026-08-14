package com.rendersoncs.report.ui.checklist

import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterListOff
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendersoncs.report.AdManager
import com.rendersoncs.report.BuildConfig
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.util.ReportFiles
import com.rendersoncs.report.ui.checklist.components.ChecklistEmpty
import com.rendersoncs.report.ui.checklist.components.ChecklistItemCard
import com.rendersoncs.report.ui.checklist.components.ChecklistProgressHeader
import com.rendersoncs.report.ui.checklist.components.ChecklistSkeleton
import com.rendersoncs.report.ui.common.UiState
import com.rendersoncs.report.ui.components.ReportExtendedFab
import com.rendersoncs.report.ui.components.SnackbarBottomOverlay
import com.rendersoncs.report.ui.components.SnackbarFabState
import com.rendersoncs.report.ui.components.paddingAboveSnackbar
import com.rendersoncs.report.ui.components.rememberSnackbarFabState
import com.rendersoncs.report.ui.dashboard.components.DashboardSearchBar
import com.rendersoncs.report.ui.dashboard.components.SearchFilterOption
import com.rendersoncs.report.ui.camera.CameraActivity
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.util.Locale

@Composable
fun ChecklistScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onConcluded: (Long) -> Unit,
    viewModel: ChecklistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarFab = rememberSnackbarFabState()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val adManager = remember(activity) {
        activity?.let { host ->
            val adUnitId = if (BuildConfig.BUILD_TYPE != "release") {
                ReportConstants.ADMOB.ADMOB_HLG
            } else {
                ReportConstants.ADMOB.ADMOB_PROD
            }
            AdManager(host, adUnitId)
        }
    }
    var showCloseDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var noteItem by remember { mutableStateOf<ChecklistItemUi?>(null) }
    var editorItem by remember { mutableStateOf<ChecklistItemUi?>(null) }
    var showNewItemDialog by remember { mutableStateOf(false) }
    var itemToRemove by remember { mutableStateOf<ChecklistItemUi?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != ReportConstants.PHOTO.REQUEST_CAMERA_X) return@rememberLauncherForActivityResult
        val extras = result.data?.extras ?: return@rememberLauncherForActivityResult
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getSerializable(ReportConstants.PHOTO.RESULT_CAMERA_X, File::class.java)
        } else {
            @Suppress("DEPRECATION")
            extras.getSerializable(ReportConstants.PHOTO.RESULT_CAMERA_X) as? File
        }
        file?.path?.let(viewModel::onPhotoPicked)
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val copied = uri?.let { ReportFiles.copyUriToAppFile(context, it) }
        copied?.path?.let(viewModel::onPhotoPicked)
    }

    val adManagerRef = rememberUpdatedState(adManager)

    LaunchedEffect(adManager) {
        adManager?.loadAdMob()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ChecklistEvent.Closed -> onBack()
                is ChecklistEvent.Concluded -> {
                    val openResume = { onConcluded(event.reportId) }
                    adManagerRef.value?.showAdMob(openResume) ?: openResume()
                }
                is ChecklistEvent.Message -> snackbarFab.hostState.showSnackbar(context.getString(event.textRes))
                is ChecklistEvent.Error -> {
                    val message = event.message.ifBlank { context.getString(R.string.txt_error_save) }
                    snackbarFab.hostState.showSnackbar(message)
                }
            }
        }
    }

    BackHandler { showCloseDialog = true }

    if (showCloseDialog) {
        ConfirmDialog(
            title = stringResource(R.string.alert_leave_the_report),
            text = stringResource(R.string.alert_leave_the_report_text),
            onConfirm = {
                showCloseDialog = false
                viewModel.closeReport()
            },
            onDismiss = { showCloseDialog = false }
        )
    }
    if (showClearDialog) {
        ConfirmDialog(
            title = stringResource(R.string.alert_clear_list),
            text = stringResource(R.string.alert_clear_list_text),
            onConfirm = {
                showClearDialog = false
                viewModel.clearAnswers()
            },
            onDismiss = { showClearDialog = false }
        )
    }
    if (state.showConcludeDialog) {
        val result = state.resultLabel.lowercase(Locale.getDefault())
        ConfirmDialog(
            title = stringResource(R.string.alert_punctuation),
            text = stringResource(R.string.alert_punctuation_label1, result) +
                " ${state.answeredCount} " +
                stringResource(R.string.alert_punctuation_label2, state.score.toString()),
            onConfirm = viewModel::confirmConclude,
            onDismiss = viewModel::dismissConcludeDialog
        )
    }
    noteItem?.let { item ->
        NoteDialog(
            initialValue = item.note,
            onConfirm = { note ->
                viewModel.setNote(item.key, note)
                noteItem = null
            },
            onDismiss = { noteItem = null }
        )
    }
    if (showNewItemDialog) {
        ItemEditorDialog(
            dialogTitle = stringResource(R.string.insert),
            initialTitle = "",
            initialDescription = "",
            confirmLabel = stringResource(R.string.insert),
            onConfirm = { title, description ->
                viewModel.addExtraItem(title, description)
                showNewItemDialog = false
            },
            onDismiss = { showNewItemDialog = false }
        )
    }
    editorItem?.let { item ->
        ItemEditorDialog(
            dialogTitle = stringResource(R.string.checklist_edit_item),
            initialTitle = item.title,
            initialDescription = item.description,
            confirmLabel = stringResource(R.string.change),
            onConfirm = { title, description ->
                viewModel.updateExtraItem(item.key, title, description)
                editorItem = null
            },
            onDismiss = { editorItem = null }
        )
    }
    itemToRemove?.let { item ->
        ConfirmDialog(
            title = stringResource(R.string.checklist_remove_item),
            text = stringResource(R.string.label_remove_item_list),
            onConfirm = {
                viewModel.removeExtraItem(item.key)
                itemToRemove = null
            },
            onDismiss = { itemToRemove = null }
        )
    }

    ChecklistContent(
        state = state,
        snackbarFab = snackbarFab,
        onBack = { showCloseDialog = true },
        onSave = viewModel::requestConclude,
        onClear = { showClearDialog = true },
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onSelectConformity = viewModel::selectConformity,
        onCamera = { key ->
            viewModel.prepareMedia(key)
            cameraLauncher.launch(Intent(context, CameraActivity::class.java))
        },
        onGallery = { key ->
            viewModel.prepareMedia(key)
            galleryLauncher.launch("image/*")
        },
        onNote = { noteItem = it },
        onEdit = { editorItem = it },
        onReset = viewModel::resetItem,
        onRemove = { itemToRemove = it },
        onAddItem = { showNewItemDialog = true },
        onRetry = viewModel::load,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChecklistContent(
    modifier: Modifier = Modifier,
    state: ChecklistUiState,
    snackbarFab: SnackbarFabState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterChange: (ChecklistFilter) -> Unit,
    onSelectConformity: (String, Int) -> Unit,
    onCamera: (String) -> Unit,
    onGallery: (String) -> Unit,
    onNote: (ChecklistItemUi) -> Unit,
    onEdit: (ChecklistItemUi) -> Unit,
    onReset: (String) -> Unit,
    onRemove: (ChecklistItemUi) -> Unit,
    onAddItem: () -> Unit,
    onRetry: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val fabExpanded by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.label_menu_new_report),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.leave)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = stringResource(R.string.txt_to_clean)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (state.listState !is UiState.Loading && state.listState !is UiState.Error) {
                ReportExtendedFab(
                    text = stringResource(R.string.checklist_add_extra),
                    icon = Icons.Rounded.Add,
                    onClick = onAddItem,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
            DashboardSearchBar(
                query = state.query,
                onQueryChange = onQueryChange,
                onFilterChange = { onFilterChange(ChecklistFilter.valueOf(it)) },
                selectedFilterId = state.filter.name,
                placeholder = stringResource(R.string.checklist_search_hint),
                filters = listOf(
                    SearchFilterOption(
                        id = ChecklistFilter.ALL.name,
                        label = stringResource(R.string.dashboard_filter_all)
                    ),
                    SearchFilterOption(
                        id = ChecklistFilter.CONFORME.name,
                        label = stringResource(R.string.according)
                    ),
                    SearchFilterOption(
                        id = ChecklistFilter.NAO_CONFORME.name,
                        label = stringResource(R.string.not_according)
                    ),
                    SearchFilterOption(
                        id = ChecklistFilter.NAO_APLICAVEL.name,
                        label = stringResource(R.string.not_applicable)
                    )
                )
            )
            if (state.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                ChecklistProgressHeader(
                    answeredCount = state.answeredCount,
                    totalCount = state.items.size
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                when (val catalogState = state.listState) {
                    UiState.Loading -> ChecklistSkeleton()
                    UiState.Empty -> ChecklistEmpty()
                    is UiState.Error -> ChecklistEmpty(
                        icon = Icons.Outlined.ErrorOutline,
                        title = stringResource(R.string.txt_error_save),
                        subtitle = catalogState.message.ifBlank {
                            stringResource(R.string.label_error_update_list)
                        },
                        actionLabel = stringResource(R.string.checklist_retry),
                        onAction = onRetry
                    )
                    is UiState.Success -> {
                        if (state.visibleItems.isEmpty()) {
                            val hasQuery = state.query.isNotBlank()
                            ChecklistEmpty(
                                icon = if (hasQuery) {
                                    Icons.Outlined.SearchOff
                                } else {
                                    Icons.Outlined.FilterListOff
                                },
                                title = stringResource(R.string.checklist_no_results),
                                subtitle = if (hasQuery) {
                                    stringResource(R.string.checklist_no_results_hint)
                                } else {
                                    stringResource(
                                        R.string.checklist_no_filter_results_hint,
                                        filterLabel(state.filter)
                                    )
                                }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = lazyListState,
                                contentPadding = PaddingValues(bottom = 88.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.visibleItems, key = { it.key }) { item ->
                                    ChecklistItemCard(
                                        item = item,
                                        onSelectConformity = { onSelectConformity(item.key, it) },
                                        onCamera = { onCamera(item.key) },
                                        onGallery = { onGallery(item.key) },
                                        onNote = { onNote(item) },
                                        onEdit = { onEdit(item) },
                                        onReset = { onReset(item.key) },
                                        onRemove = { onRemove(item) }
                                    )
                                }
                            }
                        }
                    }
                }
                if (state.isSaving) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun NoteDialog(
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.label_observation)) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.checklist_note_placeholder)) },
                minLines = 3
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value.trim()) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ItemEditorDialog(
    dialogTitle: String,
    initialTitle: String,
    initialDescription: String,
    confirmLabel: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    val canSave = title.isNotBlank() && description.isNotBlank()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle.replaceFirstChar { it.uppercase() }) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.checklist_item_title)) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.checklist_item_description)) },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title.trim(), description.trim()) },
                enabled = canSave
            ) {
                Text(confirmLabel.replaceFirstChar { it.uppercase() })
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun filterLabel(filter: ChecklistFilter): String {
    return when (filter) {
        ChecklistFilter.ALL -> stringResource(R.string.dashboard_filter_all)
        ChecklistFilter.CONFORME -> stringResource(R.string.according)
        ChecklistFilter.NAO_CONFORME -> stringResource(R.string.not_according)
        ChecklistFilter.NAO_APLICAVEL -> stringResource(R.string.not_applicable)
    }
}
