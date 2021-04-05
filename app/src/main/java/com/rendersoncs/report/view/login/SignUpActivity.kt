package com.rendersoncs.report.view.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ActivitySignUpBinding
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import java.io.IOException
import java.util.*

class SignUpActivity : CommonActivity(), DatabaseReference.CompletionListener, OnEditorActionListener {
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: AuthStateListener? = null
    private var user: User? = null

    private var mSelectedUri: Uri? = null
    private var mImgPhoto: ImageView? = null

    private lateinit var binding : ActivitySignUpBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()

        mAuthStateListener = AuthStateListener { fireBaseAuth: FirebaseAuth ->
            val firebaseUser = fireBaseAuth.currentUser
            if (firebaseUser == null || user!!.id != null) {
                return@AuthStateListener
            }
            user!!.id = firebaseUser.uid
            user!!.saveDB(this@SignUpActivity)
        }

        mImgPhoto = findViewById(R.id.img_photo)
        binding.btnSelectPhoto.setOnClickListener { selectPhoto() }
        initViews()
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            mAuth!!.removeAuthStateListener(mAuthStateListener!!)
        }
    }

    override fun initViews() {
        progressBar = findViewById(R.id.sign_up_progress)

        binding.sigInName.addTextChangedListener(nameTextWatcher)
        binding.sigInEmail.addTextChangedListener(emailTextWatcher)
        binding.sigInPassword.addTextChangedListener(passwordTextWatcher)

        binding.sigInPassword.setOnEditorActionListener(this)
    }

    private var nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validate(s, binding.textInputName, R.string.label_sign_insert_name)
        }
    }

    private var emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validate(s, binding.textInputEmail, R.string.txt_email)
        }
    }

    private var passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validate(s, binding.textInputPassword, R.string.label_insert_password)
        }
    }

    override fun initUser() {
        user = User()
        user!!.name = binding.sigInName.text.toString()
        user!!.email = binding.sigInEmail.text.toString()
        user!!.password = binding.sigInPassword.text.toString()
    }

    fun sendSignUpData(view: View?) {
        openProgressBar()
        initUser()
        saveUser()
    }

    fun callLogin(view: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUser() {
        when {
            binding.sigInName.text.toString().isEmpty() -> {
                closeProgressBar()
                binding.textInputName.error = getString(R.string.label_sign_insert_name)
            }
            binding.sigInEmail.text.toString().isEmpty() -> {
                closeProgressBar()
                binding.textInputEmail.error = getString(R.string.txt_email)
            }
            binding.sigInPassword.text.toString().isEmpty() -> {
                closeProgressBar()
                binding.textInputPassword.error = getString(R.string.label_insert_password)
            }
            mSelectedUri == null -> {
                closeProgressBar()
                showSnackBar(resources.getString(R.string.label_sign_insert_photo))
            }
            else -> {
                mAuth!!.createUserWithEmailAndPassword(
                        user!!.email,
                        user!!.password
                ).addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        closeProgressBar()
                        savePhotoFireBase()
                    }
                }.addOnFailureListener(this) { e: Exception ->
                    closeProgressBar()
                    FirebaseCrashlytics.getInstance().recordException(e)
                    showSnackBar(e.message)
                }
            }
        }
    }

    private fun savePhotoFireBase() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        if (mSelectedUri == null) {
            showSnackBar(resources.getString(R.string.label_sign_insert_photo))
        } else {
            ref.putFile(mSelectedUri!!)
                    .addOnSuccessListener {
                        ref.downloadUrl
                                .addOnSuccessListener { uri: Uri ->
                                    val photoUri = uri.toString()
                                    Log.i("log", "Item: $photoUri profileUrl")
                                    val ref1 = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS)
                                            .child(user!!.id).child(ReportConstants.FIREBASE.FIRE_PHOTO)
                                    ref1.setValue(photoUri).addOnSuccessListener { }
                                }
                    }.addOnFailureListener { e: Exception ->
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Toast.makeText(this@SignUpActivity, "" + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        }
    }

    private fun selectPhoto() {
        val it = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(it, ReportConstants.PHOTO.REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_GALLERY) {
            if (data != null) {
                mSelectedUri = data.data
                val bitmap: Bitmap
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mSelectedUri)
                    mImgPhoto!!.setImageDrawable(BitmapDrawable(this.resources, bitmap))
                    binding.btnSelectPhoto.alpha = ALPHA.toFloat()
                } catch (e: IOException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    showSnackBar(e.message)
                }
            }
        }
    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        mAuth!!.signOut()
        showToast(resources.getString(R.string.label_account_create))
        closeProgressBar()
        finish()
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(this, view!!)
            sendSignUpData(view)
            return true
        }
        return false
    }

    companion object {
        private const val ALPHA = 0
    }
}