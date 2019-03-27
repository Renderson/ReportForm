package com.rendersoncs.reportform.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportResumeAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.itens.ReportResumeItems;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportResume extends AppCompatActivity {

    private ReportBusiness mReportBusiness;
    TextView companyResume;
    TextView emailResume;
    TextView dateResume;
    TextView listReportResume;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ReportResumeItems> repoResumeList;


    String titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.resume_list);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repoResumeList = new ArrayList<>();

        companyResume = findViewById(R.id.company_resume);
        emailResume = findViewById(R.id.email_resume);
        dateResume = findViewById(R.id.date_resume);
        listReportResume = findViewById(R.id.list_report_resume);

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        this.loadReportResume();
    }

    private void loadReportResume() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mReportId = bundle.getInt(ReportConstants.BundleConstants.REPORT_ID);

            Repo repoEntity = this.mReportBusiness.load(mReportId);
            this.companyResume.setText(repoEntity.getCompany());
            this.emailResume.setText(repoEntity.getEmail());
            this.dateResume.setText(repoEntity.getDate());
            //this.listReportResume.setText(repoEntity.getListJson());

            try {
                JSONArray array = new JSONArray(repoEntity.getListJson());
                Log.i("log", "Item: " + array + " array ");

                for (int i = 0; i < array.length(); i++) {
                    Log.i("log", "Item: " + i + " listI ");
                    JSONObject jo = array.getJSONObject(i);
                    Log.i("log", "Item: " + jo + " stringResult ");

                    ReportResumeItems repoJson = new ReportResumeItems(jo.getString("title_list"));
                    repoResumeList.add(repoJson);

                    //titulo = (String) jsonObject.getString("title_list");
                    //Log.i("log", "Item: " + titulo + " title_list ");
                }
                adapter = new ReportResumeAdapter(repoResumeList, this);
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //this.listReportResume.setText(titulo);

            setTitle("Resumo Relatório " + repoEntity.getCompany());

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
