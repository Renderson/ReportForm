package com.rendersoncs.reportform.viewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.extension.StringExtension;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.listener.OnInteractionListener;

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
        this.CompanyView.setText(StringExtension.limitsText(repoEntity.getCompany(), ReportConstants.CHARACTERS.LIMITS_TEXT));
        this.DateView.setText(repoEntity.getDate());

        itemView.setOnClickListener(v -> listener.onOpenPdf(repoEntity.getId()));

        this.overflow.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(mContext, overflow);
            setForceShowIcon(popupMenu);
            popupMenu.inflate(R.menu.menu_main);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_resume:
                        listener.onListClick(repoEntity.getId());
                        return true;

                    case R.id.action_edit:
                        listener.onEditReport(repoEntity.getId());
                        return true;

                    case R.id.action_share:
                        listener.onShareReport(repoEntity.getId());
                        return true;

                    case R.id.action_remove:
                        new AlertDialog.Builder(mContext)
                                .setTitle(R.string.alert_remove_report)
                                .setMessage(R.string.alert_remove_report_text)
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    // Delete item
                                    listener.onDeleteClick(repoEntity.getId());
                                })
                                .setNeutralButton(R.string.cancel, null)
                                .show();
                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

    }
    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    assert menuPopupHelper != null;
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

