package com.rendersoncs.report.view.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.rendersoncs.report.ui.ReportActivity
import com.rendersoncs.report.ui.login.AuthActivity

class SplashScreenActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (firebaseAuth.currentUser != null) {
            openNext(loggedIn = true)
            return
        }
        val listener = FirebaseAuth.AuthStateListener { auth ->
            openNext(loggedIn = auth.currentUser != null)
        }
        authStateListener = listener
        firebaseAuth.addAuthStateListener(listener)
    }

    private fun openNext(loggedIn: Boolean) {
        if (navigated || isFinishing) return
        navigated = true
        removeAuthListener()
        val destination = if (loggedIn) {
            ReportActivity::class.java
        } else {
            AuthActivity::class.java
        }
        startActivity(
            Intent(this, destination).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
    }

    private fun removeAuthListener() {
        authStateListener?.let { firebaseAuth.removeAuthStateListener(it) }
        authStateListener = null
    }

    override fun onDestroy() {
        removeAuthListener()
        super.onDestroy()
    }
}
