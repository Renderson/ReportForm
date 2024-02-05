package com.rendersoncs.report.view.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rendersoncs.report.view.introduction.IntroductionActivity
import com.rendersoncs.report.view.login.LoginActivity

class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, LoginActivity::class.java))
        //startActivity(Intent(this, IntroductionActivity::class.java))
        finish()
    }
}