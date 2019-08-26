package com.rendersoncs.reportform.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.rendersoncs.reportform.util.MyDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class ReportResume extends AppCompatActivity {

    private ReportBusiness mReportBusiness;
    ReportItems repoEntity;
    TextView emailResume;
    TextView dateResume;
    TextView companyResume;
    TextView itemsResume;
    PieChart pieChart;
    String selected;

    private RecyclerView recyclerView;
    private List<ReportResumeItems> repoResumeList;

    private ArrayList<String> listSelected = new ArrayList<>();
    public ArrayList listYes = new ArrayList<>();
    public ArrayList listNot = new ArrayList<>();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        pieChart = findViewById(R.id.pieChart);

        repoResumeList = new ArrayList<>();

        companyResume = findViewById(R.id.company_resume);
        emailResume = findViewById(R.id.email_resume);
        dateResume = findViewById(R.id.date_resume);
        itemsResume = findViewById(R.id.items_resume);

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

        int mxNot = listNot.size();
        int mxYes = listYes.size();

        yValues.add(new PieEntry(mxYes, "Sim"));
        yValues.add(new PieEntry(mxNot, "Não"));

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
            int mReportId = bundle.getInt(ReportConstants.ConstantsBundle.REPORT_ID);

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

    private void countRadioSelected() {
        try {
            JSONArray arrayL = new JSONArray(repoEntity.getListJson());
            for (int i = 0; i < arrayL.length(); i++) {
                JSONObject obj = arrayL.getJSONObject(i);
                selected = obj.getString("radio_tx");
                listSelected.add(selected);
            }

            for (int i = 0; i < listSelected.size(); i++) {
                String item = listSelected.get(i);
                if (listSelected.get(i).equals("Sim")) {
                    listYes.add("Sim");
                }
                if (listSelected.get(i).equals("Não")) {
                    listNot.add("Não");
                }
            }

            maxList = arrayL.length();
            this.itemsResume.setText(maxList + " Items selecionados.");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
