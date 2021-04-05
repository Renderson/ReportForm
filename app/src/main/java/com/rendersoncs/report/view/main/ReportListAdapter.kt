package com.rendersoncs.report.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rendersoncs.report.R
import com.rendersoncs.report.model.ReportItems

class ReportListAdapter(private val mRepoEntityList: List<ReportItems>,
                        private val mOnInteractionListener: ReportInteractionListener) :
        RecyclerView.Adapter<ReportListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ReportListViewHolder {
        //val context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.activity_main_list, parent, false)
        return ReportListViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
        val repoEntity = mRepoEntityList[position]
        holder.bindData(repoEntity, mOnInteractionListener)
    }

    override fun getItemCount(): Int {
        return mRepoEntityList.size
    }
}