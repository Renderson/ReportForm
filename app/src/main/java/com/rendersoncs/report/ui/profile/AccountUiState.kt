package com.rendersoncs.report.ui.profile

data class AccountUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val currentVisible: Boolean = false,
    val newVisible: Boolean = false,
    val currentError: Boolean = false,
    val newError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AccountEvent {
    data object PasswordUpdated : AccountEvent
    data object AccountDeleted : AccountEvent
}
