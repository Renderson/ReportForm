package com.rendersoncs.reportform.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.listener.OnInteractionListener;
import com.rendersoncs.reportform.viewHolder.ReportListViewHolder;

import java.util.List;

import com.rendersoncs.reportform.R;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListViewHolder> {

    private List<Repo> mRepoEntityList;
    private OnInteractionListener mOnInteractionListener;
    Context context;

    public ReportListAdapter(List<Repo> repoEntityList, OnInteractionListener listener) {
        this.mRepoEntityList = repoEntityList;
        this.mOnInteractionListener = listener;
    }

    @NonNull
    @Override
    public ReportListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_report_list_entry, parent, false);

        return new ReportListViewHolder(view, context);

    }

    @Override
    public void onBindViewHolder(@NonNull final ReportListViewHolder holder, int position) {
        Repo repoEntity = this.mRepoEntityList.get(position);
        holder.bindData(repoEntity, mOnInteractionListener);
    }

    @Override
    public int getItemCount() {
        return this.mRepoEntityList.size();
    }

}
