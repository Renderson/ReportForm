package com.rendersoncs.report.infrastructure.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ReportEmptyObserve(private val recyclerView: RecyclerView,
                         private val emptyView: View?) :
        RecyclerView.AdapterDataObserver() {

    private fun checkEmpty() {
        if (emptyView != null && recyclerView.adapter != null) {
            val emptyViewVisible = recyclerView.adapter!!.itemCount == 0
            emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            recyclerView.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun onChanged() {
        checkEmpty()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        checkEmpty()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        checkEmpty()
    }

    init {
        checkEmpty()
    }
}