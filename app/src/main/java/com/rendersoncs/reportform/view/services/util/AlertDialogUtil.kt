package com.rendersoncs.reportform.view.services.util

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class AlertDialogUtil {
    fun showDialog(activity: Activity?, title: String?, msg: String?, positiveLabel: String?,
                   positiveOnClick: DialogInterface.OnClickListener?,
                   negativeLabel: String?, negativeOnClick: DialogInterface.OnClickListener?,
                   isCancelAble: Boolean) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(title)
        builder.setCancelable(isCancelAble)
        builder.setMessage(msg)
        builder.setPositiveButton(positiveLabel, positiveOnClick)
        builder.setNegativeButton(negativeLabel, negativeOnClick)
        val alert = builder.create()
        alert.show()
    }

    fun showDialogScore(activity: Activity?, img: Int, title: String?, msg: String?,
                        positiveLabel: String?,
                        positiveOnClick: DialogInterface.OnClickListener?,
                        negativeLabel: String?, negativeOnClick: DialogInterface.OnClickListener?,
                        isCancelAble: Boolean) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setIcon(img)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(isCancelAble)
        builder.setPositiveButton(positiveLabel, positiveOnClick)
        builder.setNegativeButton(negativeLabel, negativeOnClick)
        val alert = builder.create()
        alert.show()
    }
}