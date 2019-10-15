package com.rendersoncs.reportform.viewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.extension.StringExtension;
import com.rendersoncs.reportform.listener.OnInteractionListener;

import com.rendersoncs.reportform.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    public void bindData(final ReportItems repoEntity, final OnInteractionListener listener) {
        //this.CompanyView.setDescription(repoEntity.getCompany());
        this.CompanyView.setText(StringExtension.limitsText(repoEntity.getCompany(), ReportConstants.ConstantsCharacters.LIMITS_TEXT));
        this.DateView.setText(repoEntity.getDate());
        //this.DateView.setDescription((CharSequence) StringExtension.formataParaBrasileiro(repoEntity.getDate()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOpenPdf(repoEntity.getId());
            }
        });

        this.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //listener.onBottomSheet(repoEntity.getId());

                PopupMenu popupMenu = new PopupMenu(mContext, overflow);
                setForceShowIcon(popupMenu);
                popupMenu.inflate(R.menu.menu_report);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                listener.onListClick(repoEntity.getId());
                                return true;

                            case R.id.action_share:
                                listener.onShareReport(repoEntity.getId());
                                return true;

                            case R.id.action_remove:
                                new AlertDialog.Builder(mContext)
                                        .setTitle(R.string.alert_remove_report)
                                        .setMessage(R.string.txt_remove_report)
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Delete item
                                                listener.onDeleteClick(repoEntity.getId());
                                            }
                                        })
                                        .setNeutralButton(R.string.cancel, null)
                                        .show();
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
    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

