package com.rendersoncs.report.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.R
import com.rendersoncs.report.common.util.isValidateEmail
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
class AuthViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<AuthEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value, emailError = false, errorMessage = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value, passwordError = false, errorMessage = null)
        }
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value, nameError = false, errorMessage = null)
        }
    }

    fun onCargoChange(value: String) {
        _uiState.update {
            it.copy(cargo = value, cargoError = false, errorMessage = null)
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value, confirmPasswordError = false, errorMessage = null)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun signIn() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        val emailInvalid = email.isEmpty() || !isValidateEmail(email)
        val passwordInvalid = password.isEmpty()

        if (emailInvalid || passwordInvalid) {
            _uiState.update {
                it.copy(
                    emailError = emailInvalid,
                    passwordError = passwordInvalid
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, uid = user.uid, password = "")
                    }
                    eventsChannel.send(AuthEvent.LoggedIn(user.uid))
                }
                .onFailure { error ->
                    val message = error.message
                        ?: getApplication<Application>().getString(R.string.label_failed)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }

    fun signUp() {
        val state = _uiState.value
        val name = state.name.trim()
        val email = state.email.trim()
        val cargo = state.cargo.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword
        val nameInvalid = name.isEmpty()
        val emailInvalid = email.isEmpty() || !isValidateEmail(email)
        val cargoInvalid = cargo.isEmpty()
        val passwordInvalid = password.length < MIN_PASSWORD_LENGTH
        val confirmInvalid = confirmPassword != password || confirmPassword.isEmpty()

        if (nameInvalid || emailInvalid || cargoInvalid || passwordInvalid || confirmInvalid) {
            _uiState.update {
                it.copy(
                    nameError = nameInvalid,
                    emailError = emailInvalid,
                    cargoError = cargoInvalid,
                    passwordError = passwordInvalid,
                    confirmPasswordError = confirmInvalid
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signUp(name, email, password, cargo)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            uid = user.uid,
                            password = "",
                            confirmPassword = ""
                        )
                    }
                    eventsChannel.send(AuthEvent.LoggedIn(user.uid))
                }
                .onFailure { error ->
                    val message = error.message
                        ?: getApplication<Application>().getString(R.string.label_failed)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }

    fun sendRecoveryEmail() {
        val email = _uiState.value.email.trim()
        if (email.isEmpty() || !isValidateEmail(email)) {
            _uiState.update { it.copy(emailError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recoveryEmail = email
                        )
                    }
                    eventsChannel.send(AuthEvent.RecoveryEmailSent)
                }
                .onFailure { error ->
                    val message = error.message
                        ?: getApplication<Application>().getString(R.string.label_failed)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }

    fun restoreSessionIfLogged() {
        val uid = authRepository.currentUid ?: return
        if (_uiState.value.uid != null) return
        _uiState.update { it.copy(uid = uid) }
        eventsChannel.trySend(AuthEvent.LoggedIn(uid))
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
