package com.rendersoncs.report.common.util

import com.rendersoncs.report.common.constants.ReportConstants
import org.json.JSONArray

object ChecklistPhotos {
    const val MAX_PER_ITEM = 3

    fun parse(raw: String?): List<String> {
        val value = raw.orEmpty().trim()
        if (value.isBlank() || value == ReportConstants.PHOTO.NOT_PHOTO) return emptyList()
        if (value.startsWith("[")) {
            return runCatching {
                val array = JSONArray(value)
                buildList {
                    for (index in 0 until array.length()) {
                        val path = array.optString(index)
                        if (path.isNotBlank() && path != ReportConstants.PHOTO.NOT_PHOTO) {
                            add(path)
                        }
                    }
                }
            }.getOrElse { listOf(value) }
        }
        return listOf(value)
    }

    fun encode(paths: List<String>): String {
        val clean = paths
            .filter { it.isNotBlank() && it != ReportConstants.PHOTO.NOT_PHOTO }
            .take(MAX_PER_ITEM)
        if (clean.isEmpty()) return ReportConstants.PHOTO.NOT_PHOTO
        if (clean.size == 1) return clean.first()
        val array = JSONArray()
        clean.forEach { array.put(it) }
        return array.toString()
    }
}
