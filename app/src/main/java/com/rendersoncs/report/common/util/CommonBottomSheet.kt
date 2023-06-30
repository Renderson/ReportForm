package com.rendersoncs.report.common.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.CommonBottomSheetBinding

class CommonBottomSheet(context: Context) {

    private val commonBottomSheetBinding: CommonBottomSheetBinding = CommonBottomSheetBinding.inflate(
        LayoutInflater.from(context))
    private val dialog = Dialog(context).apply {
        setupCommonBottomSheet(commonBottomSheetBinding)
    }

    fun showBottomSheet(
        cameraListener: ((Unit) -> Unit?)? = null,
        galleryListener: ((Unit) -> Unit?)? = null,
    ) {
        with(commonBottomSheetBinding) {
            camera.setOnClickListener {
                dialog.dismiss()
                cameraListener?.invoke(Unit)
            }
            gallery.setOnClickListener {
                dialog.dismiss()
                galleryListener?.invoke(Unit)
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

fun Dialog.setupCommonBottomSheet(binding: CommonBottomSheetBinding) {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.setContentView(binding.root)
    this.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    this.window?.attributes?.windowAnimations = R.style.DialogAnimation
    this.window?.setGravity(Gravity.BOTTOM)
}