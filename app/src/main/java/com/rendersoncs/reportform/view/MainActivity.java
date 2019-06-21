package com.rendersoncs.reportform.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportListAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.ReportFormDialog;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.listener.OnInteractionListener;
import com.rendersoncs.reportform.observer.RVEmptyObserver;
import com.rendersoncs.reportform.service.NetworkConnectedService;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFireBaseAnalytics;
    NetworkConnectedService netService = new NetworkConnectedService(this);

    private ViewHolder viewHolder = new ViewHolder();
    private ReportBusiness reportBusiness;
    private OnInteractionListener listener;

    private static final int REQUEST_PERMISSIONS_CODE = 128;
    private static final String PACKAGE_FILE_PROVIDER = "com.rendersoncs.reportform.FileProvider";

    public Context context;
    View emptyLayout;
    Button emptyButton;
    View floatingActionButton;

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

        this.listener = new OnInteractionListener() {

            // Click list
            @Override
            public void onListClick(int reportId) {
                // Open ReportResume pass bundle for ID
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "list_id");
                mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                bundle.putInt(ReportConstants.BundleConstants.REPORT_ID, reportId);

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

                Repo repo = reportBusiness.load(reportId);

                Uri uri;
                String subject = String.format("Relatorio-%s-%s", repo.getCompany(), repo.getDate());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report" + "/" + subject + ".pdf");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri = FileProvider.getUriForFile(MainActivity.this, PACKAGE_FILE_PROVIDER, file);
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
                Repo repo = reportBusiness.load(reportId);

                Uri uri;
                String subject = String.format("Relatorio-%s-%s", repo.getCompany(), repo.getDate());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report" + "/" + subject + ".pdf");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri = FileProvider.getUriForFile(MainActivity.this, PACKAGE_FILE_PROVIDER, file);
                } else {
                    uri = Uri.fromFile(file);
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("pdf/plain");
                intent.putExtra(intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { repo.getEmail()});
                intent.putExtra(Intent.EXTRA_TEXT, "Em anexo o relatório da empresa " + repo.getCompany() + " concluído!" + " Realizado no dia " + repo.getDate());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Compartilhar"));
            }

            // Delete Item list
            @Override
            public void onDeleteClick(int id) {
                // Remove relatórios do banco de dados
                reportBusiness.remove(id);
                Toast.makeText(MainActivity.this, R.string.txt_report_removed, Toast.LENGTH_LONG).show();

                // Lista novamente os relatórios
                loadReport();
            }
        };

        // Define um layout
        this.viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));

        floatingActionButton = findViewById(R.id.floatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReportFormDialog();
            }
        });

        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReportFormDialog();
            }
        });
    }

    private void startReportFormDialog() {
        ReportFormDialog reportFormDialog = new ReportFormDialog();
        reportFormDialog.show(getSupportFragmentManager(), "report_dialog");
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.loadReport();
    }

    private void loadReport() {
        List<Repo> repoEntityList = this.reportBusiness.getInvited();

        // Definir um adapter
        ReportListAdapter reportListAdapter = new ReportListAdapter(repoEntityList, listener);
        this.viewHolder.recyclerView.setAdapter(reportListAdapter);

        // Notifica o Adapter mudança na lista
        reportListAdapter.notifyDataSetChanged();

        // Mostra imagen quando não á itens
        reportListAdapter.registerAdapterDataObserver(new RVEmptyObserver(this.viewHolder.recyclerView, emptyLayout, floatingActionButton));

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
}