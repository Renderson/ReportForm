package com.rendersoncs.report.infrastructure.util

import com.rendersoncs.report.model.Report

sealed class ViewState {
    object Loading : ViewState()
    object Empty : ViewState()
    data class Success(val report: List<Report>) : ViewState()
    data class Error(val exception: Throwable) : ViewState()
}