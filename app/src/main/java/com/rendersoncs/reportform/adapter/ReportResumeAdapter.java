package com.rendersoncs.reportform.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.fragment.FullPhotoFragment;
import com.rendersoncs.reportform.itens.ReportResumeItems;
import com.rendersoncs.reportform.listener.OnItemListenerClicked;

import java.util.Arrays;
import java.util.List;

public class ReportResumeAdapter extends RecyclerView.Adapter<ReportResumeAdapter.ViewHolder> {

    private List<ReportResumeItems> repoResumeList;
    private Context context;

    private OnItemListenerClicked onItemListenerClicked;

    public void setOnItemListenerClicked(OnItemListenerClicked onItemListenerClicked) {
        this.onItemListenerClicked = onItemListenerClicked;
    }

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        int position = holder.getAdapterPosition();
        final ReportResumeItems resumeItems = repoResumeList.get(position);

        holder.resume_title.setText(resumeItems.getTitle());
        holder.resume_description.setText(resumeItems.getDescription());
        holder.resume_conformity.setText(resumeItems.getConformity());
        holder.resume_note.setText(resumeItems.getNote());

        holder.resume_photo.setOnClickListener(view -> {
            onItemListenerClicked.fullPhoto(position);
        });
    }

    @Override
    public int getItemCount() {
        return this.repoResumeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  {

        private TextView resume_title, resume_description, resume_conformity, resume_note, resume_photo;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            resume_title = itemView.findViewById(R.id.jo_title);
            resume_description = itemView.findViewById(R.id.jo_description);
            resume_conformity = itemView.findViewById(R.id.jo_conformity);
            resume_note = itemView.findViewById(R.id.jo_note);
            resume_photo = itemView.findViewById(R.id.jo_photo);
        }
    }
}
