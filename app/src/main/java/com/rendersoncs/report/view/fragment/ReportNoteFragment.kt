package com.rendersoncs.report.view.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rendersoncs.report.R
import com.rendersoncs.report.model.ReportItems
import com.rendersoncs.report.view.report.ReportAdapter
import com.rendersoncs.report.infrastructure.constants.ReportConstants

class ReportNoteFragment(private val adapter: ReportAdapter,
                         private val reportItems: ReportItems) : DialogFragment() {

    private var note: EditText? = null
    private var getNote: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_report_note, null)

        if (view != null) {
            note = view.findViewById(R.id.txt_note)
        }

        this.checkArguments()

        val alertButton: String = changeTextButtonDialog()

        return showAlertDialog(view, alertButton)
    }

    private fun showAlertDialog(view: View?, alertButton: String): AlertDialog {
        val builder = AlertDialog.Builder(requireActivity())
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
            getNote = requireArguments().getString(ReportConstants.ITEM.NOTE)
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
        adapter.insertNote(reportItems, newNote)
    }

    private fun updateNote() {
        if (arguments != null) {
            val updateNote = note!!.text.toString()
            adapter.insertNote(reportItems, updateNote)
        }
    }

    override fun onDetach() {
        super.onDetach()
        dismiss()
    }
}