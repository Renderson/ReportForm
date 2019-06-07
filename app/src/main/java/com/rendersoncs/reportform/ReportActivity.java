package com.rendersoncs.reportform;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rendersoncs.reportform.adapter.ExpandableRecyclerAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.async.PDFAsyncTask;
import com.rendersoncs.reportform.async.DownloadJsonFireBaseAsyncTask;


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

    private List<Repo> repository = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private ExpandableRecyclerAdapter mAdapter;
    private TextView resultCompany;
    private TextView resultEmail;
    private TextView resultDate;

    JSONArray jsArray = new JSONArray();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    View floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form);
        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Data");

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //preparedListFire();

        mAdapter = new ExpandableRecyclerAdapter(repository);
        recyclerView.setAdapter(mAdapter);

        Toolbar toolbar = findViewById(R.id.toolbarForm);
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

        floatingActionButton = findViewById(R.id.floatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewItemListFireBase();
            }
        });
        isConnected();

        //preparedList();
    }

    private void startNewItemListFireBase(){
        NewItemListFireBase newItemListFirebase = new NewItemListFireBase();
        newItemListFirebase.show(getSupportFragmentManager(), "dialog_list");
    }

    public void isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            //floatingActionButton.setVisibility(View.VISIBLE);
            preparedListFire();
            DownloadJsonFireBaseAsyncTask async = new DownloadJsonFireBaseAsyncTask(ReportActivity.this);
            async.execute();
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        }else{
            preparedList();
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void preparedList() {
        Repo repo = new Repo("INSTALAÇÕES FÍSICAS");
        repository.add(repo);

        repo = new Repo(getString(R.string.title_1), getString(R.string.subTitle_1));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_2), getString(R.string.subTitle_2));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_3), getString(R.string.subTitle_3));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_4), getString(R.string.subTitle_4));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_5), getString(R.string.subTitle_5));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_7), getString(R.string.subTitle_7));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_8), getString(R.string.subTitle_8));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_9), getString(R.string.subTitle_9));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_10), getString(R.string.subTitle_10));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_11), getString(R.string.subTitle_11));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_12), getString(R.string.subTitle_12));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_13), getString(R.string.subTitle_13));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_14), getString(R.string.subTitle_14));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_15), getString(R.string.subTitle_15));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_16), getString(R.string.subTitle_16));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_17), getString(R.string.subTitle_17));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_18), getString(R.string.subTitle_18));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_19), getString(R.string.subTitle_19));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_20), getString(R.string.subTitle_20));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_21), getString(R.string.subTitle_21));
        repository.add(repo);

        repo = new Repo("ÁREA DO SALÃO / CONSUMAÇÃO  / BAR ");
        repository.add(repo);

        repo = new Repo(getString(R.string.title_22), getString(R.string.subTitle_22));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_23), getString(R.string.subTitle_23));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_24), getString(R.string.subTitle_24));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_25), getString(R.string.subTitle_25));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_26), getString(R.string.subTitle_26));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_27), getString(R.string.subTitle_27));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_28), getString(R.string.subTitle_28));
        repository.add(repo);

        repo = new Repo(getString(R.string.title_29), getString(R.string.subTitle_29));
        repository.add(repo);

        repo = new Repo("ÁREA DO SALÃO / CONSUMAÇÃO  / BAR ");
        repository.add(repo);

        repo = new Repo(getString(R.string.title_29), getString(R.string.subTitle_29));
        repository.add(repo);

        mAdapter.notifyDataSetChanged();

    }

    public void preparedListFire(){

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Repo repo = dataSnapshot.getValue(Repo.class);
                //repository.add(repo);
                repository.add(dataSnapshot.getValue(Repo.class));
                //recyclerView.setAdapter(mAdapter);
                String key = dataSnapshot.getKey();
                mKeys.add(key);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Repo repo = dataSnapshot.getValue(Repo.class);
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                repository.set(index, repo);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Repo repo = dataSnapshot.getValue(Repo.class);
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                repository.remove(index);
                mAdapter.notifyItemRemoved(index);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                                public void onClick(DialogInterface dialog, int which) {}
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
        JSONObject job = new JSONObject();
        for (int i = 0; (i < mAdapter.listTxtRadio.size()) && (i < mAdapter.listText.size()) && (i < mAdapter.listIDRadio.size()) && (i < mAdapter.listId.size()); i++) {
            JSONObject jsObject = new JSONObject();
            try {
                //jsObject.put("List", job);
                jsObject.put("title_list", mAdapter.listText.get(i));
                jsObject.put("radio_tx", mAdapter.listTxtRadio.get(i));
                jsObject.put("radio_id", mAdapter.listIDRadio.get(i));
                jsObject.put("id_list", mAdapter.listId.get(i));
                Log.i("log", "Item: " + jsObject + " jsObject");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsArray.put(jsObject);
        }

        repo.setListJson(jsArray.toString());
        Log.i("log", "Item: " + jsArray + " jsArray");
        // Finish JsonObject

        //Save
        if (this.mReportBusiness.insert(repo)) {
            // Execute Async create PDF
            PDFAsyncTask asy = new PDFAsyncTask(this);
            asy.execute(repo);
            Toast.makeText(this, R.string.txt_report_save, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.txt_error_save, Toast.LENGTH_SHORT).show();
            finish();
        }
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

