package com.rendersoncs.report.repository

import com.rendersoncs.report.data.local.AppDatabase
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.model.User
import javax.inject.Inject

class ReportRepository @Inject constructor(private val db: AppDatabase) {

    // insert user
    suspend fun insertUser(user: User) = db.getReportDao().insertUser(user)

    // get all report for userUid
    suspend fun getUserWithReport(userId: String) = db.getReportDao().getUserWithReports(userId)

    // get reports with checklist
    suspend fun getReportWithChecklist(id: String) = db.getReportDao().getReportWithCheckList(id)

    // insert new report
    suspend fun insertReport(report: Report): Long {
        return db.getReportDao().insertReport(report)
    }

    // insert new check list
    suspend fun insertCheckList(checkList: ReportCheckList): Long {
        return db.getReportDao().insertCheckList(checkList)
    }

    // get report to id
    suspend fun getReportById(id: Int) = db.getReportDao().getReportByID(id)

    // delete report to id
    suspend fun deleteReportByID(id: Int) {
        db.getReportDao().deleteReportByID(id)
        db.getReportDao().deleteCheckListByID(id)
    }

    // delete check list to id
    suspend fun deleteCheckList(id: Int) {
        db.getReportDao().deleteCheckListByID(id)
    }

    // update items report to id
    suspend fun updateReport(id: Int, report: Report) = db.getReportDao()
        .updateAllReport(
            id = id,
            newCompany = report.company.orEmpty(),
            newEmail = report.email.orEmpty(),
            newDate = report.date.orEmpty(),
            newController = report.controller.orEmpty(),
            newScore = report.score.orEmpty(),
            newResult = report.result.orEmpty(),
            newConcluded = report.concluded ?: false,
            newUserId = report.userId.orEmpty()
        )
}