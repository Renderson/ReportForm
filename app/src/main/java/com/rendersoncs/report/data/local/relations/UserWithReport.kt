package com.rendersoncs.report.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.User

data class UserWithReport(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val reports: List<Report>
)