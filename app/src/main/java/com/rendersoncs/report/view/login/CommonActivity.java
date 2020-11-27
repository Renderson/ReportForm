package com.rendersoncs.report.view.login;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.rendersoncs.report.infrastructure.util.SnackBarHelper;

abstract public class CommonActivity extends AppCompatActivity {

    protected EditText email;
    protected EditText password;
    protected ProgressBar progressBar;

    protected void showSnackBar(String message){
        Snackbar snackbar = Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        SnackBarHelper.configSnackBar(CommonActivity.this, snackbar);
        snackbar.show();
    }

    protected void showToast(String message){
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );

    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initUser();
}
