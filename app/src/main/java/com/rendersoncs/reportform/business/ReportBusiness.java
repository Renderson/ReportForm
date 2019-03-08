package com.rendersoncs.reportform.business;

import android.content.Context;

import com.rendersoncs.reportform.constants.DataBaseConstants;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.repository.ReportRepository;

import java.util.List;

public class ReportBusiness {

    private ReportRepository mReportRepository;

    public ReportBusiness(Context context){
        this.mReportRepository = ReportRepository.getInstance(context);
    }

    public Boolean insert(Repo repo){
        return this.mReportRepository.insert(repo);
    }

    public Boolean remove(int reportId){
        return this.mReportRepository.remove(reportId);
    }

    public Repo load(int reportId){
        return this.mReportRepository.load(reportId);
    }

    public List<Repo> getInvited(){
        return this.mReportRepository.getReportByQuery("select * from " + DataBaseConstants.REPORT.TABLE_NAME);
    }

}
