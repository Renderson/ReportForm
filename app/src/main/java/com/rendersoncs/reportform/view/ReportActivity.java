package com.rendersoncs.reportform.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.fragment.app.FragmentTransaction;
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
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.BuildConfig;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportCheckListAdapter;
import com.rendersoncs.reportform.animated.AnimatedFloatingButton;
import com.rendersoncs.reportform.async.PDFAsyncTask;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity implements OnItemListenerClicked {

    @BindView(R.id.recycler_view_form)
    RecyclerView recyclerView;

    private static final int REQUEST_CODE_CAMERA = 2000;
    private static final int REQUEST_CODE_GALLERY = 2006;
    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_PERMISSIONS_READ_WHITE = 128;

    private ReportBusiness mReportBusiness;
    private ListJsonOff jsonListModeOff = new ListJsonOff();

    private ArrayList<ReportItems> reportItems = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private ReportCheckListAdapter mAdapter;

    private TextView resultCompany, resultEmail, resultDate;
    private AnimatedFloatingButton animated = new AnimatedFloatingButton();

    private ArrayList<String> listTitle = new ArrayList<>();
    private ArrayList<String> listDescription = new ArrayList<>();
    private ArrayList<String> listRadio = new ArrayList<>();
    private ArrayList<String> listPhoto = new ArrayList<>();
    private JSONArray jsArray = new JSONArray();

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
        user.setId(mAuth.getCurrentUser().getUid());

        databaseReference = LibraryClass.getFirebase().child("users").child(user.getId()).child("list");
        //databaseReference.keepSynced(true);

        mAdapter = new ReportCheckListAdapter(reportItems, this);
        mAdapter.setOnItemListenerClicked(this);
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

        FloatingActionButton fab = findViewById(R.id.fab_new_item);
        fab.setOnClickListener(v -> startNewItemListFireBase());
        //mAdapter.notifyDataSetChanged();

        // Animated FloatingButton
        animated.animatedFab(recyclerView, fab);
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

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expandable, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (mAdapter.listIDRadio.isEmpty()) {
                    alertDialog.showDialog(ReportActivity.this,
                            getResources().getString(R.string.txt_empty_report),
                            getResources().getString(R.string.txt_choice_item),
                            getResources().getString(R.string.txt_back),
                            (dialogInterface, i) -> {
                            },
                            null, null, false);
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
                if (mAdapter.listIDRadio.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lista vazia!", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.showDialog(ReportActivity.this,
                            "Limpar lista?",
                            "Deseja realmente limpar a seção? Esse processo vai apagar todo seu progresso!",
                            "Sim",
                            (dialogInterface, i) -> clearRadioButton(),
                            "Não", null, false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDialogClose() {
        alertDialog.showDialog(ReportActivity.this,
                getResources().getString(R.string.alert_leave_the_report),
                getResources().getString(R.string.txt_leave_the_report),
                getResources().getString(R.string.txt_continue),
                (dialogInterface, i) -> {
                    this.closeMethods();
                    finish();
                },
                getResources().getString(R.string.txt_cancel), null, false);
    }

    // Save Company, Email, Data, List
    private void handleSave() {

        this.checkAnswers();

        final ReportItems reportItems = new ReportItems();
        reportItems.setCompany(resultCompany.getText().toString());
        reportItems.setEmail(resultEmail.getText().toString());
        reportItems.setDate(resultDate.getText().toString());

        // Convert ArrayList in Json Object
        for (int i = 0; (i < listRadio.size()) && (i < listTitle.size()) && (i < listDescription.size()) && (i < listPhoto.size()); i++) {
            JSONObject jsObject = new JSONObject();
            try {
                jsObject.put("title_list", listTitle.get(i));
                jsObject.put("description_list", listDescription.get(i));
                jsObject.put("radio_tx", listRadio.get(i));
                jsObject.put("photo_list", listPhoto.get(i));

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
            PDFAsyncTask asy = new PDFAsyncTask(ReportActivity.this);
            asy.execute(reportItems);
            Toast.makeText(getApplicationContext(), R.string.txt_report_save, Toast.LENGTH_SHORT).show();
            this.closeMethods();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.txt_error_save, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Clear all every Lists and reload Adapter
    private void clearRadioButton() {
        for (int i = 0; i < reportItems.size(); i++) {
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2()) {
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

        Toast.makeText(getApplicationContext(), "Lista vazia!", Toast.LENGTH_SHORT).show();
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

    private void checkAnswers() {

        for (int i = 0; i < reportItems.size(); i++) {
            if (reportItems.get(i).isOpt1() || reportItems.get(i).isOpt2()) {
                if (reportItems.get(i).getSelectedAnswerPosition() == 1) {
                    String yes = "Sim";
                    listRadio.add(yes);
                } else {
                    String not = "Não";
                    listRadio.add(not);
                }

                String title = reportItems.get(i).getTitle();
                listTitle.add(title);
                Log.i("LOG", "getTitle " + listTitle);

                String description = reportItems.get(i).getDescription();
                listDescription.add(description);
                Log.i("LOG", "getDescription " + listDescription);

                Bitmap bitmapPhoto = reportItems.get(i).getPhotoId();
                String encodeImage = resizeImage.getEncoded64Image(bitmapPhoto);
                listPhoto.add(encodeImage);
                Log.i("List ", "Lista Photo " + listPhoto + " item");

            }
        }
    }

    @Override
    public void updateList(int position) {
        NewItemListFireBase nFrag = new NewItemListFireBase();
        Bundle bundle = new Bundle();
        ReportItems items = reportItems.get(position);

        bundle.putString("title", items.getTitle());
        bundle.putString("desc", items.getDescription());
        Log.d("TestFrag", items.getTitle() + items.getDescription());
        nFrag.setArguments(bundle);
        nFrag.show((ReportActivity.this).getSupportFragmentManager(), "new_item");

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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

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
        }
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri mSelectedUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mAdapter.setImageInItem(position, bitmap);
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

