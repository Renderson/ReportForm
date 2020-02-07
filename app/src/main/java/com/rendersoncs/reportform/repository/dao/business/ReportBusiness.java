package com.rendersoncs.reportform.repository.dao.business;

import android.content.Context;

import com.rendersoncs.reportform.repository.dao.DataBaseConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.repository.dao.ReportRepository;

import java.util.List;

public class ReportBusiness {

    private ReportRepository mReportRepository;

    public ReportBusiness(Context context){
        this.mReportRepository = ReportRepository.getInstance(context);
    }

    public Boolean insert(ReportItems repo){
        return this.mReportRepository.insert(repo);
    }

    public Boolean update(ReportItems repo){
        return this.mReportRepository.update(repo);
    }

    public void remove(int reportId){
        this.mReportRepository.remove(reportId);
    }

    public ReportItems load(int reportId){
        return this.mReportRepository.load(reportId);
    }

    public List<ReportItems> getInvited(){
        return this.mReportRepository.getReportByQuery("select * from " + DataBaseConstants.REPORT.TABLE_NAME);
    }

    public void close(){
        this.mReportRepository.close();
    }

}
