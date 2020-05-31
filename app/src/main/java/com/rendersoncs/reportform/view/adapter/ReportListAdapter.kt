package com.rendersoncs.reportform.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.itens.ReportItems
import com.rendersoncs.reportform.view.adapter.listener.OnInteractionListener
import com.rendersoncs.reportform.view.adapter.viewHolder.ReportListViewHolder

class ReportListAdapter(private val mRepoEntityList: List<ReportItems>,
                        private val mOnInteractionListener: OnInteractionListener) :
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