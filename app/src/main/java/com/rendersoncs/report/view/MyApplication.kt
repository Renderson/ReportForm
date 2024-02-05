package com.rendersoncs.report.view

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.rendersoncs.report.common.constants.ReportConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    private lateinit var preference: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        preference = getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, Context.MODE_PRIVATE)
        val position = preference.getInt(ReportConstants.THEME.KEY_THEME, THEME_DAY)

        if (position == THEME_DAY) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (position == THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        // AdMob
        MobileAds.initialize(applicationContext)
    }

    companion object {
        lateinit var appContext: Context
        const val THEME_DAY = 0
        const val THEME_DARK = 1
    }
}