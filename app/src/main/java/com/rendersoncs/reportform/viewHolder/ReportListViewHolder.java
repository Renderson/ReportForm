package com.rendersoncs.reportform.viewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.listener.OnReportListenerInteractionListener;

import com.rendersoncs.reportform.R;

public class ReportListViewHolder extends RecyclerView.ViewHolder {

    private TextView CompanyView;
    private TextView DateView;
    private ImageView overflow;
    private Context mContext;

    public ReportListViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.CompanyView = itemView.findViewById(R.id.companyView);
        this.DateView = itemView.findViewById(R.id.dateView);
        this.overflow = itemView.findViewById(R.id.overflow);
        this.mContext = context;
    }

    public void bindData(final Repo repoEntity, final OnReportListenerInteractionListener listener) {
        this.CompanyView.setText(repoEntity.getCompany());
        this.DateView.setText(repoEntity.getDate());

        this.CompanyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListClick(repoEntity.getId());
            }
        });

        this.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, overflow);
                popupMenu.inflate(R.menu.menu_report);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                listener.onListClick(repoEntity.getId());
                                return true;

                            case R.id.action_remove:
                                new AlertDialog.Builder(mContext)
                                        .setTitle(R.string.alert_remove_report)
                                        .setMessage(R.string.txt_remove_report)
                                        .setPositiveButton(R.string.txt_continue, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Delete item
                                                listener.onDeleteClick(repoEntity.getId());
                                            }
                                        })
                                        .setNeutralButton(R.string.txt_cancel, null)
                                        .show();
                                return true;

                            case R.id.action_share:
                                Toast.makeText(mContext, R.string.txt_share_report, Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

    }
}

