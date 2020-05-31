package com.rendersoncs.reportform.view.activitys.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.activitys.login.util.User
import com.rendersoncs.reportform.view.services.util.closeVirtualKeyBoard
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_remove_user.*

class RemoveUserActivity : AppCompatActivity(), ValueEventListener,
        DatabaseReference.CompletionListener,
        TextView.OnEditorActionListener {

    private var user: User? = null
    //private var password: EditText? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_user)
        Fabric.with(this, Crashlytics())
        mAuth = FirebaseAuth.getInstance()
        password.setOnEditorActionListener(this)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        //password = findViewById(R.id.password)
        user = User()
        user!!.id = mAuth!!.currentUser!!.uid
        user!!.contextDataDB(this)
    }

    fun btnRemoveUser(view: View?) {
        this.removeUser()
    }

    private fun removeUser() {
        if (password!!.text.toString().isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.label_insert_password), Toast.LENGTH_SHORT).show()
        } else {
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
                        deleteUser()
                    }
                }.addOnFailureListener { e: Exception ->
                    Crashlytics.logException(e)
                    Toast.makeText(
                            this@RemoveUserActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    private fun deleteUser() {
        val firebaseUser = mAuth!!.currentUser ?: return
        firebaseUser.delete().addOnCompleteListener { task: Task<Void?> ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    user!!.removeDB(this@RemoveUserActivity)
                }
                .addOnFailureListener { e: Exception ->
                    Crashlytics.logException(e)
                    Toast.makeText(
                            this@RemoveUserActivity,
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
        Crashlytics.logException(fireBaseError.toException())
    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        if (databaseError != null) {
            Crashlytics.logException(databaseError.toException())
        }
        Toast.makeText(
                this@RemoveUserActivity,
                resources.getString(R.string.label_account_removed),
                Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    this,
                    view
            )
            this.removeUser()
            return true
        }
        return false
    }
}