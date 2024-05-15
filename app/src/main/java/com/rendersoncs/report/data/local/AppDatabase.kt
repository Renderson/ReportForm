package com.rendersoncs.report.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.model.User

@Database(
    entities = [
        User::class,
        Report::class,
        ReportCheckList::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getReportDao(): ReportDao
}