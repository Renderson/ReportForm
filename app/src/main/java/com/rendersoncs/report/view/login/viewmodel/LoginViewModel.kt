package com.rendersoncs.report.view.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.model.User
import com.rendersoncs.report.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val repository: ReportRepository
): AndroidViewModel(application) {

    fun insertUserBD(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }
}