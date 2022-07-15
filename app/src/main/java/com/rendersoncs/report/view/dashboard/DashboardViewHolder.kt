package com.rendersoncs.report.view.dashboard

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ItemMainListBinding
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.extension.StringExtension.limitsText
import com.rendersoncs.report.model.Report
import kotlinx.android.synthetic.main.item_main_list.view.*
import java.util.*

class DashboardViewHolder(val binding: ItemMainListBinding, context: Context) : RecyclerView.ViewHolder(binding.root) {

    private val mContext = context
    private val result = mContext.getString(R.string.according)
    private val colorAccording = ContextCompat.getColor(mContext, R.color.colorRadioC)
    private val colorNotAccording = ContextCompat.getColor(mContext, R.color.colorRadioNC)

    fun bindData(report: Report, listener: DashboardListener) = with(binding) {

        companyView.text = limitsText(report.company, ReportConstants.CHARACTERS.LIMITS_TEXT)
        dateView.text = report.date
        resultView.text = report.result

        val getResult = report.result

        if (getResult == result.toUpperCase(Locale.ROOT)) {
            changeColorShape(colorAccording)
        } else {
            changeColorShape(colorNotAccording)
        }

        itemView.setOnClickListener { listener.onOpenPdf(report) }
        overflow.setOnClickListener {
            val popupMenu = PopupMenu(mContext, overflow)
            setForceShowIcon(popupMenu)
            popupMenu.inflate(R.menu.menu_main)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_resume -> {
                        listener.onClickReport(report)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_edit -> {
                        listener.onEditReport(report.id)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_share -> {
                        listener.onShareReport(report)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_remove -> {
                        AlertDialog.Builder(mContext)
                                .setTitle(R.string.alert_remove_report)
                                .setMessage(R.string.alert_remove_report_text)
                                .setPositiveButton(R.string.confirm) { _: DialogInterface?, _: Int ->
                                    // Delete item
                                    listener.onDeleteReport(report)
                                }
                                .setNeutralButton(R.string.cancel, null)
                                .show()
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popupMenu.show()
        }
    }

    private fun changeColorShape(color: Int) {
        val bgShape = binding.viewResult.background as GradientDrawable
        bgShape.setColor(color)
    }

    private fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val mFields = popupMenu.javaClass.declaredFields
            for (field in mFields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]!!
                    val popupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val mMethods = popupHelper.getMethod("setForceShowIcon",
                            Boolean::class.javaPrimitiveType)
                    mMethods.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}