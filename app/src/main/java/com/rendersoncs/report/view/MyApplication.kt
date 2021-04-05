package com.rendersoncs.report.view

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.rendersoncs.report.infrastructure.constants.ReportConstants

internal class MyApplication : Application() {

    private lateinit var preference: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        preference = getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, Context.MODE_PRIVATE)
        val position = preference.getInt(ReportConstants.THEME.KEY_THEME, THEME_DAY)

        if (position == THEME_DAY) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (position == THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }

    companion object {
        const val THEME_DAY = 0
        const val THEME_DARK = 1
    }
}