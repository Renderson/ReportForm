package com.rendersoncs.report.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.util.SharePrefInfoUser
import com.rendersoncs.report.repository.AuthRepository
import com.rendersoncs.report.ui.theme.ThemeSettings
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
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharePref: SharePrefInfoUser,
    private val themeSettings: ThemeSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(darkTheme = themeSettings.darkTheme.value)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            themeSettings.darkTheme.collect { dark ->
                _uiState.update { it.copy(darkTheme = dark) }
            }
        }
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val firebaseUser = authRepository.currentUser
            var name = ""
            var email = firebaseUser?.email.orEmpty()
            var photo = firebaseUser?.photoUrl?.toString().orEmpty()

            if (sharePref.getKey(ReportConstants.FIREBASE.FIRE_NAME)) {
                name = sharePref.getUser()
                email = sharePref.getEmail().ifBlank { email }
                photo = sharePref.getPhoto().ifBlank { photo }
            } else {
                name = firebaseUser?.displayName.orEmpty()
                if (name.isBlank() || photo.isBlank()) {
                    val (rtdbName, rtdbPhoto) = authRepository.fetchCredential()
                    if (name.isBlank()) name = rtdbName.orEmpty()
                    if (photo.isBlank()) photo = rtdbPhoto.orEmpty()
                }
                if (name.isNotBlank()) sharePref.saveUserSharePref(name)
                if (email.isNotBlank()) sharePref.saveEmailSharePref(email)
                if (photo.isNotBlank()) sharePref.savePhotoSharePref(photo)
            }

            _uiState.update {
                it.copy(
                    name = name,
                    email = email,
                    photoUrl = photo,
                    showPhoto = SHOW_PROFILE_PHOTO
                )
            }
        }
    }

    fun onDarkThemeChange(enabled: Boolean) {
        themeSettings.setDarkTheme(enabled)
    }

    fun logout() {
        authRepository.signOut()
        sharePref.deleteSharePref()
        viewModelScope.launch {
            eventsChannel.send(ProfileEvent.LoggedOut)
        }
    }

    private companion object {
        const val SHOW_PROFILE_PHOTO = false
    }
}
