package com.rendersoncs.reportform;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rendersoncs.reportform.adapter.ExpandableRecyclerAdapter;
import com.rendersoncs.reportform.adapter.ReportResumeAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.async.PDFAsyncTask;
import com.rendersoncs.reportform.async.DownloadJsonFireBaseAsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;

    private Parcelable savedRecyclerLayoutState;
    private static final String LIST_STATE_KEY = "recycler_layout";

    public ExpandableRecyclerAdapter.ViewHolder viewHolder;
    private ReportBusiness mReportBusiness;

    private ArrayList<Repo> repository = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private ExpandableRecyclerAdapter mAdapter;
    private TextView resultCompany;
    private TextView resultEmail;
    private TextView resultDate;

    View floatingActionButton;

    JSONArray jsArray = new JSONArray();

    private DatabaseReference databaseReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbarForm);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report);

        databaseReference = FirebaseDatabase.getInstance().getReference("Data").child("list");
        databaseReference.keepSynced(true);

        mAdapter = new ExpandableRecyclerAdapter(repository, ReportActivity.this);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);

        this.isConnected();

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

        floatingActionButton = findViewById(R.id.fab_new_item);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewItemListFireBase();
            }
        });
        //mAdapter.notifyDataSetChanged();
    }

    private void startNewItemListFireBase() {
        NewItemListFireBase newItemListFirebase = new NewItemListFireBase();
        newItemListFirebase.show(getSupportFragmentManager(), "new_item");
    }

    public void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            this.addItemsFromFireBase();

            // Download list FireBase
            DownloadJsonFireBaseAsyncTask async = new DownloadJsonFireBaseAsyncTask(ReportActivity.this);
            async.execute();
            Toast.makeText(this, "Lista onLine", Toast.LENGTH_SHORT).show();

        } else {
            //floatingActionButton.setVisibility(View.GONE);
            this.addItemsFromJsonList();
            //mAdapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, null, floatingActionButton));
            Toast.makeText(this, "Lista offLine", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemsFromJsonList() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        try {
            JSONObject js = new JSONObject(readJsonDataFromFile()).getJSONObject("list");
            Log.i("File", "jsTest" + js);

            Iterator<String> iterator = js.keys();
            while (iterator.hasNext()) {
                String dynamicKey = iterator.next();
                JSONObject jsKeys = js.getJSONObject(dynamicKey);
                Log.i("File", "jsKeys" + jsKeys);

                String itemTitle = jsKeys.getString("title");
                Log.i("File", "itemTitle " + itemTitle);

                String itemText = jsKeys.getString("text");
                Log.i("File", "itemTitle " + itemText);

                Repo repo = new Repo();
                repo.setTitle(itemTitle);
                repo.setText(itemText);
                repository.add(repo);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read Json and populate recyclerView
    private String readJsonDataFromFile() throws IOException {

        String subject = ReportConstants.ConstantsFireBase.JSON_FIRE;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "Report" + "/" + subject + ".json";
        Log.i("File", "path" + path);

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = new FileInputStream(path);
            Log.i("File", "inputStream" + inputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
                Log.i("File", "jsonDataString" + jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new String(builder);
    }

    // Sync RecyclerView with FireBase
    public void addItemsFromFireBase() {

        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                repository.add(dataSnapshot.getValue(Repo.class));
                String key = dataSnapshot.getKey();
                mKeys.add(key);

                int index = mKeys.indexOf(key);
                mAdapter.notifyItemChanged(index);
                Log.i("item", "onChildAdded : " + dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Repo repo = dataSnapshot.getValue(Repo.class);
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                repository.set(index, repo);
                mAdapter.notifyItemChanged(index);
                Log.i("item", "onChildChanged : " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                repository.remove(index);
                mAdapter.notifyItemRemoved(index);
                Log.i("item", "onChildRemoved : " + dataSnapshot.getValue());
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
                                public void onClick(DialogInterface dialog, int which) {
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

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.i("Instante state", "onSaveInstanceState");
        //state.putParcelableArrayList(LIST_STATE, repository);
        state.putParcelable(LIST_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        //mListState = layoutManager.onSaveInstanceState();
        //state.putParcelable(LIST_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        //state.putSerializable(LIST_STATE_KEY, mAdapter.getClass());
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Log.i("Instante state", "onRestoreInstanceState");
        //repository = state.getParcelableArrayList(LIST_STATE);
        savedRecyclerLayoutState = state.getParcelable(LIST_STATE_KEY);
        /*if (state != null){
            mListState = state.getParcelable(LIST_STATE_KEY);
        }*/
    }

    @Override
    public void onBackPressed() {
        this.alertDialogClose();
    }
}

