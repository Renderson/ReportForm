package com.rendersoncs.report.data.local

import androidx.room.*
import com.rendersoncs.report.data.local.relations.ReportWithCheckList
import com.rendersoncs.report.data.local.relations.UserWithReport
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.model.User
import kotlinx.coroutines.flow.Flow

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

    // used to update report
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateReport(report: Report)

    // used to delete report
    @Delete
    suspend fun deleteReport(report: Report)

    // get all saved report list
    @Query("SELECT * FROM all_reports ORDER by id DESC")
    fun getAllReports(): Flow<List<Report>>

    // get single report by id
    @Query("SELECT * FROM all_reports WHERE id = :id")
    fun getReportByID(id: Int): Flow<Report>

    // delete report by id
    @Query("DELETE FROM all_reports WHERE id = :id")
    suspend fun deleteReportByID(id: Int)

    @Query("DELETE FROM ReportCheckList WHERE id = :id")
    suspend fun deleteCheckListByID(id: Int)

    @Transaction
    @Query("SELECT * FROM all_reports WHERE userId = :userId")
    fun getUserWithReports(userId: String): Flow<List<UserWithReport>>

    @Transaction
    @Query("SELECT * FROM all_reports WHERE id = :id")
    fun getReportWithCheckList(id: String): Flow<List<ReportWithCheckList>>

    @Query("UPDATE all_reports SET concluded = :newConcluded WHERE id = :id")
    suspend fun updateConcluded(id: Int, newConcluded: Boolean)
}