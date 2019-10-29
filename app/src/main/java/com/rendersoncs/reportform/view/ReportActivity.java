package com.rendersoncs.reportform.view;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.BuildConfig;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportCheckListAdapter;
import com.rendersoncs.reportform.animated.AnimatedFloatingButton;
import com.rendersoncs.reportform.async.PDFCreateAsync;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.FullPhotoFragment;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.fragment.ReportNoteFragment;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.listener.OnItemListenerClicked;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;
import com.rendersoncs.reportform.photo.CameraUtil;
import com.rendersoncs.reportform.photo.ResizeImage;
import com.rendersoncs.reportform.util.AlertDialogUtil;
import com.rendersoncs.reportform.util.ListJsonOff;
import com.rendersoncs.reportform.util.SnackBarHelper;

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

public class ReportActivity extends AppCompatActivity implements OnItemListenerClicked {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;

    private static final int REQUEST_CODE_CAMERA = 2000;
    private static final int REQUEST_CODE_GALLERY = 2006;
    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_PERMISSIONS_READ_WHITE = 128;

//    private static final String LIST_STATE = "list_state";
//    private static final String BUNDLE_RECYCLER_LAYOUT= "recycler_layout";

    private ReportBusiness mReportBusiness;
    private ListJsonOff jsonListModeOff = new ListJsonOff();

    private ArrayList<ReportItems> reportItems = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private ReportCheckListAdapter mAdapter;

    private TextView resultCompany, resultEmail, resultDate;
    private PDFCreateAsync pdfCreateAsync = new PDFCreateAsync(ReportActivity.this);
    private AnimatedFloatingButton animated = new AnimatedFloatingButton();

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
    private User user = new User();

    private ResizeImage resizeImage = new ResizeImage();
    private Uri photoUri;
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
        user.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        //databaseReference = LibraryClass.getFirebase().child("users").child(user.getId()).child("list");
        //databaseReference.keepSynced(true);

//        if (savedInstanceState != null) {
//            findViewById(R.id.progressBar).setVisibility(View.GONE);
//            reportItems = savedInstanceState.getParcelableArrayList(LIST_STATE);
//            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
//        } else {
//            this.initViews();
//        }
        this.mReportBusiness = new ReportBusiness(this);
        this.initViews();
        //this.isConnected();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mReportId = bundle.getInt(ReportConstants.ConstantsBundle.REPORT_ID);
        }
        if (mReportId == 0){
            databaseReference = LibraryClass.getFirebase().child("users").child(user.getId()).child("list");
            this.isConnected();
            this.getBundleReportFromDialog();
        } else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
//            EditReportActivity edit = new EditReportActivity();
//            edit.loadEditReportExt(mReportId, mAdapter, mReportBusiness, recyclerView);
            this.loadEditReport();
        }

        FloatingActionButton fab = findViewById(R.id.fab_new_item);
        fab.setOnClickListener(v -> startNewItemListFireBase());

        // Animated FloatingButton
        animated.animatedFab(recyclerView, fab);
    }

    private void initViews() {
        resultCompany = findViewById(R.id.result_company);
        resultEmail = findViewById(R.id.result_email);
        resultDate = findViewById(R.id.result_date);

        mAdapter = new ReportCheckListAdapter(reportItems, this);
        mAdapter.setOnItemListenerClicked(this);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);

//        ItemTouchHelper.Callback callback = new ItemMoveCallBack(mAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    private void loadEditReport() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mReportId = bundle.getInt(ReportConstants.ConstantsBundle.REPORT_ID);

            ReportItems repoEntity = this.mReportBusiness.load(this.mReportId);
            this.resultCompany.setText(repoEntity.getCompany());
            this.resultEmail.setText(repoEntity.getEmail());
            this.resultDate.setText(repoEntity.getDate());

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
                            object.getString(ReportConstants.ITEM.DESCRIPTION), object.getString("title"));
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

                for (int i = 0; i < editNotes.size(); i ++){
                    String notes = editNotes.get(i);
                    mAdapter.insertNote(i, notes);
                    Log.i("log", "NOTES: " + i + notes + " notes");
                }

                for (int i = 0; i < editPhoto.size(); i ++){
                    String photos = editPhoto.get(i);

                    byte[] decodedString = Base64.decode(photos, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    mAdapter.setImageInItem(i, bitmap);
                }

                mAdapter = new ReportCheckListAdapter(reportItems, ReportActivity.this);
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
            this.addItemsFromFireBase();
            //Toast.makeText(getApplicationContext(), "Lista onLine", Toast.LENGTH_SHORT).show();

        } else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            jsonListModeOff.addItemsFromJsonList(reportItems);
            mAdapter.notifyDataSetChanged();
            //Toast.makeText(getApplicationContext(), "Lista offLine", Toast.LENGTH_SHORT).show();
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

                //int index = mKeys.indexOf(key);
                mAdapter.notifyDataSetChanged();
                //mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ReportItems reportItems = dataSnapshot.getValue(ReportItems.class);
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                ReportActivity.this.reportItems.set(index, reportItems);
                //mAdapter.notifyItemChanged(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                int index = mKeys.indexOf(key);
                reportItems.remove(index);
                mKeys.remove(index);
                //mAdapter.notifyItemRemoved(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expandable, menu);
        return true;
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
                        if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_READ_WHITE);
                        } else {
                            this.handleSave();
                        }
                    } else {
                        this.handleSave();
                    }
                }
                break;

            case R.id.close:
                this.alertDialogClose();
                break;

            case R.id.clear:
                if (this.listPhoto.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(ReportActivity.this.findViewById(R.id.fab_new_item), ReportActivity.this.getString(R.string.label_empty_list), Snackbar.LENGTH_LONG);
                    SnackBarHelper.configSnackBar(ReportActivity.this, snackbar);
                    snackbar.show();
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_list_clear), Toast.LENGTH_SHORT).show();
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

    private void alertDialogClose() {
        alertDialog.showDialog(ReportActivity.this,
                getResources().getString(R.string.alert_leave_the_report),
                getResources().getString(R.string.alert_leave_the_report_text),
                getResources().getString(R.string.confirm),
                (dialogInterface, i) -> {
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
        reportItems.setDate(resultDate.getText().toString());

        // Convert ArrayList in Json Object
        for (int i = 0; (i < listRadio.size()) && (i < listTitle.size()) && (i < listDescription.size()) && (i < listNotes.size()) && (i < listPhoto.size()); i++) {
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

        //Save
        if (this.mReportId == 0){
            if (this.mReportBusiness.insert(reportItems)) {
                // Execute Async create PDF
                pdfCreateAsync.execute(reportItems);
                Toast.makeText(getApplicationContext(), R.string.txt_report_save, Toast.LENGTH_SHORT).show();
                this.closeMethods();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.txt_error_save, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            reportItems.setId(this.mReportId);
            if (this.mReportBusiness.update(reportItems)) {
                // Execute Async create PDF
                pdfCreateAsync.execute(reportItems);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_report_update), Toast.LENGTH_SHORT).show();
                this.closeMethods();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.txt_error_save, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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

        //mAdapter.listIDRadio.clear();
        mAdapter.expandState.clear();
        reportItems.clear();
        this.isConnected();

        Snackbar snackbar = Snackbar
                .make(ReportActivity.this.findViewById(R.id.fab_new_item), ReportActivity.this.getString(R.string.label_empty_list), Snackbar.LENGTH_LONG);
        SnackBarHelper.configSnackBar(ReportActivity.this, snackbar);
        snackbar.show();
        //Toast.makeText(getApplicationContext(), R.string.label_empty_list, Toast.LENGTH_SHORT).show();
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

            case 3:
                reportItems.get(itemPosition).setOpt3(true);
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void checkAnswers() {

        for (int i = 0; i < reportItems.size(); i++) {
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2() || reportItems.get(i).isOpt3()) {
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM1) {
                    String C = getResources().getString(R.string.according);
                    listRadio.add(C);
                }
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM2) {
                    String NA = getResources().getString(R.string.not_applicable);
                    listRadio.add(NA);
                }
                if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM3) {
                    String NC = getResources().getString(R.string.not_according);
                    listRadio.add(NC);
                }

                String title = reportItems.get(i).getTitle();
                listTitle.add(title);
                Log.i("LOG", "getTitle " + listTitle);

                String description = reportItems.get(i).getDescription();
                listDescription.add(description);
                Log.i("LOG", "getDescription " + listDescription);

                String note = reportItems.get(i).getNote();
                if (note == null) {
                    note = getResources().getString(R.string.label_not_observation);
                    listNotes.add(note);
                } else {
                    listNotes.add(note);
                    Log.i("LOG", "getNotes " + listNotes);
                }

                Bitmap bitmapPhoto = reportItems.get(i).getPhotoId();
                if (bitmapPhoto == null) {
                    return;
                } else {
                    String encodeImage = resizeImage.getEncoded64Image(bitmapPhoto);
                    listPhoto.add(encodeImage);
                    Log.i("List ", "Lista Photo " + listPhoto.size() + " item");
                }
            }
        }
        Log.i("List ", "Size Photo " + listPhoto.size() + " item");
        Log.i("List ", "Size Radio " + listRadio.size() + " item");
    }

    // Update Item List FireBase
    @Override
    public void updateList(int position) {
        NewItemListFireBase nFrag = new NewItemListFireBase();
        Bundle bundle = new Bundle();
        ReportItems items = reportItems.get(position);

        bundle.putString(ReportConstants.ITEM.TITLE, items.getTitle());
        bundle.putString(ReportConstants.ITEM.DESCRIPTION, items.getDescription());
        Log.d("TestFrag", items.getTitle() + items.getDescription());
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
                    Query query = databaseReference.orderByChild("title").equalTo(items.getTitle());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ds.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_error_update_list), Toast.LENGTH_SHORT).show();
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
    @Override
    public void takePhoto(int pos) {
        position = pos;
        final String[] items = new String[]{
                this.getString(R.string.msg_take_image),
                this.getString(R.string.msg_select_from_galery)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    // Open Gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    // OPen Camera
    private void openCamera() {
        File photoFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        photoFile = path.createImageFile();

                        if (photoFile != null) {
                            photoUri = FileProvider.getUriForFile(ReportActivity.this, BuildConfig.APPLICATION_ID + ".FileProvider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Crashlytics.log(e.getMessage());
                    }
                }
            }
        } else {
            try {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = path.createImageFile(); // verificar versão
                photoUri = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Crashlytics.log(e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {

                ResizeImage.decodeBitmap(photoUri, mAdapter, position);
            }
            radioItemChecked(position, 1);
        }
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri mSelectedUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mAdapter.setImageInItem(position, bitmap);
                radioItemChecked(position, 1);
                Log.i("LOG", "ImagePath " + bitmap);

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
            if (grantResults.length > REQUEST_PERMISSIONS && grantResults[REQUEST_PERMISSIONS] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                dialog.dismiss();
                openCamera();
            } else {
                Snackbar snackbar = Snackbar
                        .make(ReportActivity.this.findViewById(R.id.fab_new_item), ReportActivity.this.getString(R.string.label_permission_camera), Snackbar.LENGTH_LONG);
                SnackBarHelper.configSnackBar(ReportActivity.this, snackbar);
                snackbar.show();
            }
        }

        if (requestCode == REQUEST_PERMISSIONS_READ_WHITE) {
            if (grantResults.length > REQUEST_PERMISSIONS && grantResults[REQUEST_PERMISSIONS] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                this.handleSave();
            } else {
                Snackbar snackbar = Snackbar
                        .make(ReportActivity.this.findViewById(R.id.fab_new_item), ReportActivity.this.getString(R.string.label_permission_read), Snackbar.LENGTH_LONG);
                SnackBarHelper.configSnackBar(ReportActivity.this, snackbar);
                snackbar.show();
            }
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putParcelableArrayList(LIST_STATE, reportItems);
//        savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        reportItems = savedInstanceState.getParcelableArrayList(LIST_STATE);
//        savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    @Override
    public void onBackPressed() {
        this.alertDialogClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeMethods();
    }

    private void closeMethods() {
        if (mReportBusiness != null) {
            mReportBusiness.close();
        }
        databaseReference = LibraryClass.closeFireBase();
    }
}

