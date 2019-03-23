package com.rendersoncs.reportform;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rendersoncs.reportform.adapter.ExpandableRecyclerAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.itens.Repo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;

    public ExpandableRecyclerAdapter.ViewHolder viewHolder;
    private ReportBusiness mReportBusiness;

    private List<Repo> data = new ArrayList<>();
    public List<Repo> list = new ArrayList<>();
    private ExpandableRecyclerAdapter mAdapter;
    private TextView resultCompany;
    private TextView resultEmail;
    private TextView resultDate;

    JsonArray jsArray = new JsonArray();

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form);
        ButterKnife.bind(this);

        mAdapter = new ExpandableRecyclerAdapter(data);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        toolbar = findViewById(R.id.toolbarForm);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report);

        // Recebendo texto digitado na DialogFragment
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String company = bundle.getString("company");
        resultCompany = findViewById(R.id.result_company);
        resultCompany.setText(company);

        String email = bundle.getString("email");
        resultEmail = findViewById(R.id.result_email);
        resultEmail.setText(email);

        String date = bundle.getString("date");
        resultDate = findViewById(R.id.result_date);
        resultDate.setText(date);

        this.mReportBusiness = new ReportBusiness(this);

        preparedList();

        this.setListener();

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivityExp.this, "Teste Recycler", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    private void setListener() {
        //this.viewHolder.
    }

    private void preparedList() {
        Repo repo = new Repo("INSTALAÇÕES FÍSICAS");
        data.add(repo);

        repo = new Repo(getString(R.string.title_1), getString(R.string.subTitle_1));
        data.add(repo);

        repo = new Repo(getString(R.string.title_2), getString(R.string.subTitle_2));
        data.add(repo);

        repo = new Repo(getString(R.string.title_3), getString(R.string.subTitle_3));
        data.add(repo);

        repo = new Repo(getString(R.string.title_4), getString(R.string.subTitle_4));
        data.add(repo);

        repo = new Repo(getString(R.string.title_5), getString(R.string.subTitle_5));
        data.add(repo);

        repo = new Repo(getString(R.string.title_7), getString(R.string.subTitle_7));
        data.add(repo);

        repo = new Repo(getString(R.string.title_8), getString(R.string.subTitle_8));
        data.add(repo);

        repo = new Repo(getString(R.string.title_9), getString(R.string.subTitle_9));
        data.add(repo);

        repo = new Repo(getString(R.string.title_10), getString(R.string.subTitle_10));
        data.add(repo);

        repo = new Repo(getString(R.string.title_11), getString(R.string.subTitle_11));
        data.add(repo);

        repo = new Repo(getString(R.string.title_12), getString(R.string.subTitle_12));
        data.add(repo);

        repo = new Repo(getString(R.string.title_13), getString(R.string.subTitle_13));
        data.add(repo);

        repo = new Repo(getString(R.string.title_14), getString(R.string.subTitle_14));
        data.add(repo);

        repo = new Repo(getString(R.string.title_15), getString(R.string.subTitle_15));
        data.add(repo);

        repo = new Repo(getString(R.string.title_16), getString(R.string.subTitle_16));
        data.add(repo);

        repo = new Repo(getString(R.string.title_17), getString(R.string.subTitle_17));
        data.add(repo);

        repo = new Repo(getString(R.string.title_18), getString(R.string.subTitle_18));
        data.add(repo);

        repo = new Repo(getString(R.string.title_19), getString(R.string.subTitle_19));
        data.add(repo);

        repo = new Repo(getString(R.string.title_20), getString(R.string.subTitle_20));
        data.add(repo);

        repo = new Repo(getString(R.string.title_21), getString(R.string.subTitle_21));
        data.add(repo);

        repo = new Repo(getString(R.string.title_22), getString(R.string.subTitle_22));
        data.add(repo);

        repo = new Repo(getString(R.string.title_23), getString(R.string.subTitle_23));
        data.add(repo);

        repo = new Repo(getString(R.string.title_24), getString(R.string.subTitle_24));
        data.add(repo);

        repo = new Repo(getString(R.string.title_25), getString(R.string.subTitle_25));
        data.add(repo);

        repo = new Repo(getString(R.string.title_26), getString(R.string.subTitle_26));
        data.add(repo);

        repo = new Repo(getString(R.string.title_27), getString(R.string.subTitle_27));
        data.add(repo);

        repo = new Repo(getString(R.string.title_28), getString(R.string.subTitle_28));
        data.add(repo);

        repo = new Repo(getString(R.string.title_29), getString(R.string.subTitle_29));
        data.add(repo);

        repo = new Repo("ÁREA DO SALÃO / CONSUMAÇÃO  / BAR ");
        data.add(repo);

        repo = new Repo(getString(R.string.title_29), getString(R.string.subTitle_29));
        data.add(repo);

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expandable, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (mAdapter.listIDRadio.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.txt_empty_report)
                            .setMessage(R.string.txt_choice_item)
                            .setPositiveButton(R.string.txt_back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else {
                    this.handleSave();
                }
                break;

            case R.id.close:
                this.alertDialogClose();
                break;

            case R.id.clear:
                if (mAdapter.listIDRadio.isEmpty()) {
                    Toast.makeText(this, "Lista vazia!", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Limpar lista?")
                            .setMessage("Deseja realmente limpar a seção? Esse processo vai apagar todo seu progresso!")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clearRadioButton();
                                }
                            })
                            .setNegativeButton("Não", null)
                            .show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Save Company, Email, Data, List
    private void handleSave() {
        final Repo repo = new Repo();
        repo.setCompany(resultCompany.getText().toString());
        repo.setEmail(resultEmail.getText().toString());
        repo.setDate(resultDate.getText().toString());

        // Convert ArrayList in Json Object
        for (int i = 0; (i < mAdapter.listTxtRadio.size()) && (i < mAdapter.listText.size()) && (i < mAdapter.listIDRadio.size()) && (i < mAdapter.listId.size()); i++) {
            JsonObject jsObject = new JsonObject();
            try {
                jsObject.addProperty("TITULO", mAdapter.listText.get(i));
                jsObject.addProperty("RADIO_TX", mAdapter.listTxtRadio.get(i));
                jsObject.addProperty("RADIO_ID", mAdapter.listIDRadio.get(i));
                jsObject.addProperty("ID_LIST", mAdapter.listId.get(i));
                Log.i("log", "Item: " + jsObject + " jsObject");

            } catch (Exception e) {
                e.printStackTrace();
            }
            jsArray.add(jsObject);
        }
        repo.setListJson(jsArray.toString());
        Log.i("log", "Item: " + jsArray + " jsArray");
        // Finish JsonObject

        //Save
        if (this.mReportBusiness.insert(repo)) {
            Toast.makeText(this, R.string.txt_report_save, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.txt_error_save, Toast.LENGTH_SHORT).show();
        }
        //Close Activity
        finish();
    }

    // Clear all every Lists and reload Adapter
    private void clearRadioButton() {
        mAdapter.listText.clear();
        mAdapter.listId.clear();
        mAdapter.listIDRadio.clear();
        mAdapter.listTxtRadio.clear();

        if (mAdapter.radioButton.isChecked()) {
            mAdapter.radioButton.clearFocus();
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.expandState.clear();

        Toast.makeText(this, "Lista vazia!", Toast.LENGTH_SHORT).show();
    }

    // Alert dialog close
    private void alertDialogClose() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_leave_the_report)
                .setMessage(R.string.txt_leave_the_report)
                .setPositiveButton(R.string.txt_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Toast.makeText(ReportActivity.this, "Relatório fechado!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.txt_cancel, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        this.alertDialogClose();
    }
}

