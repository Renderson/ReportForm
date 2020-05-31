package com.rendersoncs.reportform.view.services.service;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.rendersoncs.reportform.repository.dao.business.ReportBusiness;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;

import java.io.File;

public class AccessDocument {
    private final int reportId;
    private ReportItems reportItems;
    private Uri uri;
    private String subject;
    private final ReportBusiness reportBusiness;
    private Context context;

    public AccessDocument(int reportId) {
        this.reportId = reportId;
        this.reportBusiness = new ReportBusiness(context);
    }

    public ReportItems getReportItems() {
        return reportItems;
    }

    public Uri getUri() {
        return uri;
    }

    public String getSubject() {
        return subject;
    }

    public AccessDocument invoke() {
        reportItems = reportBusiness.load(reportId);

        subject = String.format("Report-%s-%s", reportItems.getCompany(), reportItems.getDate());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Report" + "/" + subject + ".pdf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(context, ReportConstants.PACKAGE.FILE_PROVIDER, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return this;
    }
}
