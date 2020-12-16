package com.rendersoncs.report.view.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.rendersoncs.report.R;
import com.rendersoncs.report.infrastructure.constants.ReportConstants;
import com.rendersoncs.report.infrastructure.pdf.AccessDocumentPDF;
import com.rendersoncs.report.infrastructure.util.GetInfoUserFireBase;
import com.rendersoncs.report.infrastructure.util.RVEmptyObserver;
import com.rendersoncs.report.infrastructure.util.SharePrefInfoUser;
import com.rendersoncs.report.infrastructure.util.SnackBarHelper;
import com.rendersoncs.report.model.ReportItems;
import com.rendersoncs.report.repository.dao.business.ReportBusiness;
import com.rendersoncs.report.repository.net.NetworkConnectedService;
import com.rendersoncs.report.view.fragment.AboutFragment;
import com.rendersoncs.report.view.fragment.ChooseThemeDialogFragment;
import com.rendersoncs.report.view.fragment.NewReportFragment;
import com.rendersoncs.report.view.login.LoginActivity;
import com.rendersoncs.report.view.login.RemoveUserActivity;
import com.rendersoncs.report.view.login.UpdatePasswordActivity;
import com.rendersoncs.report.view.login.util.LibraryClass;
import com.rendersoncs.report.view.login.util.User;
import com.rendersoncs.report.view.reportEdit.ReportEdit;
import com.rendersoncs.report.view.resume.ReportResume;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChooseThemeDialogFragment.SingleChoiceListener {

    private final NetworkConnectedService connectedService = new NetworkConnectedService();
    private final SnackBarHelper snackBarHelper = new SnackBarHelper();
    private final SharePrefInfoUser sharePrefInfoUser = new SharePrefInfoUser();
    private final GetInfoUserFireBase getInfoUserFireBase = new GetInfoUserFireBase();
    private final ViewHolder viewHolder = new ViewHolder();

    private FirebaseAnalytics mFireBaseAnalytics;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    private ReportBusiness reportBusiness;
    private ReportInteractionListener listener;

    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;

    private View emptyLayout;
    private FloatingActionButton floatingActionButton;
    private TextView profileName;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.checkUserFireBase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report_list);

        preferences = getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, MODE_PRIVATE);

        // Check NetWorking
        this.connectedService.isConnected(MainActivity.this);

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = LibraryClass.getFirebase();

        // Create Drawer layout
        this.createDrawerLayout(user, toolbar);
    }

    private void checkUserFireBase() {
        authStateListener = firebaseAuth -> {

            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    private void init() {

        emptyLayout = findViewById(R.id.layout_empty);
        Button newReport = findViewById(R.id.action_add_report);

        // Get the recycler
        this.viewHolder.recyclerView = findViewById(R.id.recycler_view);

        // Business layer
        this.reportBusiness = new ReportBusiness(this);

        // Listener
        this.clickListenerItems();

        // Define a layout
        this.viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButton = findViewById(R.id.floatButton);
        floatingActionButton.setOnClickListener(v -> startNewReportDialog());

        newReport.setOnClickListener(v -> startNewReportDialog());
    }

    // Create Drawer Layout
    private void createDrawerLayout(FirebaseUser user, Toolbar toolbar) {
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView mNavigationView = findViewById(R.id.navView);
        View headerLayout = mNavigationView.getHeaderView(0);
        mNavigationView.setNavigationItemSelectedListener(this);

        this.inflateMenuNavigation(mNavigationView);

        profileName = headerLayout.findViewById(R.id.txt_profile_name);
        TextView profileEmail = headerLayout.findViewById(R.id.txt_profile_mail);
        ImageView profileView = headerLayout.findViewById(R.id.img_profile);

        getInfoUserFireBase.getInfoUserFireBase(getApplicationContext(), user, databaseReference, profileName, profileEmail, profileView);
    }

    private void clickListenerItems() {
        this.listener = new ReportInteractionListener() {

            // Click list
            @Override
            public void onClickReport(int reportId) {
                // Open ReportResume pass bundle for ID
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "list_id");
                mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                bundle.putInt(ReportConstants.REPORT.REPORT_ID, reportId);

                Intent intent = new Intent(MainActivity.this, ReportResume.class);
                intent.putExtras(bundle);

                bundle = new Bundle();
                bundle.putString("select_list", "list");
                mFireBaseAnalytics.logEvent("select_list_event", bundle);
                startActivity(intent);
            }

            // Open PDF
            @Override
            public void onOpenPdf(int reportId) {

                AccessDocumentPDF accessDocument = new AccessDocumentPDF(reportId).invoke();
                Uri uri = accessDocument.getUri();
                String subject = accessDocument.getSubject();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }

            // Share PDF
            @Override
            public void onShareReport(int reportId) {

                AccessDocumentPDF accessDocument = new AccessDocumentPDF(reportId).invoke();
                ReportItems reportItems = accessDocument.getReportItems();
                Uri uri = accessDocument.getUri();
                String subject = accessDocument.getSubject();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("pdf/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{reportItems.getEmail()});
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.label_attach_report, reportItems.getCompany()) + " " + reportItems.getDate());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
            }

            // Edit Report
            @Override
            public void onEditReport(int reportId) {
                Bundle bundle = new Bundle();
                bundle.putInt(ReportConstants.REPORT.REPORT_ID, reportId);

                Intent intent = new Intent(MainActivity.this, ReportEdit.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            // Delete Item list
            @Override
            public void onDeleteReport(int reportId) {
                // Removed report DB and File PDF
                deletePhotosDirectory(reportId);

                AccessDocumentPDF accessDocument = new AccessDocumentPDF(reportId).invoke();
                Uri uri = accessDocument.getUri();
                String subject = accessDocument.getSubject();
                MainActivity.this.getContentResolver().delete(uri, subject, null);

                reportBusiness.remove(reportId);
                floatingActionButton.show();

                // List the reports again
                loadReport();

                snackBarHelper.showSnackBar(MainActivity.this, R.id.floatButton, R.string.txt_report_removed);
            }
        };
    }

    private void deletePhotosDirectory(int reportId) {
        ReportItems reportItems = reportBusiness.load(reportId);

        try {
            JSONArray arrayL = new JSONArray(reportItems.getListJson());
            for (int i = 0; i < arrayL.length(); i++) {
                JSONObject obj = arrayL.getJSONObject(i);
                String selected = obj.getString(ReportConstants.ITEM.PHOTO);

                File file = new File(selected);
                boolean b = file.delete();
                Log.i("deletePhotosDirectory", String.valueOf(b));
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void startNewReportDialog() {
        NewReportFragment newReportFragment = new NewReportFragment();
        String controller = profileName.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString(ReportConstants.ITEM.CONTROLLER, controller);
        newReportFragment.setArguments(bundle);

        newReportFragment.show((MainActivity.this).getSupportFragmentManager(), newReportFragment.getTag());
        Log.i("NameInfo ", controller);
    }

    private void loadReport() {
        List<ReportItems> reportItems = this.reportBusiness.getInvited();

        // Define an adapter
        ReportListAdapter reportListAdapter = new ReportListAdapter(reportItems, listener);
        this.viewHolder.recyclerView.setAdapter(reportListAdapter);

        // Notify Adapter Change in List
        reportListAdapter.notifyDataSetChanged();

        // Show image when no items
        reportListAdapter.registerAdapterDataObserver(new RVEmptyObserver(this.viewHolder.recyclerView, emptyLayout, floatingActionButton));
    }

    @Override
    public void onPositiveButtonClicked(@NotNull String[] list, int position) {
        if (position == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveThemeState(position);
            recreate();
        } else if (position == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveThemeState(position);
            recreate();
        }
    }

    private void saveThemeState(int position) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ReportConstants.THEME.KEY_THEME, position);
        editor.apply();
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    private static class ViewHolder {
        RecyclerView recyclerView;
    }

    // DrawerLayout Menu
    private void inflateMenuNavigation(NavigationView mNavigationView) {
        User user = new User();
        if (user.isSocialNetworkLogged(getApplicationContext())) {
            mNavigationView.getMenu().clear();
            mNavigationView.inflateMenu(R.menu.menu_social_network_logged);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_home: {
                break;
            }

            case R.id.menu_new_report: {
                startNewReportDialog();
                break;
            }

            case R.id.menu_reset: {
                startActivity(new Intent(this, UpdatePasswordActivity.class));
                break;
            }

            case R.id.menu_delete: {
                startActivity(new Intent(this, RemoveUserActivity.class));
                break;
            }

            case R.id.menu_dark_theme: {
                DialogFragment dialogFragment = new ChooseThemeDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
                break;
            }

            case R.id.menu_about_us: {
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.show(getSupportFragmentManager(), aboutFragment.getTag());
                break;
            }

            case R.id.menu_logout: {
                FirebaseAuth.getInstance().signOut();
                finish();
                sharePrefInfoUser.clearSharePref(this);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.init();
        this.loadReport();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (reportBusiness != null) {
            reportBusiness.close();
        }

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}