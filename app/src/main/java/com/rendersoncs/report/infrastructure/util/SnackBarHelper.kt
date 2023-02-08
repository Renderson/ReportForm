package com.rendersoncs.report.infrastructure.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.rendersoncs.report.R

class SnackBarHelper {
    fun showSnackBar(activity: Activity, id: Int, label: Int) {
        val snk = Snackbar
                .make(activity.findViewById(id),
                        activity.getString(label),
                        Snackbar.LENGTH_LONG)
        configSnackBar(activity, snk)
        snk.show()
    }

    fun showSnackBar(activity: Activity, view: View) {
        val snackBar = Snackbar
            .make(
                view,
                activity.getString(R.string.txt_check_networking),
                Snackbar.LENGTH_LONG
            )
            .setAction(activity.resources.getString(R.string.check)) {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                activity.startActivity(intent)
            }
        configSnackBar(activity, snackBar)
        snackBar.show()
    }

    companion object {
        @JvmStatic
        fun configSnackBar(context: Context, snack: Snackbar) {
            addMargins(snack)
            setRoundBordersBg(context, snack)
            ViewCompat.setElevation(snack.view, 6f)
        }

        private fun addMargins(snk: Snackbar) {
            val params = snk.view.layoutParams as MarginLayoutParams
            params.setMargins(12, 12, 12, 12)
            snk.view.layoutParams = params
        }

        private fun setRoundBordersBg(context: Context, snk: Snackbar) {
            snk.view.background = ContextCompat.getDrawable(context, R.drawable.bg_snackbar)
        }
    }
}