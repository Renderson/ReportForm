package com.rendersoncs.report.model

import java.io.Serializable

data class ReportNew(
        var company: String,
        var email: String,
        var date: String,
        var controller: String
): Serializable