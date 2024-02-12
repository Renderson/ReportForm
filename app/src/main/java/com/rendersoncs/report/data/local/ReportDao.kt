package com.rendersoncs.report.data.local

import androidx.room.*
import com.rendersoncs.report.data.local.relations.ReportWithCheckList
import com.rendersoncs.report.data.local.relations.UserWithReport
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.model.User

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // used to insert new report
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report): Long

    // used to insert check list
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckList(report: ReportCheckList): Long

    // used to delete report
    @Delete
    suspend fun deleteReport(report: Report)

    // get single report by id
    @Query("SELECT * FROM all_reports WHERE id = :id")
    suspend fun getReportByID(id: Int): Report

    // delete report by id
    @Query("DELETE FROM all_reports WHERE id = :id")
    suspend fun deleteReportByID(id: Int)

    @Query("DELETE FROM ReportCheckList WHERE reportId = :id")
    suspend fun deleteCheckListByID(id: Int)

    @Transaction
    @Query("SELECT * FROM all_reports WHERE userId = :userId")
    suspend fun getUserWithReports(userId: String): List<UserWithReport>

    @Transaction
    @Query("SELECT * FROM all_reports WHERE id = :id")
    suspend fun getReportWithCheckList(id: String): List<ReportWithCheckList>

    @Query("UPDATE all_reports SET " +
    "company = :newCompany, " +
    "email = :newEmail, " +
    "date = :newDate, " +
    "controller = :newController, " +
    "score = :newScore, " +
    "result = :newResult, " +
    "concluded = :newConcluded, " +
    "userId = :newUserId " +
    "WHERE id = :id")
    suspend fun updateAllReport(
        id: Int,
        newCompany: String,
        newEmail: String,
        newDate: String,
        newController: String,
        newScore: String,
        newResult: String,
        newConcluded: Boolean,
        newUserId: String
    )
}