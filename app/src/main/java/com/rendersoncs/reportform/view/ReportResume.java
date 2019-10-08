package com.rendersoncs.reportform.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportResumeAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.itens.ReportResumeItems;
import com.rendersoncs.reportform.service.AccessDocument;
import com.rendersoncs.reportform.util.MyDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportResume extends AppCompatActivity {

    private ReportBusiness mReportBusiness;
    private ReportItems repoEntity;
    private TextView emailResume, dateResume, companyResume, itemsResume, openPdf;
    private PieChart pieChart;
    private int mReportId;

    private RecyclerView recyclerView;
    private List<ReportResumeItems> repoResumeList;

    private ArrayList<String> listSelected = new ArrayList<>();
    public ArrayList<String> listYes = new ArrayList<>();
    public ArrayList<String> listNot = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.init();

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        this.loadReportResume();
        this.createPieChart();
        this.openPDF();
    }

    private void init() {
        recyclerView = findViewById(R.id.resume_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        pieChart = findViewById(R.id.pieChart);

        repoResumeList = new ArrayList<>();

        companyResume = findViewById(R.id.company_resume);
        emailResume = findViewById(R.id.email_resume);
        dateResume = findViewById(R.id.date_resume);
        itemsResume = findViewById(R.id.items_resume);
        openPdf = findViewById(R.id.open_pdf);
    }

    private void openPDF() {
        openPdf.setOnClickListener(view -> {
            AccessDocument accessDocument = new AccessDocument(mReportId).invoke();
            Uri uri = accessDocument.getUri();
            String subject = accessDocument.getSubject();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
    }

    private void createPieChart() {

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        // Rotation graphic
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        // Design graphic
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        int mxNot = listNot.size();
        int mxYes = listYes.size();

        yValues.add(new PieEntry(mxYes, "Conforme"));
        yValues.add(new PieEntry(mxNot, "Não Conforme"));

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(24f);
        dataSet.setColors(getResources().getColor(R.color.colorRadioYes), getResources().getColor(R.color.colorRadioNot));

        // Animation
        pieChart.animateY(800, Easing.EaseInCirc);
        PieData data = integerFormatter(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
    }

    private PieData integerFormatter(PieDataSet dataSet) {
        PieData data = new PieData((dataSet));
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        return data;
    }

    private void loadReportResume() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mReportId = bundle.getInt(ReportConstants.ConstantsBundle.REPORT_ID);

            repoEntity = this.mReportBusiness.load(mReportId);
            this.emailResume.setText(repoEntity.getEmail());
            this.dateResume.setText(getString(R.string.resume_date, repoEntity.getDate()));
            this.companyResume.setText(repoEntity.getCompany());

            setTitle("Empresa " + repoEntity.getCompany());

            this.populateRecyclerViewResume();
            this.countRadioSelected();

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateRecyclerViewResume() {
        try {
            JSONArray array = new JSONArray(repoEntity.getListJson());

            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);

                ReportResumeItems repoJson = new ReportResumeItems(jo.getString("title_list"), jo.getString("description_list"), jo.getString("radio_tx"));
                repoResumeList.add(repoJson);
            }
            RecyclerView.Adapter adapter = new ReportResumeAdapter(repoResumeList, this);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StringFormatMatches")
    private void countRadioSelected() {
        try {
            JSONArray arrayL = new JSONArray(repoEntity.getListJson());
            for (int i = 0; i < arrayL.length(); i++) {
                JSONObject obj = arrayL.getJSONObject(i);
                String selected = obj.getString("radio_tx");
                listSelected.add(selected);
            }

            for (int i = 0; i < listSelected.size(); i++) {
                if (listSelected.get(i).equals("Conforme")) {
                    listYes.add("Conforme");
                }
                if (listSelected.get(i).equals("Não Conforme")) {
                    listNot.add("Não Conforme");
                }
            }

            int maxList = arrayL.length();
            this.itemsResume.setText(getString(R.string.item_selected, maxList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReportBusiness != null){
            mReportBusiness.close();
        }
    }
}
