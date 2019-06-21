package com.rendersoncs.reportform.view;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportResumeAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.itens.ReportResumeItems;
import com.rendersoncs.reportform.observer.IntegerFormatter;
import com.rendersoncs.reportform.observer.MyDividerItemDecoration;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportResume extends AppCompatActivity {

    private ReportBusiness mReportBusiness;
    Repo repoEntity;
    //TextView companyResume;
    TextView emailResume;
    TextView dateResume;
    //TextView listReportResume;
    TextView totalList;
    PieChart pieChart;
    String selected;

    private RecyclerView recyclerView;
    private List<ReportResumeItems> repoResumeList;

    private ArrayList<String> listSelected = new ArrayList<>();
    private HashMap<String, Integer> contListSelected = new HashMap<>();

    int maxList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resume);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.resume_list);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        pieChart = findViewById(R.id.pieChart);

        repoResumeList = new ArrayList<>();

        //companyResume = findViewById(R.id.company_resume);
        emailResume = findViewById(R.id.email_resume);
        dateResume = findViewById(R.id.date_resume);
        //listReportResume = findViewById(R.id.list_report_resume);
        totalList = findViewById(R.id.all_list);

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        this.loadReportResume();
        this.createPieChart();
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

        int mxConformed = listSelected.size();
        Log.i("log", "Item: " + mxConformed + " mxConformed ");

        yValues.add(new PieEntry(maxList, "Conforme"));
        yValues.add(new PieEntry(mxConformed, "Não Aplicavel"));
        yValues.add(new PieEntry(15, "Não Conforme"));

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setValueFormatter(new IntegerFormatter());
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Animation
        pieChart.animateY(800, Easing.EaseInCirc);

        PieData data = new PieData((dataSet));
        data.setValueFormatter(new IntegerFormatter());
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
    }

    private void loadReportResume() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mReportId = bundle.getInt(ReportConstants.BundleConstants.REPORT_ID);

            repoEntity = this.mReportBusiness.load(mReportId);
            //this.companyResume.setText(repoEntity.getCompany());
            this.emailResume.setText(repoEntity.getEmail());
            this.dateResume.setText(getString(R.string.resume_date) + repoEntity.getDate());
            //this.listReportResume.setText(repoEntity.getListJson());

            // popular RecyclerView
            try {
                JSONArray array = new JSONArray(repoEntity.getListJson());
                Log.i("log", "Item: " + array + " array ");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    Log.i("log", "Item: " + jo + " stringResult ");

                    ReportResumeItems repoJson = new ReportResumeItems(jo.getString("title_list"));
                    repoResumeList.add(repoJson);

                    //titulo = (String) jsonObject.getString("title_list");
                    //Log.i("log", "Item: " + titulo + " title_list ");
                }
                RecyclerView.Adapter adapter = new ReportResumeAdapter(repoResumeList, this);
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // finish recyclerView

            try {
                JSONArray arrayL = new JSONArray(repoEntity.getListJson());
                for (int i = 0; i < arrayL.length(); i++) {
                    JSONObject obj = arrayL.getJSONObject(i);
                    selected = obj.getString("radio_tx");
                    listSelected.add(selected);
                    Log.i("log", "Item: " + listSelected + " selected ");
                }
                for (int i = 0; i < listSelected.size(); i++) {
                    String item = listSelected.get(i);
                    if (contListSelected.containsKey(item))
                        contListSelected.put(item, contListSelected.get(item) + 1);
                    else
                        contListSelected.put(item, 1);
                    Log.i("log", "Item: " + contListSelected + " contListSelected ");
                }
                StringBuilder sb = new StringBuilder();

                for (Map.Entry<String, Integer> e : contListSelected.entrySet()) {
                    sb.append("\n").append(e.getKey()).append(" : ").append(e.getValue());
                    Log.i("log", "Item: " + sb + " sb ");
                }

                //this.listReportResume.setText(sb.toString());

                maxList = arrayL.length();
                this.totalList.setText(String.valueOf(maxList) + " Items selecionados.");
                Log.i("log", "Item: " + maxList + " valor ");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            setTitle("Empresa " + repoEntity.getCompany());

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
