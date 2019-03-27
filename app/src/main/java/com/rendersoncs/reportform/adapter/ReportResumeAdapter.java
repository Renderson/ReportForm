package com.rendersoncs.reportform.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.itens.ReportResumeItems;

import java.util.List;

public class ReportResumeAdapter extends RecyclerView.Adapter<ReportResumeAdapter.ViewHolder> {

    private List<ReportResumeItems> repoResumeList;
    private Context context;

    public ReportResumeAdapter(List<ReportResumeItems> repoResumeList, Context context){
        this.repoResumeList = repoResumeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_report_resume_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ReportResumeItems repoResumeList = this.repoResumeList.get(position);
        holder.title_list.setText(repoResumeList.getTitle_list());

    }

    @Override
    public int getItemCount() {
        return this.repoResumeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView title_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title_list = itemView.findViewById(R.id.jo_title);
        }
    }
}
