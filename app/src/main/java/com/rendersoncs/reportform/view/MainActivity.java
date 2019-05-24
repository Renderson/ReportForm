package com.rendersoncs.reportform.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.ReportListAdapter;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.fragment.ReportFormDialog;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.listener.OnInteractionListener;
import com.rendersoncs.reportform.observer.RVEmptyObserver;

import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ViewHolder viewHolder = new ViewHolder();
    private ReportBusiness reportBusiness;
    private OnInteractionListener listener;

    private static final int REQUEST_PERMISSIONS_CODE = 128;

    public Context context;
    View emptyLayout;
    Button emptyButton;
    View floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_report_list);

        emptyLayout = findViewById(R.id.layout_empty);

        emptyButton = findViewById(R.id.action_add_report);

        //Check permissions Android 6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
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
                bundle.putInt(ReportConstants.BundleConstants.REPORT_ID, reportId);

                Intent intent = new Intent(MainActivity.this, ReportResume.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }

            // Create PDF
            @Override
            public void onOpenPdf(int reportId) {
                // Open ReportResume pass bundle for ID
                Bundle bundle = new Bundle();
                bundle.putInt(ReportConstants.BundleConstants.REPORT_ID, reportId);

                Intent intent = new Intent(MainActivity.this, ViewReportAsyncTask.class);
                intent.putExtras(bundle);
                startActivity(intent);
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