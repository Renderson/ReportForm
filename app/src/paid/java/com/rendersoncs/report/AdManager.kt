package com.rendersoncs.report

import android.app.Activity

class AdManager(private val context: Activity, private val config: String) {

    fun loadAdMob() {
        // It doesn't do anything in the paid version
    }

    fun showAdMob(onAdDismissed: () -> Unit) {
        // It doesn't do anything in the paid version
        onAdDismissed()
    }
}
