package com.rendersoncs.reportform.view.services.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

class RVEmptyObserver(private val recyclerView: RecyclerView,
                      private val emptyView: View?,
                      private val floatingActionButton: View?) :
        AdapterDataObserver() {

    private fun checkEmpty() {
        if (emptyView != null && recyclerView.adapter != null) {
            val emptyViewVisible = recyclerView.adapter!!.itemCount == 0
            emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            recyclerView.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
        if (floatingActionButton != null && recyclerView.adapter != null) {
            val emptyViewVisible5 = recyclerView.adapter!!.itemCount == 0
            floatingActionButton.visibility = if (emptyViewVisible5) View.GONE else View.VISIBLE
            recyclerView.visibility = if (emptyViewVisible5) View.GONE else View.VISIBLE
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