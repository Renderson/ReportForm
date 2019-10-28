package com.rendersoncs.reportform.listener;

import java.io.File;

public interface OnInteractionListener {

    /* Click for List Report*/
    void onListClick(int reportId);

    /* Click open PDF*/
    void onOpenPdf(int reportId);

    /* Click for Delete Report*/
    void onDeleteClick(int id);

    /* Click for Share Report*/
    void onShareReport(int reportId);

    /* Click for Edit Report*/
    void onEditReport(int id);
}
