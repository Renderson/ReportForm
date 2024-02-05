package com.rendersoncs.report.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList

data class ReportWithCheckList(
    @Embedded val report: Report,
    @Relation(
        parentColumn = "id",
        entityColumn = "reportId"
    )
    val checkList: List<ReportCheckList>
)