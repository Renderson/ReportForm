package com.rendersoncs.report.repository

import com.rendersoncs.report.data.local.AppDatabase
import com.rendersoncs.report.model.Report
import javax.inject.Inject

class ReportRepository @Inject constructor(private val db: AppDatabase) {

    // get all reports
    fun getAllReports() = db.getReportDao().getAllReports()

    // insert new report
    suspend fun  insertReport(report: Report) = db.getReportDao().insertReport(report)

    // get report to id
    fun getReportById(id: Int) = db.getReportDao().getReportByID(id)

    // delete report to id
    suspend fun deleteReportByID(id: Int) = db.getReportDao().deleteReportByID(id)
}