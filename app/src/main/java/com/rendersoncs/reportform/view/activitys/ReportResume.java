package com.rendersoncs.reportform.view.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.itens.ReportResumeItems;
import com.rendersoncs.reportform.repository.dao.business.ReportBusiness;
import com.rendersoncs.reportform.view.adapter.ReportResumeAdapter;
import com.rendersoncs.reportform.view.adapter.listener.OnItemClickResume;
import com.rendersoncs.reportform.view.fragment.FullPhotoFragment;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.view.services.extension.StringExtension;
import com.rendersoncs.reportform.view.services.service.AccessDocument;
import com.rendersoncs.reportform.view.services.util.MyDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportResume extends AppCompatActivity implements OnItemClickResume {

    private ReportBusiness mReportBusiness;
    private ReportItems repoEntity;
    private TextView emailResume, dateResume, companyResume, itemsResume, openPdf;
    private PieChart pieChart;
    private int mReportId;

    private RecyclerView recyclerView;
    private List<ReportResumeItems> repoResumeList;

    private ArrayList<String> listSelected = new ArrayList<>();
    private ArrayList<String> listRadioC = new ArrayList<>();
    private ArrayList<String> listRadioNA = new ArrayList<>();
    private ArrayList<String> listRadioNC = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.init();

        // Business Layer
        this.mReportBusiness = new ReportBusiness(ReportResume.this);

        this.loadReportResume();
        this.createPieChart();
        this.openPDF();
    }

    private void init() {
        recyclerView = findViewById(R.id.resume_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReportResume.this));
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

        int mxC = listRadioC.size();
        int mxNA = listRadioNA.size();
        int mxNC = listRadioNC.size();

        yValues.add(new PieEntry(mxC, getResources().getString(R.string.according)));
        yValues.add(new PieEntry(mxNA, getResources().getString(R.string.not_applicable)));
        yValues.add(new PieEntry(mxNC, getResources().getString(R.string.not_according)));

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(24f);
        dataSet.setColors(getResources().getColor(R.color.colorRadioC), getResources().getColor(R.color.colorRadioNA), getResources().getColor(R.color.colorRadioNC));

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
            mReportId = bundle.getInt(ReportConstants.REPORT.REPORT_ID);

            repoEntity = this.mReportBusiness.load(mReportId);
            this.emailResume.setText(repoEntity.getEmail());
            this.dateResume.setText(repoEntity.getDate());
            this.companyResume.setText(StringExtension.limitsText(repoEntity.getCompany(), ReportConstants.CHARACTERS.LIMITS_TEXT));

            setTitle(getResources().getString(R.string.company) + " " + StringExtension.limitsText(repoEntity.getCompany(), ReportConstants.CHARACTERS.LIMITS_TEXT));

            this.populateRecyclerViewResume();
            this.countRadioSelected();

        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    private void populateRecyclerViewResume() {
        try {
            JSONArray array = new JSONArray(repoEntity.getListJson());

            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);

                ReportResumeItems repoJson = new ReportResumeItems(jo.getString(ReportConstants.ITEM.TITLE),
                        jo.getString(ReportConstants.ITEM.DESCRIPTION),
                        jo.getString(ReportConstants.ITEM.CONFORMITY),
                        jo.getString(ReportConstants.ITEM.NOTE),
                        jo.getString(ReportConstants.ITEM.PHOTO));
                repoResumeList.add(repoJson);
            }
            RecyclerView.Adapter adapter = new ReportResumeAdapter(repoResumeList, ReportResume.this);
            ((ReportResumeAdapter) adapter).setOnItemListenerClicked(this);
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
                String selected = obj.getString(ReportConstants.ITEM.CONFORMITY);
                listSelected.add(selected);
            }

            for (int i = 0; i < listSelected.size(); i++) {
                if (listSelected.get(i).equals(getResources().getString(R.string.according))) {
                    listRadioC.add(getResources().getString(R.string.according));
                }
                if (listSelected.get(i).equals(getResources().getString(R.string.not_applicable))) {
                    listRadioNA.add(getResources().getString(R.string.not_applicable));
                }
                if (listSelected.get(i).equals(getResources().getString(R.string.not_according))) {
                    listRadioNC.add(getResources().getString(R.string.not_according));
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

        if (mReportBusiness != null) {
            mReportBusiness.close();
        }
    }

    @Override
    public void fullPhoto(ReportResumeItems reportItems) {
        FullPhotoFragment fullFragment = new FullPhotoFragment();
        Bundle bundle = new Bundle();

        String image = reportItems.getPhoto();

        byte[] bytes = Base64.decode(image, Base64.DEFAULT);

        bundle.putByteArray(ReportConstants.ITEM.PHOTO, bytes);

        fullFragment.setArguments(bundle);
        fullFragment.show(getSupportFragmentManager(), "fullPhoto");
    }
}
