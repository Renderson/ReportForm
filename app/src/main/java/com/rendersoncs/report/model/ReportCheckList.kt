package com.rendersoncs.report.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReportCheckList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "reportId")
    var reportId: Int,
    @ColumnInfo(name = "key")
    var key: String,
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "note")
    var note: String = "",
    @ColumnInfo(name = "photo")
    var photo: String = "",
    @ColumnInfo(name = "conformity")
    var conformity: Int = 0
)