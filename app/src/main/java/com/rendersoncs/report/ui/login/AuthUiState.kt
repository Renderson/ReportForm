package com.rendersoncs.report.ui.login

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val errorMessage: String? = null,
    val uid: String? = null,
    val recoveryEmail: String = "",
    val name: String = "",
    val cargo: String = "",
    val confirmPassword: String = "",
    val confirmPasswordVisible: Boolean = false,
    val nameError: Boolean = false,
    val cargoError: Boolean = false,
    val confirmPasswordError: Boolean = false
)

sealed interface AuthEvent {
    data class LoggedIn(val uid: String) : AuthEvent
    data object RecoveryEmailSent : AuthEvent
}
