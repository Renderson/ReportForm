package com.rendersoncs.reportform.view.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.repository.dao.ReportDataBaseAsyncTask;
import com.rendersoncs.reportform.repository.dao.business.ReportBusiness;
import com.rendersoncs.reportform.view.activitys.login.util.LibraryClass;
import com.rendersoncs.reportform.view.activitys.login.util.User;
import com.rendersoncs.reportform.view.adapter.checkListAdapter.CheckListReportAdapter;
import com.rendersoncs.reportform.view.adapter.checkListAdapter.EditCheckListReportAdapter;
import com.rendersoncs.reportform.view.adapter.checkListAdapter.ReportRecyclerView;
import com.rendersoncs.reportform.view.adapter.listener.OnItemListenerClicked;
import com.rendersoncs.reportform.view.fragment.FullPhotoFragment;
import com.rendersoncs.reportform.view.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.view.fragment.ReportNoteFragment;
import com.rendersoncs.reportform.view.services.async.PDFCreateAsync;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.view.services.photo.CameraUtil;
import com.rendersoncs.reportform.view.services.photo.ResizeImage;
import com.rendersoncs.reportform.view.services.photo.TakePicture;
import com.rendersoncs.reportform.view.services.util.AlertDialogUtil;
import com.rendersoncs.reportform.view.services.util.ListJsonOff;
import com.rendersoncs.reportform.view.services.util.RVEmptyObserver;
import com.rendersoncs.reportform.view.services.util.SnackBarHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity implements OnItemListenerClicked, SearchView.OnQueryTextListener {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;

    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_PERMISSIONS_GRANTED = 1;
    private static final int REQUEST_PERMISSIONS_READ_WHITE = 128;

    // SavedInstanceState
    /*private static final String LIST_STATE = "list_state";
    private static final String BUNDLE_RECYCLER_LAYOUT= "recycler_layout";*/

    private ReportBusiness mReportBusiness;
    private ListJsonOff jsonListModeOff = new ListJsonOff();

    private ArrayList<ReportItems> reportItems = new ArrayList<>();
    private final ArrayList<String> mKeys = new ArrayList<>();
    private ReportRecyclerView mAdapter;
    private FloatingActionButton fab;
    private View emptyLayout;

    private TextView resultCompany, resultEmail, resultDate, scores;
    private String resultController = "";
    private String resultScore = "";
    private int listRadioMax = 0;
    private PDFCreateAsync pdfCreateAsync = new PDFCreateAsync(ReportActivity.this);
    //private AnimatedFloatingButton animated = new AnimatedFloatingButton();

    private ArrayList<String> listTitle = new ArrayList<>();
    private ArrayList<String> listDescription = new ArrayList<>();
    private ArrayList<String> listRadio = new ArrayList<>();
    private ArrayList<String> listNotes = new ArrayList<>();
    private ArrayList<String> listPhoto = new ArrayList<>();
    private JSONArray jsArray = new JSONArray();

    // Val for Edit Report
    private int mReportId = 0;
    private ArrayList<String> editConformity = new ArrayList<>();
    private ArrayList<String> editNotes = new ArrayList<>();
    private ArrayList<String> editPhoto = new ArrayList<>();

    //public Parcelable savedRecyclerLayoutState;

    private DatabaseReference databaseReference;
    private FirebaseAnalytics mFireBaseAnalytics;
    private User user = new User();

    private SnackBarHelper snackBarHelper = new SnackBarHelper();
    private CheckAnswerList checkAnswerList = new CheckAnswerList();
    private TakePicture takePicture = new TakePicture();
    private AlertDialog dialog;
    private int position;

    private CameraUtil path = new CameraUtil();
    private AlertDialogUtil alertDialog = new AlertDialogUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        user.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        // SavedInstanceState
        /*if (savedInstanceState != null) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            reportItems = savedInstanceState.getParcelableArrayList(LIST_STATE);
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        } else {
            this.initViews();
        }*/

        this.mReportBusiness = new ReportBusiness(this);
        this.initViews();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mReportId = bundle.getInt(ReportConstants.REPORT.REPORT_ID);
        }
        if (mReportId == 0) {
            this.loadListFire();
        } else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            this.loadEditReport();
        }
    }

    private void initViews() {
        resultCompany = findViewById(R.id.result_company);
        resultEmail = findViewById(R.id.result_email);
        resultDate = findViewById(R.id.result_date);
        scores = findViewById(R.id.score);
        emptyLayout = findViewById(R.id.layout_report_list_empty);
        Button emptyButton = findViewById(R.id.action_add_item);

        mAdapter = new CheckListReportAdapter(reportItems, this);
        mAdapter.setOnItemListenerClicked(this);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);

        // Implementation ItemTouchHelper
        /*ItemTouchHelper.Callback callback = new ItemMoveCallBack(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        mAdapter.notifyDataSetChanged();*/

        recyclerView.setAdapter(mAdapter);

        fab = findViewById(R.id.fab_new_item);
        fab.setOnClickListener(v -> startNewItemListFireBase());

        emptyButton.setOnClickListener(v -> startNewItemListFireBase());

        // Animated FloatingButton
        //animated.animatedFab(recyclerView, fab);
    }

    private void loadListFire() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        this.isConnected();
        this.getBundleReportFromDialog();

        //mAdapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, emptyLayout, fab));
    }

    private void loadEditReport() {
        findViewById(R.id.fab_new_item).setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mReportId = bundle.getInt(ReportConstants.REPORT.REPORT_ID);

            ReportItems repoEntity = this.mReportBusiness.load(this.mReportId);
            this.resultCompany.setText(repoEntity.getCompany());
            this.resultEmail.setText(repoEntity.getEmail());
            this.resultDate.setText(repoEntity.getDate());

            /*EditReportActivity edit = new EditReportActivity();
            edit.loadEditReportExt(this,
                    mReportId,
                    mAdapter,
                    mReportBusiness,
                    reportItems,
                    recyclerView);*/

            try {
                JSONArray array = new JSONArray(repoEntity.getListJson());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);

                    String conformity = object.getString(ReportConstants.ITEM.CONFORMITY);
                    editConformity.add(conformity);

                    String note = object.getString(ReportConstants.ITEM.NOTE);
                    editNotes.add(note);

                    String photo = object.getString(ReportConstants.ITEM.PHOTO);
                    editPhoto.add(photo);

                    ReportItems repoJson = new ReportItems(object.getString(ReportConstants.ITEM.TITLE),
                            object.getString(ReportConstants.ITEM.DESCRIPTION),
                            null, null);
                    reportItems.add(repoJson);
                }

                for (int i = 0; i < editConformity.size(); i++) {
                    if (editConformity.get(i).equals(getResources().getString(R.string.according))) {
                        this.radioItemChecked(i, ReportConstants.ITEM.OPT_NUM1);
                    }
                    if (editConformity.get(i).equals(getResources().getString(R.string.not_applicable))) {
                        this.radioItemChecked(i, ReportConstants.ITEM.OPT_NUM2);
                    }
                    if (editConformity.get(i).equals(getResources().getString(R.string.not_according))) {
                        this.radioItemChecked(i, ReportConstants.ITEM.OPT_NUM3);
                    }
                }

                for (int i = 0; i < editNotes.size(); i++) {
                    String notes = editNotes.get(i);
                    mAdapter.insertNote(i, notes);
                    Log.i("log", "NOTES: " + i + notes + " notes");
                }

                for (int i = 0; i < editPhoto.size(); i++) {
                    String photos = editPhoto.get(i);

                    byte[] decodedString = Base64.decode(photos, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    mAdapter.setImageInItem(i, bitmap);
                }

                mAdapter = new EditCheckListReportAdapter(reportItems, ReportActivity.this);
                mAdapter.setOnItemListenerClicked(ReportActivity.this);
                recyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void getBundleReportFromDialog() {
        // get text from DialogFragment
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String company = bundle.getString(ReportConstants.ITEM.COMPANY);
        resultCompany.setText(company);

        String email = bundle.getString(ReportConstants.ITEM.EMAIL);
        resultEmail.setText(email);

        resultController = bundle.getString(ReportConstants.ITEM.CONTROLLER);

        String date = bundle.getString(ReportConstants.ITEM.DATE);
        resultDate.setText(date);
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
            databaseReference = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS)
                    .child(user.getId()).child(ReportConstants.FIREBASE.FIRE_LIST);
            this.addItemsFromFireBase();

        } else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.action_add_item).setVisibility(View.GONE);
            fab.setEnabled(false);
            jsonListModeOff.addItemsFromJsonList(reportItems);
        }
    }

    // Sync RecyclerView with FireBase
    private void addItemsFromFireBase() {

        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                reportItems.add(dataSnapshot.getValue(ReportItems.class));
                String key = dataSnapshot.getKey();
                mKeys.add(key);

                //int index = mKeys.indexOf(key);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ReportItems reportItems = dataSnapshot.getValue(ReportItems.class);
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                ReportActivity.this.reportItems.set(index, reportItems);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                reportItems.remove(index);
                mKeys.remove(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getString(R.string.label_failed), Toast.LENGTH_SHORT).show();
            }
        });
        this.showLayoutEmpty();
    }

    private void showLayoutEmpty() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        mAdapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, emptyLayout, fab));
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search ");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.getFilter().filter(query);
        return false;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            // Save report
            case R.id.save:
                this.checkAnswers();
                if (this.listPhoto.isEmpty()) {
                    this.alertDialog.showDialog(ReportActivity.this,
                            getResources().getString(R.string.alert_empty_report),
                            getResources().getString(R.string.alert_empty_report_text),
                            getResources().getString(R.string.back),
                            (dialogInterface, i) -> {
                            },
                            null, null, false);
                    this.clearList();

                } else if (this.listRadio.size() > this.listPhoto.size()) {
                    this.alertDialog.showDialog(ReportActivity.this,
                            getResources().getString(R.string.alert_check_list),
                            getResources().getString(R.string.alert_check_list_text),
                            getResources().getString(R.string.back),
                            (dialogInterface, i) -> {
                            },
                            null, null, false);
                    this.clearList();

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(ReportActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ReportActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_READ_WHITE);
                        } else {
                            this.alertDialogScore();
                        }
                    } else {
                        this.alertDialogScore();
                    }
                }
                break;

            case R.id.close:
                this.alertDialogClose();
                break;

            case R.id.clear:
                this.checkAnswers();
                if (this.listRadio.isEmpty()) {
                    snackBarHelper.showSnackBar(ReportActivity.this, R.id.fab_new_item, R.string.label_empty_list);
                } else {
                    alertDialog.showDialog(ReportActivity.this,
                            getResources().getString(R.string.alert_clear_list),
                            getResources().getString(R.string.alert_clear_list_text),
                            getResources().getString(R.string.confirm),
                            (dialogInterface, i) -> this.clearCheckList(),
                            getResources().getString(R.string.cancel), null, false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDialogScore() {
        alertDialog.showDialogScore(ReportActivity.this,
                getResources().getString(R.string.alert_punctuation),
                getResources().getString(R.string.alert_punctuation_label1, resultScore) + " " +
                        listRadioMax + " " +
                        getResources().getString(R.string.alert_punctuation_label2, scores.getText().toString()),
                getResources().getString(R.string.confirm),
                (dialogInterface, i) -> {
                    this.handleSave();
                },
                getResources().getString(R.string.cancel), (dialogInterface, i) ->{
                    this.clearList();
                }, false);
    }

    private void alertDialogClose() {
        alertDialog.showDialog(ReportActivity.this,
                getResources().getString(R.string.alert_leave_the_report),
                getResources().getString(R.string.alert_leave_the_report_text),
                getResources().getString(R.string.confirm),
                (dialogInterface, i) -> {
                    /*Intent intent = new Intent(this, SignatureActivityMain.class);
                    startActivity(intent);*/
                    this.closeMethods();
                    finish();
                },
                getResources().getString(R.string.cancel), null, false);
    }

    // Save Company, Email, Data, List
    private void handleSave() {

        final ReportItems reportItems = new ReportItems();
        reportItems.setCompany(resultCompany.getText().toString());
        reportItems.setEmail(resultEmail.getText().toString());
        reportItems.setController(resultController);
        reportItems.setScore(scores.getText().toString());
        reportItems.setResult(resultScore);
        reportItems.setDate(resultDate.getText().toString());

        // Convert ArrayList in Json Object
        for (int i = 0; (i < listRadio.size())
                && (i < listTitle.size())
                && (i < listDescription.size())
                && (i < listNotes.size())
                && (i < listPhoto.size()); i++) {

            JSONObject jsObject = new JSONObject();
            try {
                jsObject.put(ReportConstants.ITEM.TITLE, listTitle.get(i));
                jsObject.put(ReportConstants.ITEM.DESCRIPTION, listDescription.get(i));
                jsObject.put(ReportConstants.ITEM.CONFORMITY, listRadio.get(i));
                jsObject.put(ReportConstants.ITEM.NOTE, listNotes.get(i));
                jsObject.put(ReportConstants.ITEM.PHOTO, listPhoto.get(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsArray.put(jsObject);
        }

        reportItems.setListJson(jsArray.toString());
        Log.i("log", "Item: " + jsArray + " jsArray");
        // Finish JsonObject

        // Save Report in SQLite
        new ReportDataBaseAsyncTask(ReportActivity.this,
                pdfCreateAsync,
                mReportId,
                mReportBusiness,
                reportItems,
                mFireBaseAnalytics,
                this::finish).execute();
    }

    // Clear list
    private void clearList() {
        listRadio.clear();
        listPhoto.clear();
        listTitle.clear();
        listDescription.clear();
        listNotes.clear();
    }

    // Clear every Lists and reload Adapter
    private void clearCheckList() {

        mAdapter.expandState.clear();
        reportItems.clear();
        this.clearList();
        this.isConnected();

        snackBarHelper.showSnackBar(ReportActivity.this, R.id.fab_new_item, R.string.label_empty_list);
    }

    private void getScore() {
        ArrayList<String> listMax = new ArrayList<>();
        listMax.clear();

        float NCT = 0.7f;
        float soma = 0f;
        float sizeList = 0f;

        for (int i = 0; i < reportItems.size(); i++) {
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2() || reportItems.get(i).isOpt3()) {
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM1) {
                    String C = getResources().getString(R.string.according);
                    listMax.add(C);
                }
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM2) {
                    String NA = getResources().getString(R.string.not_applicable);
                    listMax.add(NA);
                }
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM3) {
                    String NC = getResources().getString(R.string.not_according);
                    listMax.add(NC);
                    soma = NCT++;
                    //Log.i("LOG", "Total Item S " + soma + " " + NCT);
                }
            }
        }
        listRadioMax = listMax.size();
        float divList = listRadioMax / 2f;
        float scoreList = listRadioMax - soma;

        if (scoreList >= divList) {
            resultScore = "CONFORME";
            Log.i("LOG", "Estabelicimento conforme");
        } else if (sizeList < divList) {
            resultScore = "NÃO CONFORME";
            Log.i("LOG", "Estabelicimento Não Conforme");
        }
        scores.setText(Float.toString(scoreList));
    }

    @Override
    public void radioItemChecked(int itemPosition, int optNum) {
        reportItems.get(itemPosition).setSelectedAnswerPosition(optNum);
        reportItems.get(itemPosition).setShine(false);
        switch (optNum) {
            case 1:
                reportItems.get(itemPosition).setOpt1(true);
                this.getScore();
                break;

            case 2:
                reportItems.get(itemPosition).setOpt2(true);
                this.getScore();
                break;

            case 3:
                reportItems.get(itemPosition).setOpt3(true);
                this.getScore();
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void checkAnswers() {
        for (int i = 0; i < reportItems.size(); i++) {
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2() || reportItems.get(i).isOpt3()) {

                checkAnswerList.checkAnswerList(i, reportItems, listTitle, listDescription);
                checkAnswerList.checkAnswerNote(ReportActivity.this, i, reportItems, listNotes);
                checkAnswerList.checkAnswerRadiosButtons(ReportActivity.this, i, reportItems, listRadio);
                checkAnswerList.checkAnswerPhoto(ReportActivity.this, i, reportItems, listPhoto);
            }
        }
    }

    // Update Item List FireBase
    @Override
    public void updateList(int position) {
        NewItemListFireBase nFrag = new NewItemListFireBase();
        Bundle bundle = new Bundle();
        ReportItems items = reportItems.get(position);

        bundle.putString(ReportConstants.ITEM.TITLE, items.getTitle());
        bundle.putString(ReportConstants.ITEM.DESCRIPTION, items.getDescription());
        bundle.putString(ReportConstants.ITEM.KEY, items.getKey());
        Log.d("TestFrag", items.getKey());
        nFrag.setArguments(bundle);
        nFrag.show((ReportActivity.this).getSupportFragmentManager(), "new_item");
    }

    // Remove Item List FireBase
    public void removeItem(int position) {
        ReportItems items = reportItems.get(position);

        alertDialog.showDialog(ReportActivity.this,
                getResources().getString(R.string.remove),
                getResources().getString(R.string.label_remove_item_list),
                getResources().getString(R.string.confirm),
                (dialogInterface, i) -> {
                    Query query = databaseReference.orderByChild(ReportConstants.ITEM.KEY)
                            .equalTo(items.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ds.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), getResources()
                                    .getString(R.string.label_error_update_list), Toast.LENGTH_SHORT).show();
                        }
                    });
                },
                getResources().getString(R.string.cancel), null, false);
    }

    // Insert Note
    public void insertNote(int pos) {
        position = pos;
        ReportNoteFragment fragNote = new ReportNoteFragment(mAdapter);
        Bundle bundle = new Bundle();
        ReportItems items = reportItems.get(position);

        bundle.putString(ReportConstants.ITEM.NOTE, items.getNote());
        bundle.putInt(ReportConstants.ITEM.POSITION, position);
        fragNote.setArguments(bundle);
        Log.d("NoteFrag ", items.getNote() + position);
        fragNote.show((ReportActivity.this).getSupportFragmentManager(), "insert_note");
    }

    // Show Photo Full
    public void fullPhoto(int pos) {
        position = pos;
        FullPhotoFragment fullFragment = new FullPhotoFragment();
        Bundle bundle = new Bundle();
        ReportItems items = reportItems.get(position);

        Bitmap bitmap = items.getPhotoId();

        if (bitmap == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_nothing_image), Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        bundle.putInt(ReportConstants.ITEM.POSITION, position);
        bundle.putByteArray(ReportConstants.ITEM.PHOTO, bytes);

        fullFragment.setArguments(bundle);
        fullFragment.show(getSupportFragmentManager(), "fullPhoto");
        Log.i("FullFrag ", " " + Arrays.toString(bytes));
    }

    // Reset Item Adapter
    @Override
    public void resetItem(int pos) {
        position = pos;
        ReportItems items = reportItems.get(position);

        mAdapter.setImageInItem(position, null);
        mAdapter.insertNote(position, null);
        items.setOpt1(false);
        items.setOpt2(false);
        items.setOpt3(false);
        mAdapter.notifyDataSetChanged();
    }

    // OPen Camera
    public void takePhoto(int pos) {
        position = pos;
        final String[] items = new String[]{
                this.getString(R.string.msg_take_image),
                this.getString(R.string.msg_select_from_gallery)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setItems(items, (dialogInterface, i) -> {
            if (i == 0) {
                takePicture.openCamera(ReportActivity.this);
            } else {
                takePicture.openGallery(ReportActivity.this);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {

                Uri photoUri = takePicture.getPathUri();

                ResizeImage.decodeBitmap(photoUri, mAdapter, position);
                Log.i("LOG", "ImagePathCameraPath " + photoUri + " " + data.getData());
            }
            this.radioItemChecked(position, ReportConstants.ITEM.OPT_NUM1);
        }
        if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri mSelectedUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mAdapter.setImageInItem(position, bitmap);
                this.radioItemChecked(position, ReportConstants.ITEM.OPT_NUM1);
                Log.i("LOG", "ImagePathGallery " + bitmap);

            } catch (IOException e) {
                Crashlytics.logException(e);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > REQUEST_PERMISSIONS
                    && grantResults[REQUEST_PERMISSIONS] == PackageManager.PERMISSION_GRANTED
                    && grantResults[REQUEST_PERMISSIONS_GRANTED] == PackageManager.PERMISSION_GRANTED) {
                this.dialog.dismiss();
                takePicture.openCamera(ReportActivity.this);
            } else {
                snackBarHelper.showSnackBar(this, R.id.fab_new_item, R.string.label_permission_camera);
            }
        }

        if (requestCode == REQUEST_PERMISSIONS_READ_WHITE) {
            if (grantResults.length > REQUEST_PERMISSIONS
                    && grantResults[REQUEST_PERMISSIONS] == PackageManager.PERMISSION_GRANTED
                    && grantResults[REQUEST_PERMISSIONS_GRANTED] == PackageManager.PERMISSION_GRANTED) {
                this.handleSave();
            } else {
                snackBarHelper.showSnackBar(this, R.id.fab_new_item, R.string.label_permission_read);
            }
        }
    }

    // SaveInstance Rotate
    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(LIST_STATE, reportItems);
        savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        reportItems = savedInstanceState.getParcelableArrayList(LIST_STATE);
        savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        super.onRestoreInstanceState(savedInstanceState);
    }*/

    private static void delete(File fileDirectory) {
        if (fileDirectory == null) {
            return;
        } else if (fileDirectory.isDirectory())
            for (File child : Objects.requireNonNull(fileDirectory.listFiles()))
                delete(child);
        boolean delete = fileDirectory.delete();
    }

    private void closeMethods() {
        if (mReportBusiness != null) {
            mReportBusiness.close();
        }
        databaseReference = LibraryClass.closeFireBase();
    }

    @Override
    public void onBackPressed() {
        this.alertDialogClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.closeMethods();
        File file = path.getStorageDir();
        delete(file);
    }
}
