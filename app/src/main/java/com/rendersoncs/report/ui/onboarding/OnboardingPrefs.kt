package com.rendersoncs.report.ui.onboarding

import android.content.Context
import androidx.core.content.edit

object OnboardingPrefs {
    private const val PREFS = "onboarding_prefs"
    private const val KEY_COMPLETED = "completed"

    fun isCompleted(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_COMPLETED, false)
    }

    fun markCompleted(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit { putBoolean(KEY_COMPLETED, true) }
    }
}
