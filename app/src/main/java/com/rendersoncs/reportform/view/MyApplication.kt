package com.rendersoncs.reportform.view

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.rendersoncs.reportform.view.services.constants.ReportConstants
import com.squareup.leakcanary.RefWatcher

internal class MyApplication : Application() {

    private val refWatcher: RefWatcher? = null
    private lateinit var preference: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        /*MultiDex.install(this);
        if (LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        refWatcher = LeakCanary.install(this);*/

        preference = getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, Context.MODE_PRIVATE)
        val position = preference.getInt(ReportConstants.THEME.KEY_THEME, THEME_DAY)

        if (position == THEME_DAY) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (position == THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    } /*public static RefWatcher getRefWatcher(Context context){
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }*/

    companion object {
        const val THEME_DAY = 0
        const val THEME_DARK = 1
    }

}