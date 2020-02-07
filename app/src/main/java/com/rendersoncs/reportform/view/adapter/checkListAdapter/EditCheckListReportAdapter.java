package com.rendersoncs.reportform.view.adapter.checkListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.itens.ReportItems;

import java.util.List;

public class EditCheckListReportAdapter extends ReportRecyclerView {

    public EditCheckListReportAdapter(List<ReportItems> reportItems, Context context) {
        super(reportItems, context);
    }

    @NonNull
    @Override
    public EditCheckListReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_report_list, viewGroup, false);
        return new EditCheckListReportAdapter.ItemVh(view);
    }
}
