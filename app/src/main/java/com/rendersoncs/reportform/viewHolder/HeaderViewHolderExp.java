package com.rendersoncs.reportform.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rendersoncs.reportform.R;

public class HeaderViewHolderExp extends RecyclerView.ViewHolder {
    @BindView(R.id.header_id)

    public TextView headerTitle;

    public HeaderViewHolderExp(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
