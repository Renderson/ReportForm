package com.rendersoncs.reportform.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportListAdapter;
import com.rendersoncs.reportform.animated.AnimatedFloatingButton;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.BottomSheetFragment;
import com.rendersoncs.reportform.fragment.ReportFormDialog;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.listener.OnInteractionListener;
import com.rendersoncs.reportform.login.LoginActivity;
import com.rendersoncs.reportform.login.util.User;
import com.rendersoncs.reportform.util.RVEmptyObserver;
import com.rendersoncs.reportform.service.NetworkConnectedService;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFireBaseAnalytics;
    private NetworkConnectedService netService = new NetworkConnectedService(this);
    private AnimatedFloatingButton animated = new AnimatedFloatingButton();

    private ViewHolder viewHolder = new ViewHolder();
    private ReportBusiness reportBusiness;
    private OnInteractionListener listener;

    private static final int REQUEST_PERMISSIONS_CODE = 128;

    public Context context;
    View emptyLayout;
    Button emptyButton;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Check NetWorking
        this.netService.isConnected(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report_list);

        emptyLayout = findViewById(R.id.layout_empty);

        emptyButton = findViewById(R.id.action_add_report);

        //Check permissions Android 6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        //Obter a recycler
        this.viewHolder.recyclerView = findViewById(R.id.recycler_view);

        // Camada Business
        this.reportBusiness = new ReportBusiness(this);

        // Listener
        this.clickListenerItems();

        // Define um layout
        this.viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));

        // Animated FAB
        animatedFloatingButtom();
        //animated.animatedFab(viewHolder.recyclerView, fab);

        fab = findViewById(R.id.floatButton);
        fab.setOnClickListener(v -> startReportFormDialog());

        emptyButton.setOnClickListener(v -> startReportFormDialog());
    }

    private void animatedFloatingButtom() {
        this.viewHolder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

            // Create PDF
            @Override
            public void onOpenPdf(int reportId) {

                ReportItems reportItems = reportBusiness.load(reportId);

                Uri uri;
                String subject = String.format("Relatorio-%s-%s", reportItems.getCompany(), reportItems.getDate());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report" + "/" + subject + ".pdf");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri = FileProvider.getUriForFile(MainActivity.this, ReportConstants.ConstantsProvider.PACKAGE_FILE_PROVIDER, file);
                } else {
                    uri = Uri.fromFile(file);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.putExtra(intent.EXTRA_SUBJECT, subject);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);

            }

            // Share PDF
            @Override
            public void onShareReport(int reportId) {
                // Share PDF the Report
                ReportItems reportItems = reportBusiness.load(reportId);

                Uri uri;
                String subject = String.format("Relatorio-%s-%s", reportItems.getCompany(), reportItems.getDate());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report" + "/" + subject + ".pdf");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri = FileProvider.getUriForFile(MainActivity.this, ReportConstants.ConstantsProvider.PACKAGE_FILE_PROVIDER, file);
                } else {
                    uri = Uri.fromFile(file);
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("pdf/plain");
                intent.putExtra(intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{reportItems.getEmail()});
                intent.putExtra(Intent.EXTRA_TEXT, "Em anexo o relatório da empresa " + reportItems.getCompany() + " concluído!" + " Realizado no dia " + reportItems.getDate());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Compartilhar"));
            }

            @Override
            public void onBottomSheet(int reportId) {
                startBottomSheetFragment();
            }

            // Delete Item list
            @Override
            public void onDeleteClick(int id) {
                // Remove relatórios do banco de dados
                reportBusiness.remove(id);
                fab.show();
                Toast.makeText(MainActivity.this, R.string.txt_report_removed, Toast.LENGTH_LONG).show();

                // Lista novamente os relatórios
                loadReport();
            }
        };
    }

    private void startReportFormDialog() {
        ReportFormDialog reportFormDialog = new ReportFormDialog();
        reportFormDialog.show(getSupportFragmentManager(), "report_dialog");
    }

    private void startBottomSheetFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), "report_sheet");
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.loadReport();
    }

    private void loadReport() {
        List<ReportItems> reportItems = this.reportBusiness.getInvited();

        // Definir um adapter
        ReportListAdapter reportListAdapter = new ReportListAdapter(reportItems, listener);
        this.viewHolder.recyclerView.setAdapter(reportListAdapter);

        // Notifica o Adapter mudança na lista
        reportListAdapter.notifyDataSetChanged();

        // Mostra imagen quando não á itens
        reportListAdapter.registerAdapterDataObserver(new RVEmptyObserver(this.viewHolder.recyclerView, emptyLayout, fab));

    }

    private static class ViewHolder {
        RecyclerView recyclerView;
    }

    // Permission
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)
                            && grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        loadReport();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                dialogPermission(getString(R.string.alert_dialog_permission), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                dialogPermission(getString(R.string.alert_dialog_permission), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
            }
        }
    }

    private void dialogPermission(String message, final String[] permission) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.txt_permission)
                .setMessage(message)
                .setPositiveButton(R.string.txt_to_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, permission, REQUEST_PERMISSIONS_CODE);
                    }
                })
                .setNegativeButton(R.string.txt_deny, null)
                .show();
    }
    // Final code permission

    // Implemetation Firebase
    // Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = new User();

        if (user.isSocialNetworkLogged(this)) {
            getMenuInflater().inflate(R.menu.menu_social_network_logged, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu, menu);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action.update) {
//            startActivity(new Intent(this, UpdateActivity.class));
//        } else if (id == R.id.action_logout) {
        if (id == R.id.action_logout)
            FirebaseAuth.getInstance().signOut();
            finish();
        //}
        return super.onOptionsItemSelected(item);
    }
}