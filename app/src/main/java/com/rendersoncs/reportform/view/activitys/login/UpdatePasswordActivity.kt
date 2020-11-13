package com.rendersoncs.reportform.view.activitys.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.activitys.login.util.User
import com.rendersoncs.reportform.view.services.util.closeVirtualKeyBoard
import kotlinx.android.synthetic.main.activity_update_password.*

class UpdatePasswordActivity : AppCompatActivity(), ValueEventListener, TextView.OnEditorActionListener {
    private var user: User? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        password.setOnEditorActionListener(this)
    }

    private fun init() {
        user = User()
        user!!.id = mAuth!!.currentUser!!.uid
        user!!.contextDataDB(this)
    }

    fun btnUpdate(view: View?) {
        this.update()
    }

    private fun update() {
        if (newPassword!!.text.toString().isEmpty() || password!!.text.toString().isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.label_insert_password), Toast.LENGTH_SHORT).show()
        } else {
            user!!.newPassword = newPassword!!.text.toString()
            user!!.password = password!!.text.toString()
            reAuthenticate()
        }
    }

    private fun reAuthenticate() {
        val firebaseUser = mAuth!!.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(
                user!!.email,
                user!!.password
        )
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        updateData()
                    }
                }
                .addOnFailureListener { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Toast.makeText(
                            this@UpdatePasswordActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    private fun updateData() {
        user!!.newPassword = newPassword!!.text.toString()
        user!!.password = password!!.text.toString()
        val firebaseUser = mAuth!!.currentUser ?: return
        firebaseUser
                .updatePassword(user!!.newPassword)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        newPassword!!.setText("")
                        password!!.setText("")
                        Toast.makeText(
                                this@UpdatePasswordActivity,
                                resources.getString(R.string.label_password_update),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener(this) { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Toast.makeText(
                            this@UpdatePasswordActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val u = dataSnapshot.getValue(User::class.java)
        if (u != null) {
            user!!.email = u.email
        }
    }

    override fun onCancelled(fireBaseError: DatabaseError) {
        FirebaseCrashlytics.getInstance().recordException(fireBaseError.toException())
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    this,
                    view
            )
            this.update()
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        this.init()
    }
}