package com.rendersoncs.report.common.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.rendersoncs.report.databinding.CustomAlertDialogBinding

class CommonDialog(private val context: Context) {

    private val customAlertDialogBinding: CustomAlertDialogBinding = CustomAlertDialogBinding.inflate(LayoutInflater.from(context))
    private val dialog = Dialog(context).apply {
        setupCommonDialog(customAlertDialogBinding)
    }

    fun showDialog(
        title: String,
        description: String,
        buttonConfirm: String,
        buttonCancel: String? = null,
        confirmListener: ((Unit) -> Unit?)? = null,
        cancelListener: ((Unit) -> Unit?)? = null,
    ) {
        with(customAlertDialogBinding) {
            this.txtTitle.text = title
            this.txtDescription.text = description
            this.confirm.text = buttonConfirm
            if (buttonCancel == null ) {
                this.cancel.hide()
            } else {
                this.cancel.text = buttonCancel
            }
            confirm.setOnClickListener {
                dialog.dismiss()
                confirmListener?.invoke(Unit)
            }
            cancel.setOnClickListener {
                dialog.dismiss()
                cancelListener?.invoke(Unit)
            }
        }
        avoidException()
    }

    private fun avoidException() {
        runCatching {
            if (dialog.isShowing.not()) {
                dialog.show()
            }
        }
    }
}

fun Dialog.setupCommonDialog(binding: CustomAlertDialogBinding) {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.setCancelable(false)
    this.setContentView(binding.root)
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}