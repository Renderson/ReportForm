package com.rendersoncs.reportform.view.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.repository.dao.ReportDataBaseAsyncTask;
import com.rendersoncs.reportform.repository.dao.business.ReportBusiness;
import com.rendersoncs.reportform.view.activitys.cameraX.CameraXMainActivity;
import com.rendersoncs.reportform.view.activitys.login.util.LibraryClass;
import com.rendersoncs.reportform.view.activitys.login.util.User;
import com.rendersoncs.reportform.view.adapter.ReportAdapter;
import com.rendersoncs.reportform.view.adapter.listener.OnItemClickedReport;
import com.rendersoncs.reportform.view.fragment.FullPhotoFragment;
import com.rendersoncs.reportform.view.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.view.fragment.ReportNoteFragment;
import com.rendersoncs.reportform.view.services.async.PDFCreateAsync;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.view.services.util.AlertDialogUtil;
import com.rendersoncs.reportform.view.services.util.ListJsonOff;
import com.rendersoncs.reportform.view.services.util.RVEmptyObserver;
import com.rendersoncs.reportform.view.services.util.SnackBarHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ReportActivity extends AppCompatActivity implements OnItemClickedReport {

    //@BindView(R.id.recycler_view_form)
    private RecyclerView recyclerView;

    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_PERMISSIONS_GRANTED = 1;
    private static final int REQUEST_PERMISSIONS_READ_WHITE = 128;

    // SavedInstanceState
    /*private static final String LIST_STATE = "list_state";
    private static final String BUNDLE_RECYCLER_LAYOUT= "recycler_layout";*/

    private ReportBusiness mReportBusiness;
    private ListJsonOff jsonListModeOff = new ListJsonOff();

    private ArrayList<ReportItems> reportItems = new ArrayList<>();
    private ReportItems reportTakePhoto;
    private final ArrayList<String> mKeys = new ArrayList<>();
    private ReportAdapter mAdapter;
    private FloatingActionButton fab;
    private View emptyLayout;

    private TextView resultCompany, resultEmail, resultDate, scores;
    private String resultController = "";
    private String resultScore = "";
    private PDFCreateAsync pdfCreateAsync = new PDFCreateAsync(ReportActivity.this);

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
    //private TakePicture takePicture = new TakePicture();
    private AlertDialog dialog;

    private AlertDialogUtil alertDialog = new AlertDialogUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

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
        recyclerView = findViewById(R.id.recycler_view_form);

        mAdapter = new ReportAdapter(reportItems);

        RecyclerView.LayoutManager llm = new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(mAdapter);

        // Implementation ItemTouchHelper
        /*ItemTouchHelper.Callback callback = new ItemMoveCallBack(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        mAdapter.notifyDataSetChanged();*/

        mAdapter.setOnItemListenerClicked(this);

        fab = findViewById(R.id.fab_new_item);
        fab.setOnClickListener(v -> startNewItemListFireBase());

        emptyButton.setOnClickListener(v -> startNewItemListFireBase());

        // Animated FloatingButton
        //animated.animatedFab(recyclerView, fab);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void keyboardCloseTouchListener() {
        recyclerView.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            return false;
        });
    }

    private void loadListFire() {
        this.isConnected();
        this.getBundleReportFromDialog();

        //mAdapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, emptyLayout, fab));
    }

    private void loadEditReport() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
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
                        this.radioItemChecked(reportItems.get(i), ReportConstants.ITEM.OPT_NUM1);
                    }
                    if (editConformity.get(i).equals(getResources().getString(R.string.not_applicable))) {
                        this.radioItemChecked(reportItems.get(i), ReportConstants.ITEM.OPT_NUM2);
                    }
                    if (editConformity.get(i).equals(getResources().getString(R.string.not_according))) {
                        this.radioItemChecked(reportItems.get(i), ReportConstants.ITEM.OPT_NUM3);
                    }
                }

                for (int i = 0; i < editNotes.size(); i++) {
                    String notes = editNotes.get(i);

                    if (notes.equals(getString(R.string.label_not_observation))){
                        mAdapter.insertNote(reportItems.get(i), null);
                    } else {
                        mAdapter.insertNote(reportItems.get(i), notes);
                    }
                }

                for (int i = 0; i < editPhoto.size(); i++) {
                    String photos = editPhoto.get(i);

                    mAdapter.setImageInItem(reportItems.get(i), photos);
                }

                mAdapter = new ReportAdapter(reportItems);
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

            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        addItemsFromFireBase();
                    } else {
                        addItemsFromListOFF();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.label_error_return), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            addItemsFromListOFF();
        }
    }

    private void addItemsFromListOFF() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.action_add_item).setVisibility(View.GONE);
        fab.setEnabled(false);
        jsonListModeOff.addItemsFromJsonList(reportItems);
        mAdapter.notifyDataSetChanged();
    }

    // Sync RecyclerView with FireBase
    private void addItemsFromFireBase() {
        databaseReference = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS)
                .child(user.getId()).child(ReportConstants.FIREBASE.FIRE_LIST);

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
        searchView.setQueryHint(getString(R.string.label_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });
        searchView.setIconified(true);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                return true;

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
                        listRadio.size() + " " +
                        getResources().getString(R.string.alert_punctuation_label2, scores.getText().toString()),
                getResources().getString(R.string.confirm),
                (dialogInterface, i) ->
                        this.handleSave(),
                getResources().getString(R.string.cancel), (dialogInterface, i) ->
                        this.clearList(), false);
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

        //mAdapter.expandState.clear();
        reportItems.clear();
        this.clearList();
        this.getScore();
        this.isConnected();

        snackBarHelper.showSnackBar(ReportActivity.this, R.id.fab_new_item, R.string.label_empty_list);
    }

    private void getScore() {
        ArrayList<String> listMax = new ArrayList<>();
        listMax.clear();

        float startingPoints = 10f;
        float losePoints = 0.7f;
        float average = 5.0f;

        for (int i = 0; i < reportItems.size(); i++) {
            if (reportItems.get(i).isOpt3()) {
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM3) {
                    String NC = getResources().getString(R.string.not_according);
                    listMax.add(NC);
                }
            }
        }
        int listRadioMax = listMax.size();
        float scoreList = listRadioMax * losePoints;
        float score = startingPoints - scoreList;

        if (score >= average) {
            resultScore = getString(R.string.according).toUpperCase();
        } else {
            resultScore = getString(R.string.not_according).toUpperCase();
        }
        scores.setText(Float.toString(score));
    }

    @Override
    public void radioItemChecked(ReportItems reportItems, int optNum) {
        reportItems.setSelectedAnswerPosition(optNum);
        reportItems.setShine(false);
        switch (optNum) {
            case 1:
                reportItems.setOpt1(true);
                this.getScore();
                break;

            case 2:
                reportItems.setOpt2(true);
                this.getScore();
                break;

            case 3:
                reportItems.setOpt3(true);
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
                checkAnswerList.checkAnswerPhoto(i, reportItems, listPhoto);
            }
        }
    }

    // Update Item List FireBase
    @Override
    public void updateList(ReportItems report) {
        NewItemListFireBase nFrag = new NewItemListFireBase();
        Bundle bundle = new Bundle();

        bundle.putString(ReportConstants.ITEM.TITLE, report.getTitle());
        bundle.putString(ReportConstants.ITEM.DESCRIPTION, report.getDescription());
        bundle.putString(ReportConstants.ITEM.KEY, report.getKey());
        nFrag.setArguments(bundle);
        nFrag.show((ReportActivity.this).getSupportFragmentManager(), "new_item");
    }

    // Remove Item List FireBase
    public void removeItem(ReportItems reportItems) {

        alertDialog.showDialog(ReportActivity.this,
                getResources().getString(R.string.remove),
                getResources().getString(R.string.label_remove_item_list),
                getResources().getString(R.string.confirm),
                (dialogInterface, i) -> {
                    Query query = databaseReference.orderByChild(ReportConstants.ITEM.KEY)
                            .equalTo(reportItems.getKey());
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
    public void insertNote(ReportItems reportItems) {
        ReportNoteFragment fragNote = new ReportNoteFragment(mAdapter, reportItems);
        Bundle bundle = new Bundle();

        bundle.putString(ReportConstants.ITEM.NOTE, reportItems.getNote());
        fragNote.setArguments(bundle);
        fragNote.show((ReportActivity.this).getSupportFragmentManager(), "insert_note");
    }

    // Show Photo Full
    public void fullPhoto(ReportItems reportItems) {
        FullPhotoFragment fullFragment = new FullPhotoFragment();
        Bundle bundle = new Bundle();

        String photo = reportItems.getPhotoPath();

        if (photo == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_nothing_image), Toast.LENGTH_SHORT).show();
            return;
        }

        bundle.putString(ReportConstants.ITEM.PHOTO, photo);

        fullFragment.setArguments(bundle);
        fullFragment.show(getSupportFragmentManager(), "fullPhoto");
    }

    // Reset Item Adapter
    @Override
    public void resetItem(ReportItems reportItems) {

        mAdapter.setImageInItem(reportItems, null);
        mAdapter.insertNote(reportItems, null);
        reportItems.setOpt1(false);
        reportItems.setOpt2(false);
        reportItems.setOpt3(false);
        this.getScore();
        mAdapter.notifyDataSetChanged();
    }

    // OPen Camera
    public void takePhoto(ReportItems reportItems) {
        reportTakePhoto = reportItems;
        final String[] items = new String[]{
                this.getString(R.string.msg_take_image),
                this.getString(R.string.msg_select_from_gallery)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setItems(items, (dialogInterface, i) -> {
            if (i == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void openGallery() {
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(it, ReportConstants.PHOTO.REQUEST_CODE_GALLERY);
    }

    private void openCamera() {
        Intent it = new Intent(this, CameraXMainActivity.class);
        startActivityForResult(it, ReportConstants.PHOTO.REQUEST_CAMERA_X);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_CAMERA ||
                    resultCode == ReportConstants.PHOTO.REQUEST_CAMERA_X) {

                File file = (File) data.getExtras().get(ReportConstants.PHOTO.RESULT_CAMERA_X);
                mAdapter.setImageInItem(reportTakePhoto, file.getPath());

                Log.i("LOG", "CAMERA " + " " + file.getPath());
            }
            this.radioItemChecked(reportTakePhoto, ReportConstants.ITEM.OPT_NUM1);
        }
        if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                File file = new File(getRealPathFromURI(uri));
                mAdapter.setImageInItem(reportTakePhoto, file.getPath());

                Log.i("LOG", "GALLERY " + " " + file.getPath());

            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    protected void onResume() {
        super.onResume();
        this.keyboardCloseTouchListener();
        if (reportItems.isEmpty()) {
            this.showLayoutEmpty();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.closeMethods();
    }
}
