package com.rendersoncs.report.common.util

import android.util.Patterns

fun isValidateEmail(s: String): Boolean {
    val email = s.trim { it <= ' ' }
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
