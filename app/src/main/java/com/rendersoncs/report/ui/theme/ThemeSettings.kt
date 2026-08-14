package com.rendersoncs.report.ui.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.rendersoncs.report.common.constants.ReportConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class ThemeSettings @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(
        ReportConstants.THEME.MY_PREFERENCE_THEME,
        Context.MODE_PRIVATE
    )

    private val _darkTheme = MutableStateFlow(readDarkTheme())
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        val position = if (enabled) THEME_DARK else THEME_DAY
        prefs.edit { putInt(ReportConstants.THEME.KEY_THEME, position) }
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        _darkTheme.value = enabled
    }

    private fun readDarkTheme(): Boolean {
        return prefs.getInt(ReportConstants.THEME.KEY_THEME, THEME_DAY) == THEME_DARK
    }

    private companion object {
        const val THEME_DAY = 0
        const val THEME_DARK = 1
    }
}
