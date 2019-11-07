package com.rendersoncs.reportform.viewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rendersoncs.reportform.R;

class HeaderViewHolderExp extends RecyclerView.ViewHolder {
    @BindView(R.id.header_id)

    public TextView headerTitle;

    public HeaderViewHolderExp(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
