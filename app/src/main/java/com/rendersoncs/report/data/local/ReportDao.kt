package com.rendersoncs.report.data.local

import androidx.room.*
import com.rendersoncs.report.model.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    // used to insert new report
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report)

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
}