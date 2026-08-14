package com.rendersoncs.report.ui.newreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.common.util.SharePrefInfoUser
import com.rendersoncs.report.common.util.isValidateEmail
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.repository.AuthRepository
import com.rendersoncs.report.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
    private val sharePref: SharePrefInfoUser
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NewReportUiState(
            date = today(),
            controller = sharePref.getUser().ifBlank {
                authRepository.currentUser?.displayName.orEmpty()
            }
        )
    )
    val uiState: StateFlow<NewReportUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<NewReportEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onCompanyChange(value: String) {
        _uiState.update {
            it.copy(
                company = value.take(COMPANY_MAX_LENGTH),
                companyError = false
            )
        }
    }

    fun onEmailChange(value: String) {
        val trimmed = value.trim()
        _uiState.update {
            it.copy(
                email = value,
                emailError = trimmed.isNotEmpty() && !isValidateEmail(trimmed)
            )
        }
    }

    fun onDateChange(value: String) {
        _uiState.update { it.copy(date = value) }
    }

    fun onControllerChange(value: String) {
        _uiState.update {
            it.copy(controller = value, controllerError = false)
        }
    }

    fun startReport() {
        val state = _uiState.value
        val companyInvalid = state.company.isBlank()
        val emailInvalid = state.email.isBlank() || !isValidateEmail(state.email.trim())
        val controllerInvalid = state.controller.isBlank()

        if (companyInvalid || emailInvalid || controllerInvalid) {
            _uiState.update {
                it.copy(
                    companyError = companyInvalid,
                    emailError = emailInvalid,
                    controllerError = controllerInvalid
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val id = reportRepository.insertReport(
                    Report(
                        company = state.company.trim(),
                        email = state.email.trim(),
                        date = state.date,
                        controller = state.controller.trim(),
                        score = "",
                        result = "",
                        concluded = false,
                        userId = authRepository.currentUid.orEmpty()
                    )
                )
                eventsChannel.send(NewReportEvent.Started(id.toInt()))
            } catch (_: Exception) {
                eventsChannel.send(NewReportEvent.SaveFailed)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private companion object {
        const val COMPANY_MAX_LENGTH = 20

        fun today(): String {
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
    }
}
