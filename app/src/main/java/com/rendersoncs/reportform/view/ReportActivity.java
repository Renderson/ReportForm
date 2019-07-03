package com.rendersoncs.reportform.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ExpandableRecyclerAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.async.PDFAsyncTask;
import com.rendersoncs.reportform.listener.OnInteractionCameraListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;

    private ReportBusiness mReportBusiness;

    private ArrayList<Repo> repository = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private JSONObject test = new JSONObject();
    private ExpandableRecyclerAdapter mAdapter;
    public ExpandableRecyclerAdapter.ViewHolder viewHolder;
    private TextView resultCompany, resultEmail, resultDate;

    FloatingActionButton fab;

    JSONArray jsArray = new JSONArray();

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report);

        databaseReference = FirebaseDatabase.getInstance().getReference("Data").child("list");
        databaseReference.keepSynced(true);

        mAdapter = new ExpandableRecyclerAdapter(repository, this);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);

        this.isConnected();

        // get text from DialogFragment
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
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

        fab = findViewById(R.id.fab_new_item);
        fab.setOnClickListener(v -> startNewItemListFireBase());
        //mAdapter.notifyDataSetChanged();

        // Animated FloatingBottom
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy < 0 && !fab.isShown())
                    fab.show();
                else if (dy > 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void startNewItemListFireBase() {
        NewItemListFireBase newItemListFirebase = new NewItemListFireBase();
        newItemListFirebase.show(getSupportFragmentManager(), "new_item");
    }

    private void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            this.addItemsFromFireBase();
            Toast.makeText(this, "Lista onLine", Toast.LENGTH_SHORT).show();

        } else {
            //fab.setVisibility(View.GONE);
            this.addItemsFromJsonList();
            //mAdapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, null, fab));
            Toast.makeText(this, "Lista offLine", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemsFromJsonList() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        try {
            JSONObject js = new JSONObject(readJsonDataFromFile()).getJSONObject("list");

            Iterator<String> iterator = js.keys();
            while (iterator.hasNext()) {
                String dynamicKey = iterator.next();
                JSONObject jsKeys = js.getJSONObject(dynamicKey);

                String itemTitle = jsKeys.getString("title");
                String itemText = jsKeys.getString("text");

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

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString;
            inputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
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
                            .setPositiveButton(R.string.txt_back, (dialog, which) -> {
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
                            .setPositiveButton("Sim", (dialog, which) -> clearRadioButton())
                            .setNegativeButton("Não", null)
                            .show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Save Company, Email, Data, List
    private void handleSave() {
        //testRadio();

        final Repo repo = new Repo();
        repo.setCompany(resultCompany.getText().toString());
        repo.setEmail(resultEmail.getText().toString());
        repo.setDate(resultDate.getText().toString());

        // Convert ArrayList in Json Object
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

    private void testRadio(){
        /*String test1 = mAdapter.radioButton.getText().toString();
        Log.i("log", "Item: " + test1 + " listConformed1 ");
        for (int i = 0; i < test1.length(); i++) {
            ArrayList<String> test2 = new ArrayList<>();
            test2.add(test1);
            Log.i("log", "Item: " + test2 + " listConformed2 ");


            for (int y = 0; y < test2.size(); y++) {
                JSONObject test3 = new JSONObject();
                try {
                    test3.put("Radio", test2.get(y));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("log", "Item: " + test3 + " listConformed ");
            }
        }*/
        viewHolder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectRadioButtonId = viewHolder.mRadioGroup.getCheckedRadioButtonId();

                mAdapter.radioButton = group.findViewById(selectRadioButtonId);
                Log.i("log", "Item: " + selectRadioButtonId + " mAdapter.radioButton ");
            }
        });

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
                .setPositiveButton(R.string.txt_continue, (dialog, which) -> {
                    finish();
                    Toast.makeText(this, "Relatório fechado!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.txt_cancel, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        this.alertDialogClose();
    }
}

