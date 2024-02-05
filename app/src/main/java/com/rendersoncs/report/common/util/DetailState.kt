package com.rendersoncs.report.common.util

import com.rendersoncs.report.model.Report

sealed class DetailState {
    object Loading : DetailState()
    object Empty : DetailState()
    data class Success(val report: Report) : DetailState()
    data class Error(val exception: Throwable) : DetailState()
}