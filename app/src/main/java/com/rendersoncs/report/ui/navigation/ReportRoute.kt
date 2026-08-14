package com.rendersoncs.report.ui.navigation

object ReportRoute {
    const val DASHBOARD = "dashboard"
    const val NEW_REPORT = "new_report?reportId={reportId}"
    const val CHECKLIST = "checklist/{reportId}"
    const val RESUME = "resume/{reportId}"
    const val SETTINGS = "settings"
    const val CHANGE_PASSWORD = "change_password"
    const val DELETE_ACCOUNT = "delete_account"

    fun newReport() = "new_report"
    fun editReport(reportId: Long) = "new_report?reportId=$reportId"
    fun checklist(reportId: Long) = "checklist/$reportId"
    fun resume(reportId: Long) = "resume/$reportId"
}
