package com.rendersoncs.reportform.view.adapter.viewHolder

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.itens.ReportItems
import com.rendersoncs.reportform.view.adapter.listener.OnInteractionListener
import com.rendersoncs.reportform.view.services.constants.ReportConstants
import com.rendersoncs.reportform.view.services.extension.StringExtension.limitsText
import kotlinx.android.synthetic.main.activity_main_list.view.*
import java.util.*

class ReportListViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    private var companyView = itemView.companyView
    private var dateView = itemView.dateView
    private var view = itemView.viewResult
    private var resultView = itemView.resultView
    private val overflow = itemView.overflow
    private val mContext = context

    private val result = mContext.getString(R.string.according)
    private val colorAccording = ContextCompat.getColor(mContext, R.color.colorRadioC)
    private val colorNotAccording = ContextCompat.getColor(mContext, R.color.colorRadioNC)

    fun bindData(repoEntity: ReportItems, listener: OnInteractionListener) {

        companyView!!.text = limitsText(repoEntity.company, ReportConstants.CHARACTERS.LIMITS_TEXT)
        dateView!!.text = repoEntity.date
        resultView.text = repoEntity.result

        val getResult = repoEntity.result.toString()

        if (getResult == result.toUpperCase(Locale.ROOT)) {
            changeColorShape(colorAccording)
        } else {
            changeColorShape(colorNotAccording)
        }

        itemView.setOnClickListener { listener.onOpenPdf(repoEntity.id) }
        overflow.setOnClickListener {
            val popupMenu = PopupMenu(mContext, overflow)
            setForceShowIcon(popupMenu)
            popupMenu.inflate(R.menu.menu_main)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_resume -> {
                        listener.onListClick(repoEntity.id)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_edit -> {
                        listener.onEditReport(repoEntity.id)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_share -> {
                        listener.onShareReport(repoEntity.id)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_remove -> {
                        AlertDialog.Builder(mContext)
                                .setTitle(R.string.alert_remove_report)
                                .setMessage(R.string.alert_remove_report_text)
                                .setPositiveButton(R.string.confirm) { _: DialogInterface?, _: Int ->
                                    // Delete item
                                    listener.onDeleteClick(repoEntity.id)
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
        val bgShape = view.background as GradientDrawable
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
            e.printStackTrace()
        }
    }
}