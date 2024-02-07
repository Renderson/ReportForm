package com.rendersoncs.report.common.extension

object StringExtension {
    @JvmStatic
    fun limitsText(text: String?, i: Int): String {
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

fun String.spaceToNewLine(): String {
    return try {
        val index = this.indexOf(' ')
        if (index != -1 && index < this.length - 1) {
            this.replaceFirst(" ", "\n")
        } else { this }
    } catch (exception: Exception) {
        this
    }
}