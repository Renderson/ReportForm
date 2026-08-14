package com.rendersoncs.report.view.login;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.rendersoncs.report.common.util.SnackBarHelper;

abstract public class CommonActivity extends AppCompatActivity {

    protected EditText email;
    protected EditText password;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
    }

    protected void applySystemBarInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

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

    protected void validate(Editable s, TextInputLayout input, int message) {
        if (s != null && !s.toString().isEmpty()) {
            input.setError(null);
        } else {
            input.setError(getResources().getString(message));
        }
    }

    abstract protected void initViews();

    abstract protected void initUser();
}
