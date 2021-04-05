package com.rendersoncs.report.view.report

import com.rendersoncs.report.model.ReportItems

interface ReportListener {
    /* Click choice radio button*/
    fun radioItemChecked(reportItems: ReportItems, optNum: Int)

    /* Click for take photo*/
    fun takePhoto(reportItems: ReportItems)

    /* Click for show details photo*/
    fun fullPhoto(reportItems: ReportItems)

    /* Click for insert note*/
    fun insertNote(reportItems: ReportItems)

    /* Click for update item list*/
    fun updateList(reportItems: ReportItems)

    /* Click for remove item list*/
    fun removeItem(reportItems: ReportItems)

    /* Click for reset list*/
    fun resetItem(reportItems: ReportItems)
}