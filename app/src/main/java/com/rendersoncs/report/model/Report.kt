package com.rendersoncs.report.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "all_reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,
    @ColumnInfo(name = "company")
    var company: String? = null,
    @ColumnInfo(name = "email")
    var email: String? = null,
    @ColumnInfo(name = "date")
    var date: String? = null,
    @ColumnInfo(name = "controller")
    var controller: String? = null,
    @ColumnInfo(name = "score")
    var score: String? = null,
    @ColumnInfo(name = "result")
    var result: String? = null,
    @ColumnInfo(name = "concluded")
    var concluded: Boolean? = false,
    @ColumnInfo(name = "userId")
    val userId: String? = null
) : Serializable {
    fun dateFormatter(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.parse(date ?: "")
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return formatter.format(date!!)
    }

    fun companyFormatter(): String {
        return this.company?.replace(" ", "-").toString()
    }
}