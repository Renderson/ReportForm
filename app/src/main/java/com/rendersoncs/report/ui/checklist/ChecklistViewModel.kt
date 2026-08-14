package com.rendersoncs.report.ui.checklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.repository.AuthRepository
import com.rendersoncs.report.repository.ChecklistCatalogItem
import com.rendersoncs.report.repository.ChecklistCatalogRepository
import com.rendersoncs.report.repository.ReportRepository
import com.rendersoncs.report.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: ChecklistCatalogRepository,
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val reportId: Int = savedStateHandle.get<Long>(REPORT_ID_KEY)?.toInt() ?: 0

    private val _uiState = MutableStateFlow(ChecklistUiState())
    val uiState: StateFlow<ChecklistUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<ChecklistEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private var autoSaveJob: Job? = null
    private var hasUnsavedChanges = false

    init {
        load()
        startAutoSave()
    }

    fun load() {
        if (reportId <= 0) {
            viewModelScope.launch { eventsChannel.send(ChecklistEvent.Closed) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(listState = UiState.Loading) }
            try {
                val report = reportRepository.getReportById(reportId)
                val catalog = catalogRepository.loadCatalog()
                val savedByKey = reportRepository.getReportWithChecklist(reportId.toString())
                    .flatMap { it.checkList }
                    .associateBy { it.key }
                val items = mergeCatalogWithAnswers(catalog, savedByKey)
                _uiState.update {
                    it.copy(
                        report = report,
                        items = items,
                        listState = if (items.isEmpty()) UiState.Empty else UiState.Success(items)
                    )
                }
                refreshScore()
            } catch (e: Exception) {
                _uiState.update { it.copy(listState = UiState.Error(e.message.orEmpty())) }
                eventsChannel.send(ChecklistEvent.Error(e.message.orEmpty()))
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onFilterChange(filter: ChecklistFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun selectConformity(key: String, conformity: Int) {
        updateItem(key) { item ->
            item.copy(
                conformity = if (item.conformity == conformity) {
                    ChecklistItemUi.UNANSWERED
                } else {
                    conformity
                }
            )
        }
        refreshScore()
    }

    fun setNote(key: String, note: String) {
        updateItem(key) { it.copy(note = note) }
    }

    fun prepareMedia(key: String) {
        _uiState.update { it.copy(pendingMediaKey = key) }
    }

    fun onPhotoPicked(path: String) {
        val key = _uiState.value.pendingMediaKey ?: return
        updateItem(key) {
            it.copy(
                photoPath = path,
                conformity = if (it.conformity == ChecklistItemUi.UNANSWERED) {
                    ChecklistItemUi.C
                } else {
                    it.conformity
                }
            )
        }
        _uiState.update { it.copy(pendingMediaKey = null) }
        refreshScore()
    }

    fun resetItem(key: String) {
        updateItem(key) {
            it.copy(
                conformity = ChecklistItemUi.UNANSWERED,
                note = "",
                photoPath = ""
            )
        }
        refreshScore()
    }

    fun addExtraItem(title: String, description: String) {
        viewModelScope.launch {
            if (!catalogRepository.hasInternet()) {
                eventsChannel.send(ChecklistEvent.Message(R.string.checklist_offline_edit))
                return@launch
            }
            try {
                val created = catalogRepository.addItem(title.trim(), description.trim())
                val item = created.toUi()
                _uiState.update { state ->
                    val items = state.items + item
                    state.copy(
                        items = items,
                        listState = UiState.Success(items)
                    )
                }
                eventsChannel.send(ChecklistEvent.Message(R.string.txt_new_item_list))
            } catch (e: Exception) {
                eventsChannel.send(ChecklistEvent.Error(e.message.orEmpty()))
            }
        }
    }

    fun updateExtraItem(key: String, title: String, description: String) {
        viewModelScope.launch {
            if (!catalogRepository.hasInternet()) {
                eventsChannel.send(ChecklistEvent.Message(R.string.checklist_offline_edit))
                return@launch
            }
            try {
                catalogRepository.updateItem(key, title.trim(), description.trim())
                updateItem(key) { it.copy(title = title.trim(), description = description.trim()) }
            } catch (e: Exception) {
                eventsChannel.send(ChecklistEvent.Error(e.message.orEmpty()))
            }
        }
    }

    fun removeExtraItem(key: String) {
        viewModelScope.launch {
            if (!catalogRepository.hasInternet()) {
                eventsChannel.send(ChecklistEvent.Message(R.string.checklist_offline_edit))
                return@launch
            }
            try {
                catalogRepository.removeItem(key)
                _uiState.update { state ->
                    val items = state.items.filterNot { it.key == key }
                    state.copy(
                        items = items,
                        listState = if (items.isEmpty()) UiState.Empty else UiState.Success(items)
                    )
                }
                refreshScore()
            } catch (e: Exception) {
                eventsChannel.send(ChecklistEvent.Error(e.message.orEmpty()))
            }
        }
    }

    fun clearAnswers() {
        hasUnsavedChanges = true
        _uiState.update { state ->
            state.copy(
                items = state.items.map {
                    it.copy(conformity = ChecklistItemUi.UNANSWERED, note = "", photoPath = "")
                }
            )
        }
        refreshScore()
        eventsChannel.sendEvent(ChecklistEvent.Message(R.string.label_empty_list))
    }

    fun requestConclude() {
        val answered = _uiState.value.items.filter { it.isAnswered }
        viewModelScope.launch {
            when {
                answered.isEmpty() -> eventsChannel.send(ChecklistEvent.Message(R.string.alert_empty_report_text))
                answered.any { it.conformity == ChecklistItemUi.NC && !it.hasPhoto } -> {
                    eventsChannel.send(ChecklistEvent.Message(R.string.alert_check_list_text))
                }
                else -> _uiState.update { it.copy(showConcludeDialog = true) }
            }
        }
    }

    fun dismissConcludeDialog() {
        _uiState.update { it.copy(showConcludeDialog = false) }
    }

    fun confirmConclude() {
        _uiState.update { it.copy(showConcludeDialog = false) }
        conclude()
    }

    fun closeReport() {
        viewModelScope.launch {
            val state = _uiState.value
            val answered = state.items.any { it.isAnswered }
            if (answered) {
                persist(concluded = false)
            } else {
                deleteOrphanPhotos(state.items)
                reportRepository.deleteChecklistPhotos(reportId)
                reportRepository.deleteReportByID(reportId)
            }
            eventsChannel.send(ChecklistEvent.Closed)
        }
    }

    private fun conclude() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val saved = persist(concluded = true)
            if (saved) {
                runCatching { reportRepository.generatePdf(reportId) }
                eventsChannel.send(ChecklistEvent.Concluded(reportId.toLong()))
            } else {
                eventsChannel.send(ChecklistEvent.Message(R.string.txt_error_save))
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(AUTO_SAVE_MS.milliseconds)
            while (isActive) {
                if (hasUnsavedChanges && _uiState.value.items.any { it.isAnswered }) {
                    if (persist(concluded = false)) {
                        eventsChannel.send(ChecklistEvent.Message(R.string.txt_information_saved_automatically))
                    }
                }
                delay(AUTO_SAVE_MS.milliseconds)
            }
        }
    }

    private suspend fun persist(concluded: Boolean): Boolean {
        val state = _uiState.value
        val report = state.report ?: return false
        if (!concluded && !hasUnsavedChanges) return true
        return try {
            reportRepository.updateReport(
                id = reportId,
                report = Report(
                    id = reportId,
                    company = report.company,
                    email = report.email,
                    date = report.date,
                    controller = report.controller,
                    score = state.score.toString(),
                    result = state.resultLabel,
                    concluded = concluded,
                    userId = report.userId ?: authRepository.currentUid.orEmpty()
                )
            )
            reportRepository.deleteCheckList(reportId)
            state.items.filter { it.isAnswered }.forEach { item ->
                reportRepository.insertCheckList(
                    ReportCheckList(
                        reportId = reportId,
                        key = item.key,
                        title = item.title,
                        description = item.description,
                        note = item.note,
                        photo = item.photoPath.ifBlank { ReportConstants.PHOTO.NOT_PHOTO },
                        conformity = item.conformity
                    )
                )
            }
            hasUnsavedChanges = false
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun deleteOrphanPhotos(items: List<ChecklistItemUi>) {
        withContext(Dispatchers.IO) {
            items.filter { it.hasPhoto }.forEach { item ->
                runCatching { File(item.photoPath).delete() }
            }
        }
    }

    private fun mergeCatalogWithAnswers(
        catalog: List<ChecklistCatalogItem>,
        savedByKey: Map<String, ReportCheckList>
    ): List<ChecklistItemUi> {
        val fromCatalog = catalog.map { template ->
            val saved = savedByKey[template.key]
            template.toUi().copy(
                conformity = saved?.conformity?.takeIf { it in ChecklistItemUi.C..ChecklistItemUi.NC }
                    ?: ChecklistItemUi.UNANSWERED,
                note = saved?.note.orEmpty(),
                photoPath = saved?.photo
                    ?.takeIf { it.isNotBlank() && it != ReportConstants.PHOTO.NOT_PHOTO }
                    .orEmpty()
            )
        }
        val extraSaved = savedByKey.values
            .filter { saved -> fromCatalog.none { it.key == saved.key } }
            .map { saved ->
                ChecklistItemUi(
                    key = saved.key,
                    title = saved.title,
                    description = saved.description,
                    conformity = saved.conformity.takeIf { it in ChecklistItemUi.C..ChecklistItemUi.NC }
                        ?: ChecklistItemUi.UNANSWERED,
                    note = saved.note,
                    photoPath = saved.photo
                        .takeIf { it.isNotBlank() && it != ReportConstants.PHOTO.NOT_PHOTO }
                        .orEmpty()
                )
            }
        return fromCatalog + extraSaved
    }

    private fun updateItem(key: String, transform: (ChecklistItemUi) -> ChecklistItemUi) {
        hasUnsavedChanges = true
        _uiState.update { state ->
            state.copy(items = state.items.map { item ->
                if (item.key == key) transform(item) else item
            })
        }
    }

    private fun refreshScore() {
        val ncCount = _uiState.value.items.count { it.conformity == ChecklistItemUi.NC }
        val score = (STARTING_SCORE - ncCount * LOSE_POINTS).coerceAtLeast(0f)
        val result = if (score >= PASS_SCORE) CONFORME else NAO_CONFORME
        _uiState.update { it.copy(score = score, resultLabel = result) }
    }

    private fun ChecklistCatalogItem.toUi() = ChecklistItemUi(
        key = key,
        title = title,
        description = description
    )

    private fun Channel<ChecklistEvent>.sendEvent(event: ChecklistEvent) {
        viewModelScope.launch { send(event) }
    }

    private companion object {
        const val REPORT_ID_KEY = "reportId"
        const val AUTO_SAVE_MS = 60_000L
        const val STARTING_SCORE = 10f
        const val LOSE_POINTS = 0.7f
        const val PASS_SCORE = 5f
        const val CONFORME = "CONFORME"
        const val NAO_CONFORME = "NÃO CONFORME"
    }
}
