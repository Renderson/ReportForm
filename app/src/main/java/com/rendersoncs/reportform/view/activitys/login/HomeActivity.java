package com.rendersoncs.reportform.view.activitys.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rendersoncs.reportform.R;

public class HomeActivity extends AppCompatActivity {
    /*private AlertDialogUtil alertDialog = new AlertDialogUtil();*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // For Version TEST
       /* this.alertDialog.showDialog(HomeActivity.this,
                "Versão de Teste!",
                "Olá, você está usando uma versão de teste, com isso algumas coisas podem sair errado, tenha paciência e passe para nós o seu feed back.",
                "ok",
                (dialogInterface, i) -> { },
                null, null, false);*/
    }

    public void callLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void callSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
