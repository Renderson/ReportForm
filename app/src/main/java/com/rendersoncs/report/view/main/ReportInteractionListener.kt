package com.rendersoncs.report.view.main

interface ReportInteractionListener {
    /* Click for List Report*/
    fun onClickReport(id: Int)

    /* Click open PDF*/
    fun onOpenPdf(id: Int)

    /* Click for Delete Report*/
    fun onDeleteReport(id: Int)

    /* Click for Share Report*/
    fun onShareReport(id: Int)

    /* Click for Edit Report*/
    fun onEditReport(id: Int)
}