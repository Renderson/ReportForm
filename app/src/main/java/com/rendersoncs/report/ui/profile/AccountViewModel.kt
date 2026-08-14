package com.rendersoncs.report.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.common.util.SharePrefInfoUser
import com.rendersoncs.report.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharePref: SharePrefInfoUser
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<AccountEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.update {
            it.copy(currentPassword = value, currentError = false, errorMessage = null)
        }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update {
            it.copy(newPassword = value, newError = false, errorMessage = null)
        }
    }

    fun toggleCurrentVisible() {
        _uiState.update { it.copy(currentVisible = !it.currentVisible) }
    }

    fun toggleNewVisible() {
        _uiState.update { it.copy(newVisible = !it.newVisible) }
    }

    fun consumeError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updatePassword() {
        val state = _uiState.value
        val currentInvalid = state.currentPassword.isBlank()
        val newInvalid = state.newPassword.length < MIN_PASSWORD_LENGTH
        if (currentInvalid || newInvalid) {
            _uiState.update {
                it.copy(currentError = currentInvalid, newError = newInvalid)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.updatePassword(state.currentPassword, state.newPassword)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentPassword = "",
                            newPassword = ""
                        )
                    }
                    eventsChannel.send(AccountEvent.PasswordUpdated)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun deleteAccount() {
        val password = _uiState.value.currentPassword
        if (password.isBlank()) {
            _uiState.update { it.copy(currentError = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.deleteAccount(password)
                .onSuccess {
                    sharePref.deleteSharePref()
                    _uiState.update { it.copy(isLoading = false) }
                    eventsChannel.send(AccountEvent.AccountDeleted)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
