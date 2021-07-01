package com.rendersoncs.report.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "all_reports")
data class Report(
        @ColumnInfo(name = "company")
        var company: String,
        @ColumnInfo(name = "email")
        var email: String,
        @ColumnInfo(name = "date")
        var date: String,
        @ColumnInfo(name = "controller")
        var controller: String,
        @ColumnInfo(name = "score")
        var score: String,
        @ColumnInfo(name = "result")
        var result: String,
        @ColumnInfo(name = "jsonList")
        var listJson: String,
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,
): Serializable {
        fun dateFormatter(): String {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = dateFormat.parse(date)
                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                return formatter.format(date!!)
        }

        fun companyFormatter(): String {
                return this.company.replace(" ", "-")
        }
}