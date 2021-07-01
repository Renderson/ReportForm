package com.rendersoncs.report.view.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rendersoncs.report.databinding.ItemMainListBinding
import com.rendersoncs.report.model.Report

class DashboardAdapter(private var listener: DashboardListener) :
        RecyclerView.Adapter<DashboardViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Report>() {
        override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): DashboardViewHolder {
        val binding = ItemMainListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bindData(item, listener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}