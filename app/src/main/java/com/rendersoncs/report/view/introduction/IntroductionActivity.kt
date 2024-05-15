package com.rendersoncs.report.view.introduction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rendersoncs.report.databinding.ActivityIntroductionBinding
import com.rendersoncs.report.view.login.LoginActivity

class IntroductionActivity: AppCompatActivity() {

    private lateinit var binding: ActivityIntroductionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.setContent {
            IntroductionTest2(onButtonClicked = ::startLogin)
        }
    }

    private fun startLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}