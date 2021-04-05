package com.rendersoncs.report.infrastructure.extension

object StringExtension {
    @JvmStatic
    fun limitsText(text: String?, i: Int): String? {
        return if (text?.length!! > i) {
            val startIndex = 0
            val stringBuilder = StringBuilder()
            val textSize = text.substring(startIndex, i)
            stringBuilder.append(textSize).append("...").toString()
        } else {
            text
        }
    }
}