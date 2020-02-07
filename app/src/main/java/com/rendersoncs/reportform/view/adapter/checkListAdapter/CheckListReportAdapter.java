package com.rendersoncs.reportform.view.adapter.checkListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.itens.ReportItems;

import java.util.List;

public class CheckListReportAdapter extends ReportRecyclerView {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public CheckListReportAdapter(List<ReportItems> reportItems, Context context) {
        super(reportItems, context);
    }

    @NonNull
    @Override
    public CheckListReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        if (i == TYPE_HEADER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, viewGroup, false);
            return new CheckListReportAdapter.HeaderVh(view);
        } else if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_report_list, viewGroup, false);
            return new CheckListReportAdapter.ItemVh(view);
        }
        throw new RuntimeException("No macth for" + i + ".");
    }

    public int getItemViewType(int i) {
        if (isPositionHeader(i))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        // Position Header
        return position == 0 ||
                position == 30 ||
                position == 45 ||
                position == 103 || //i == 0 || i == 21 || i == 30
                position == 122 || //i == 0 || i == 21 || i == 30
                position == 133; //i == 0 || i == 21 || i == 30
    }

}
