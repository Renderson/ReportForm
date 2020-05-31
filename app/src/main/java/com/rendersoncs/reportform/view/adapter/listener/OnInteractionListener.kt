package com.rendersoncs.reportform.view.adapter.listener

interface OnInteractionListener {
    /* Click for List Report*/
    fun onListClick(id: Int)

    /* Click open PDF*/
    fun onOpenPdf(id: Int)

    /* Click for Delete Report*/
    fun onDeleteClick(id: Int)

    /* Click for Share Report*/
    fun onShareReport(id: Int)

    /* Click for Edit Report*/
    fun onEditReport(id: Int)
}