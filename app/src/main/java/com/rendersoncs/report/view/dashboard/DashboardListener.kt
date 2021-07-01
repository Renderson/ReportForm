package com.rendersoncs.report.view.dashboard

import com.rendersoncs.report.model.Report

interface DashboardListener {
    /* Click for List Report*/
    fun onClickReport(report: Report)

    /* Click open PDF*/
    fun onOpenPdf(report: Report)

    /* Click for Delete Report*/
    fun onDeleteReport(report: Report)

    /* Click for Share Report*/
    fun onShareReport(report: Report)

    /* Click for Edit Report*/
    fun onEditReport(id: Int)
}