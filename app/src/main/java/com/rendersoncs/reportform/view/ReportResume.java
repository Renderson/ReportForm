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
    TextView companyResume;
    TextView emailResume;
    TextView dateResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        companyResume = findViewById(R.id.company_resume);
        emailResume = findViewById(R.id.email_resume);
        dateResume = findViewById(R.id.date_resume);

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        loadReportResume();
    }

    private void loadReportResume() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            int mReportId = bundle.getInt(ReportConstants.BundleConstants.REPORT_ID);

            Repo repoEntity = this.mReportBusiness.load(mReportId);
            this.companyResume.setText(repoEntity.getCompany());
            this.emailResume.setText(repoEntity.getEmail());
            this.dateResume.setText(repoEntity.getDate());
            setTitle("Resumo Relatório " + repoEntity.getCompany());

        }else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
