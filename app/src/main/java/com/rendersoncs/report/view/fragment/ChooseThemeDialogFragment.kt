package com.rendersoncs.report.view.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants

class ChooseThemeDialogFragment : DialogFragment() {
    private lateinit var preference: SharedPreferences
    private var position = 0 //default selected position

    interface SingleChoiceListener {
        fun onPositiveButtonClicked(list: Array<String>, position: Int)
        fun onNegativeButtonClicked()
    }

    private var mListener: SingleChoiceListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        preference = requireActivity().getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, Context.MODE_PRIVATE)
        mListener = try {
            context as SingleChoiceListener
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            throw ClassCastException(activity.toString() + " SingleChoiceListener must implemented")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val items = arrayOf(
                getString(R.string.clear),
                getString(R.string.dark))

        position = preference.getInt(ReportConstants.THEME.KEY_THEME, 0)

        builder.setTitle(getString(R.string.choice_theme))
                .setSingleChoiceItems(items, position) { _: DialogInterface?, i: Int ->
                    position = i
                }
                .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int ->
                    mListener!!.onPositiveButtonClicked(items, position)
                    dismiss()
                }
                .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int ->
                    mListener!!.onNegativeButtonClicked()
                    dismiss()
                }
        return builder.create()
    }
}