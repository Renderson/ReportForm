package com.rendersoncs.reportform.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.adapter.checkListAdapter.ReportRecyclerView
import com.rendersoncs.reportform.view.services.constants.ReportConstants
import java.util.*

class ReportNoteFragment(private val mAdapter: ReportRecyclerView) : DialogFragment() {

    private var note: EditText? = null
    private var getNote: String? = null
    private var position = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = Objects.requireNonNull(activity)?.layoutInflater
        @SuppressLint("InflateParams") val view = inflater?.inflate(R.layout.fragment_report_note, null)

        if (view != null) {
            note = view.findViewById(R.id.txt_note)
        }

        this.checkArguments()

        val alertButton: String = changeTextButtonDialog()

        return showAlertDialog(view, alertButton)
    }

    private fun showAlertDialog(view: View?, alertButton: String): AlertDialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(view)
                .setNegativeButton(resources.getString(R.string.cancel)) { _: DialogInterface?, _: Int -> }
                .setPositiveButton(alertButton) { _: DialogInterface?, _: Int ->
                    if (getNote == null) {
                        insertNewNote()
                    } else {
                        updateNote()
                    }
                }
        return builder.create()
    }

    private fun checkArguments() {
        if (arguments != null) {
            position = arguments!!.getInt(ReportConstants.ITEM.POSITION)
            getNote = arguments!!.getString(ReportConstants.ITEM.NOTE)
            note!!.setText(getNote)
        }
    }

    private fun changeTextButtonDialog(): String {
        return if (getNote != null) {
            resources.getString(R.string.change)
        } else {
            resources.getString(R.string.insert)
        }
    }

    private fun insertNewNote() {
        val newNote = note!!.text.toString()
        mAdapter.insertNote(position, newNote)
        Log.d("NoteFrag ", "InsertNote $position$newNote")
    }

    private fun updateNote() {
        if (arguments != null) {
            val updateNote = note!!.text.toString()
            mAdapter.insertNote(position, updateNote)
            Log.d("NoteFrag ", "UpdateNote $position$updateNote")
        }
    }

    override fun onDetach() {
        super.onDetach()
        dismiss()
    }
}