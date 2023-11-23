package com.rendersoncs.report.view.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.rendersoncs.report.databinding.ItemMainListBinding
import com.rendersoncs.report.common.util.ItemCallBack
import com.rendersoncs.report.model.Report

class DashboardAdapter(
    private var listener: (Report) -> Unit
): RecyclerView.Adapter<DashboardViewHolder>() {

    val differ = AsyncListDiffer(this, ItemCallBack<Report>())

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): DashboardViewHolder {
        val binding = ItemMainListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bindData(item, listener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}