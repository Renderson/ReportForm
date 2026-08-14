package com.rendersoncs.report.ui.profile

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val showPhoto: Boolean = false,
    val darkTheme: Boolean = false
)

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
}
