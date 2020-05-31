package com.rendersoncs.reportform.view.adapter.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.rendersoncs.reportform.R

internal class HeaderViewHolderExp(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    @BindView(R.id.header_id)
    var headerTitle: TextView? = null

    init {
        ButterKnife.bind(this, itemView)
    }
}