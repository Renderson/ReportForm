package com.rendersoncs.report.ui.checklist

import androidx.annotation.StringRes
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.ui.common.UiState

data class ChecklistItemUi(
    val key: String,
    val title: String,
    val description: String,
    val conformity: Int = UNANSWERED,
    val note: String = "",
    val photoPath: String = ""
) {
    val isAnswered: Boolean get() = conformity == C || conformity == NA || conformity == NC
    val hasPhoto: Boolean
        get() = photoPath.isNotBlank() && photoPath != ReportConstants.PHOTO.NOT_PHOTO
    val hasNote: Boolean get() = note.isNotBlank()

    companion object {
        const val UNANSWERED = -1
        const val C = ReportConstants.ITEM.OPT_NUM1
        const val NA = ReportConstants.ITEM.OPT_NUM2
        const val NC = ReportConstants.ITEM.OPT_NUM3
    }
}

enum class ChecklistFilter {
    ALL,
    CONFORME,
    NAO_CONFORME,
    NAO_APLICAVEL
}

data class ChecklistUiState(
    val listState: UiState<List<ChecklistItemUi>> = UiState.Loading,
    val report: Report? = null,
    val items: List<ChecklistItemUi> = emptyList(),
    val query: String = "",
    val filter: ChecklistFilter = ChecklistFilter.ALL,
    val score: Float = 10f,
    val resultLabel: String = "",
    val isSaving: Boolean = false,
    val pendingMediaKey: String? = null,
    val showConcludeDialog: Boolean = false
) {
    val visibleItems: List<ChecklistItemUi>
        get() {
            val needle = query.trim()
            return items.filter { item ->
                val matchesQuery = needle.isEmpty() ||
                    item.title.contains(needle, ignoreCase = true) ||
                    item.description.contains(needle, ignoreCase = true)
                val matchesFilter = when (filter) {
                    ChecklistFilter.ALL -> true
                    ChecklistFilter.CONFORME -> item.conformity == ChecklistItemUi.C
                    ChecklistFilter.NAO_CONFORME -> item.conformity == ChecklistItemUi.NC
                    ChecklistFilter.NAO_APLICAVEL -> item.conformity == ChecklistItemUi.NA
                }
                matchesQuery && matchesFilter
            }
        }

    val answeredCount: Int get() = items.count { it.isAnswered }
}

sealed interface ChecklistEvent {
    data object Closed : ChecklistEvent
    data class Concluded(val reportId: Long) : ChecklistEvent
    data class Message(@StringRes val textRes: Int) : ChecklistEvent
    data class Error(val message: String) : ChecklistEvent
}
