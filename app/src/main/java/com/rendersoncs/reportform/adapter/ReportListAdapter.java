package com.rendersoncs.reportform.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View view = layoutInflater.inflate(R.layout.activity_main_list, parent, false);

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
