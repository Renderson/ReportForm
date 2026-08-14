package com.rendersoncs.report.common.extension

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun View.applyMainWindowInsets(appBar: View, content: View, navigationView: View) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        appBar.updatePadding(top = bars.top)
        content.updatePadding(
            left = bars.left,
            right = bars.right,
            bottom = bars.bottom
        )
        navigationView.updatePadding(top = bars.top)
        WindowInsetsCompat.CONSUMED
    }
}

fun View.applySystemBarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        view.updatePadding(
            left = bars.left,
            top = bars.top,
            right = bars.right,
            bottom = bars.bottom
        )
        WindowInsetsCompat.CONSUMED
    }
}
