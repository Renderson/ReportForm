package com.rendersoncs.reportform.view.services.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.rendersoncs.reportform.R

class SnackBarHelper {
    fun showSnackBar(activity: Activity, id: Int, label: Int) {
        val snk = Snackbar
                .make(activity.findViewById(id),
                        activity.getString(label),
                        Snackbar.LENGTH_LONG)
        configSnackBar(activity, snk)
        snk.show()
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                params.setMargins(12, 12, 12, 12)
            }
            params.setMargins(8, 8, 8, 8)
            snk.view.layoutParams = params
        }

        private fun setRoundBordersBg(context: Context, snk: Snackbar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                snk.view.background = ContextCompat.getDrawable(context, R.drawable.bg_snackbar)
            }
        }
    }
}