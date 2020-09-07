package com.rendersoncs.reportform.view.activitys.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.services.constants.ReportConstants

class HomeActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        preference = getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, Context.MODE_PRIVATE)
        val position = preference.getInt(ReportConstants.THEME.KEY_THEME, THEME_DAY)

        if (position == THEME_DAY) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (position == THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun callLogin(view: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun callSignUp(view: View?) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val THEME_DAY = 0
        const val THEME_DARK = 1
    }
}