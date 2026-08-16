package com.rendersoncs.report.model

import com.rendersoncs.report.common.util.ChecklistPhotos

class ReportResumeItems(
        val key: String,
        var title: String,
        var description: String,
        var conformity: Int,
        var note: String,
        var photo: String
    ) {
        val photoPaths: List<String>
            get() = ChecklistPhotos.parse(photo)
    }
