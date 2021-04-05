package com.rendersoncs.report.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.rendersoncs.report.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun callLogin(view: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}