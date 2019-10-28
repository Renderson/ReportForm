package com.rendersoncs.reportform.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportListAdapter;
import com.rendersoncs.reportform.animated.AnimatedFloatingButton;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.AboutFragment;
import com.rendersoncs.reportform.fragment.ReportFormDialog;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.listener.OnInteractionListener;
import com.rendersoncs.reportform.login.LoginActivity;
import com.rendersoncs.reportform.login.RemoveUserActivity;
import com.rendersoncs.reportform.login.UpdatePasswordActivity;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;
import com.rendersoncs.reportform.service.AccessDocument;
import com.rendersoncs.reportform.service.NetworkConnectedService;
import com.rendersoncs.reportform.util.GetInfoUserFirebase;
import com.rendersoncs.reportform.util.RVEmptyObserver;
import com.rendersoncs.reportform.util.SnackBarHelper;

import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAnalytics mFireBaseAnalytics;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;

    private NetworkConnectedService netService = new NetworkConnectedService();
    private AnimatedFloatingButton animated = new AnimatedFloatingButton();

    private ViewHolder viewHolder = new ViewHolder();
    private ReportBusiness reportBusiness;
    private OnInteractionListener listener;

    private DrawerLayout drawerLayout;
    private GetInfoUserFirebase info = new GetInfoUserFirebase();
    DatabaseReference databaseReference;

    public Context context;
    View emptyLayout;
    Button emptyButton;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report_list);

        // Check NetWorking
        this.netService.isConnected(MainActivity.this);

        this.checkUserFireBase();

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener( authStateListener );
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = LibraryClass.getFirebase();
        //databaseReference.keepSynced(true);

        // Create Drawer layout
        this.createDrawerLayout(user, toolbar);

    }

    private void checkUserFireBase() {
        authStateListener = firebaseAuth -> {

            if( firebaseAuth.getCurrentUser() == null  ){
                Intent intent = new Intent( MainActivity.this, LoginActivity.class );
                startActivity( intent );
                finish();
            }
        };
    }

    private void init() {

        emptyLayout = findViewById(R.id.layout_empty);
        emptyButton = findViewById(R.id.action_add_report);

        // Obter a recycler
        this.viewHolder.recyclerView = findViewById(R.id.recycler_view);

        // Camada Business
        this.reportBusiness = new ReportBusiness(this);

        // Listener
        this.clickListenerItems();

        // Define um layout
        this.viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));

        fab = findViewById(R.id.floatButton);
        fab.setOnClickListener(v -> startReportFormDialog());

        emptyButton.setOnClickListener(v -> startReportFormDialog());

        // Animated FAB
        animated.animatedFab(viewHolder.recyclerView, fab);
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

        TextView profileName = headerLayout.findViewById(R.id.txt_profile_name);
        TextView profileEmail = headerLayout.findViewById(R.id.txt_profile_mail);
        ImageView profileView = headerLayout.findViewById(R.id.img_profile);

        info.getInfoUserFirebase(getApplicationContext(), user, databaseReference, profileName, profileEmail, profileView);
    }

    private void clickListenerItems() {
        this.listener = new OnInteractionListener() {

            // Click list
            @Override
            public void onListClick(int reportId) {
                // Open ReportResume pass bundle for ID
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "list_id");
                mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                bundle.putInt(ReportConstants.ConstantsBundle.REPORT_ID, reportId);

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

                AccessDocument accessDocument = new AccessDocument(reportId).invoke();
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

                AccessDocument accessDocument = new AccessDocument(reportId).invoke();
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

            @Override
            public void onEditReport(int reportId) {
                Bundle bundle = new Bundle();
                bundle.putInt(ReportConstants.ConstantsBundle.REPORT_ID, reportId);

                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            // Delete Item list
            @Override
            public void onDeleteClick(int id) {
                // Removed report DB
                reportBusiness.remove(id);
                fab.show();
                Snackbar snackbar = Snackbar
                        .make(MainActivity.this.findViewById(R.id.floatButton), MainActivity.this.getString(R.string.txt_report_removed), Snackbar.LENGTH_LONG);
                SnackBarHelper.configSnackBar(MainActivity.this, snackbar);
                snackbar.show();

                // List the reports again
                loadReport();
            }
        };
    }

    private void startReportFormDialog() {
        ReportFormDialog reportFormDialog = new ReportFormDialog();
        reportFormDialog.show(getSupportFragmentManager(), "report_dialog");
    }

    private void loadReport() {
        List<ReportItems> reportItems = this.reportBusiness.getInvited();

        // Define an adapter
        ReportListAdapter reportListAdapter = new ReportListAdapter(reportItems, listener);
        this.viewHolder.recyclerView.setAdapter(reportListAdapter);

        // Notify Adapter Change in List
        reportListAdapter.notifyDataSetChanged();

        // Show image when no items
        reportListAdapter.registerAdapterDataObserver(new RVEmptyObserver(this.viewHolder.recyclerView, emptyLayout, fab));
    }

    private static class ViewHolder {
        RecyclerView recyclerView;
    }

    // DrawerLayout Menu
    private void inflateMenuNavigation(NavigationView mNavigationView) {
        User user = new User();
        if (user.isSocialNetworkLogged(getApplicationContext())){
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
                startReportFormDialog();
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

            case R.id.menu_about_us: {
                AboutFragment fragment = new AboutFragment();
                fragment.show(getSupportFragmentManager(), "report_sheet_about");
                break;
            }

            case R.id.menu_logout: {
                FirebaseAuth.getInstance().signOut();
                finish();
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

        if (reportBusiness != null){
            reportBusiness.close();
        }

        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}