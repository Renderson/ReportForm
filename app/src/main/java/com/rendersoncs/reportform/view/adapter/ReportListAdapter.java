package com.rendersoncs.reportform.view.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.view.adapter.listener.OnInteractionListener;
import com.rendersoncs.reportform.view.adapter.viewHolder.ReportListViewHolder;

import java.util.List;

import com.rendersoncs.reportform.R;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListViewHolder> {

    private List<ReportItems> mRepoEntityList;
    private OnInteractionListener mOnInteractionListener;

    public ReportListAdapter(List<ReportItems> repoEntityList, OnInteractionListener listener) {
        this.mRepoEntityList = repoEntityList;
        this.mOnInteractionListener = listener;
    }

    @NonNull
    @Override
    public ReportListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_main_list, parent, false);

        return new ReportListViewHolder(view, context);

    }

    @Override
    public void onBindViewHolder(@NonNull final ReportListViewHolder holder, int position) {
        ReportItems repoEntity = this.mRepoEntityList.get(position);
        holder.bindData(repoEntity, mOnInteractionListener);
    }

    @Override
    public int getItemCount() {
        return this.mRepoEntityList.size();
    }

}
