package com.rendersoncs.reportform.view.activitys.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rendersoncs.reportform.R

class HomeActivity : AppCompatActivity() {
    /*private AlertDialogUtil alertDialog = new AlertDialogUtil();*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // For Version TEST
        /* this.alertDialog.showDialog(HomeActivity.this,
                "Versão de Teste!",
                "Olá, você está usando uma versão de teste, com isso algumas coisas podem sair errado, tenha paciência e passe para nós o seu feed back.",
                "ok",
                (dialogInterface, i) -> { },
                null, null, false);*/
    }

    fun callLogin(view: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun callSignUp(view: View?) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}