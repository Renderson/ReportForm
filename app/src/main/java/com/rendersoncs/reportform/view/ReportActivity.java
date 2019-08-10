package com.rendersoncs.reportform.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ExpandableRecyclerAdapter;
import com.rendersoncs.reportform.animated.AnimatedFloatingButton;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.async.PDFAsyncTask;
import com.rendersoncs.reportform.listener.OnRadioItemClicked;
import com.rendersoncs.reportform.login.LoginActivity;
import com.rendersoncs.reportform.login.SignUpActivity;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;
import com.rendersoncs.reportform.util.InjectJsonListModeOff;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity implements OnRadioItemClicked {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;
    private User user;

    private ReportBusiness mReportBusiness;
    private InjectJsonListModeOff jsonListModeOff = new InjectJsonListModeOff();

    private ArrayList<ReportItems> reportItems = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private JSONObject test = new JSONObject();
    private ExpandableRecyclerAdapter mAdapter;
    public ExpandableRecyclerAdapter.ItemVh viewHolder;
    private TextView resultCompany, resultEmail, resultDate;
    FloatingActionButton fab;
    AnimatedFloatingButton animated = new AnimatedFloatingButton();

    JSONArray jsArray = new JSONArray();
    ArrayList<String> listTitle = new ArrayList<>();
    ArrayList<String> listRadio = new ArrayList<>();

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report);

        mAuth = FirebaseAuth.getInstance();
        user = new User();
        user.setId( mAuth.getCurrentUser().getUid() );
        Log.i("LOG", "mAuth: " + user.getId());

        //databaseReference = FirebaseDatabase.getInstance().getReference("Data").child("list");
        //databaseReference = FirebaseDatabase.getInstance().getReference("users").child(fire).child("list");
        databaseReference = LibraryClass.getFirebase().child("users").child(user.getId()).child("list");
        databaseReference.keepSynced(true);

        mAdapter = new ExpandableRecyclerAdapter(reportItems, this);
        mAdapter.setOnRadioItemClicked(this);
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

        // Animated FloatingBottom
        //animated.animatedFab(recyclerView, fab);
        animatedFloatingButton();

        fab = findViewById(R.id.fab_new_item);
        fab.setOnClickListener(v -> startNewItemListFireBase());
        //mAdapter.notifyDataSetChanged();

    }

    private void animatedFloatingButton() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
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
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            jsonListModeOff.addItemsFromJsonList(reportItems);
            Toast.makeText(this, "Lista offLine", Toast.LENGTH_SHORT).show();
        }
    }

    // Sync RecyclerView with FireBase
    public void addItemsFromFireBase() {

        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                reportItems.add(dataSnapshot.getValue(ReportItems.class));
                String key = dataSnapshot.getKey();
                mKeys.add(key);

                int index = mKeys.indexOf(key);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ReportItems reportItems = dataSnapshot.getValue(ReportItems.class);
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                ReportActivity.this.reportItems.set(index, reportItems);
                mAdapter.notifyItemChanged(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                reportItems.remove(index);
                mAdapter.notifyItemRemoved(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        mAdapter.notifyDataSetChanged();
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

        this.checkAnswers();

        final ReportItems reportItems = new ReportItems();
        reportItems.setCompany(resultCompany.getText().toString());
        reportItems.setEmail(resultEmail.getText().toString());
        reportItems.setDate(resultDate.getText().toString());

        // Convert ArrayList in Json Object
        for (int i = 0; (i < listRadio.size()) && (i < listTitle.size()); i++) {
            JSONObject jsObject = new JSONObject();
            try {
                jsObject.put("title_list", listTitle.get(i));
                jsObject.put("radio_tx", listRadio.get(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsArray.put(jsObject);
        }

        reportItems.setListJson(jsArray.toString());
        Log.i("log", "Item: " + jsArray + " jsArray");
        // Finish JsonObject

        //Save
        if (this.mReportBusiness.insert(reportItems)) {
            // Execute Async create PDF
            PDFAsyncTask asy = new PDFAsyncTask(this);
            asy.execute(reportItems);
            Toast.makeText(this, R.string.txt_report_save, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.txt_error_save, Toast.LENGTH_SHORT).show();
            finish();
        }
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

    // Clear all every Lists and reload Adapter
    private void clearRadioButton() {
        for (int i = 0; i < reportItems.size(); i++){
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2()){
                reportItems.get(i).setOpt1(false);
                reportItems.get(i).setOpt2(false);
            }
        }
        listTitle.clear();
        listRadio.clear();
        mAdapter.listIDRadio.clear();

        if (mAdapter.radioButton.isChecked()) {
            mAdapter.radioButton.clearFocus();
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.expandState.clear();

        Toast.makeText(this, "Lista vazia!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void radioItemChecked(int itemPosition, int optNum) {
        reportItems.get(itemPosition).setSelectedAnswerPosition(optNum);
        reportItems.get(itemPosition).setShine(false);
        switch (optNum) {
            case 1:
                reportItems.get(itemPosition).setOpt1(true);
                break;

            case 2:
                reportItems.get(itemPosition).setOpt2(true);
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void checkAnswers(){

        for (int i = 0; i < reportItems.size(); i++){
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2()){
                if (reportItems.get(i).getSelectedAnswerPosition() == 1){
                    String yes = "Sim";
                    listRadio.add(yes);
                } else {
                    String not = "Não";
                    listRadio.add(not);
                }

                String title = reportItems.get(i).getTitle();
                listTitle.add(title);

            }
        }
    }

    @Override
    public void onBackPressed() {
        this.alertDialogClose();
    }
}

