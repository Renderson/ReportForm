package com.rendersoncs.reportform.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.Repo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportResume extends AppCompatActivity {

    private ReportBusiness mReportBusiness;
    TextView companyResume;
    TextView emailResume;
    TextView dateResume;
    TextView listReportResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        companyResume = findViewById(R.id.company_resume);
        emailResume = findViewById(R.id.email_resume);
        dateResume = findViewById(R.id.date_resume);
        listReportResume = findViewById(R.id.list_report_resume);

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        loadReportResume();
    }

    private void loadReportResume() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mReportId = bundle.getInt(ReportConstants.BundleConstants.REPORT_ID);

            Repo repoEntity = this.mReportBusiness.load(mReportId);
            this.companyResume.setText(repoEntity.getCompany());
            this.emailResume.setText(repoEntity.getEmail());
            this.dateResume.setText(repoEntity.getDate());
            this.listReportResume.setText(repoEntity.getListJson());

            if (repoEntity.getListJson() != null) {
                try {
                    String s = repoEntity.getListJson();
                    JSONObject jsonObject = new JSONObject(s);
                    Log.i("log", "Item: " + jsonObject + " jsonObject ");
                    Log.i("log", "Item: " + s + " stringResult ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            setTitle("Resumo Relatório " + repoEntity.getCompany());

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
