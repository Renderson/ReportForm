package com.rendersoncs.report.ui.newreport

data class NewReportUiState(
    val company: String = "",
    val email: String = "",
    val date: String = "",
    val controller: String = "",
    val companyError: Boolean = false,
    val emailError: Boolean = false,
    val controllerError: Boolean = false,
    val isLoading: Boolean = false
) {
    val isValid: Boolean
        get() = company.isNotBlank() &&
            email.isNotBlank() &&
            !emailError &&
            controller.isNotBlank() &&
            date.isNotBlank()
}

sealed interface NewReportEvent {
    data class Started(val reportId: Int) : NewReportEvent
    data object SaveFailed : NewReportEvent
}
