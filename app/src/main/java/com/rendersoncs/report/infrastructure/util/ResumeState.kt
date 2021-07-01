package com.rendersoncs.report.infrastructure.util

import com.rendersoncs.report.model.ReportResumeItems

sealed class ResumeState {
    object Loading : ResumeState()
    data class Success(val report: ArrayList<ReportResumeItems>) : ResumeState()
    data class Error(val exception: Throwable) : ResumeState()
}