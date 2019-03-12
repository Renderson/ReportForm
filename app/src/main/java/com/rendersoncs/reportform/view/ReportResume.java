package com.rendersoncs.reportform.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.Repo;


public class ReportResume extends AppCompatActivity {

    private ReportBusiness mReportBusiness;

    TextView companyDetail;
    private int mReportId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle("Detalhes Relatório" + companyDetail);

        companyDetail = findViewById(R.id.company_detail);

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        loadDataFromActivity();


    }

    private void loadDataFromActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            this.mReportId = bundle.getInt(ReportConstants.BundleConstants.REPORT_ID);

            Repo repoEntity = this.mReportBusiness.load(this.mReportId);
            this.companyDetail.setText(repoEntity.getCompany());
            setTitle("Resumo Relatório " + repoEntity.getCompany());

        }else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
