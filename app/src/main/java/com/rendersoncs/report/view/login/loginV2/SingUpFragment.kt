package com.rendersoncs.report.view.login.loginV2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentSignUpBinding
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.util.*
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SingUpFragment: BaseFragment<FragmentSignUpBinding, LoginViewModel>(), DatabaseReference.CompletionListener,
    TextView.OnEditorActionListener {
    override val viewModel: LoginViewModel by activityViewModels()

    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var user: User? = null

    private var mSelectedUri: Uri? = null

    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                openGallery.launch("image/*")
            else
                toast(getString(R.string.label_permission_camera_denied))
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFireBase()
        initViews()
        initUser()
    }

    private fun initFireBase() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()

        mAuthStateListener = FirebaseAuth.AuthStateListener { fireBaseAuth: FirebaseAuth ->
            val firebaseUser = fireBaseAuth.currentUser
            if (firebaseUser == null || user!!.id != null) {
                return@AuthStateListener
            }
            user!!.id = firebaseUser.uid
            user!!.saveDB(this@SingUpFragment)
        }
    }

    private fun initViews() = with(binding) {
        sigInName.afterTextChanged {
            validate(it, binding.textInputName, R.string.label_sign_insert_name)
        }

        sigInEmail.afterTextChanged {
            if (isValidateEmail(sigInEmail.text.toString())) {
                textInputEmail.error = null
            } else {
                textInputEmail.error = resources.getString(R.string.txt_email)
            }
        }

        sigInPassword.afterTextChanged {
            validate(it, binding.textInputPassword, R.string.label_insert_password)
        }

        sigInPassword.setOnEditorActionListener(this@SingUpFragment)

        btnSignUp.setOnClickListener {
            openProgressBar()
            saveUser()
        }

        btnSelectPhoto.setOnClickListener {
            checkPermission()
        }

        callLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUp_to_homeLogin
            )
        }
    }

    private fun validate(s: String, input: TextInputLayout, message: Int) {
        if (s.isNotEmpty()) {
            input.error = null
        } else {
            input.error = resources.getString(message)
        }
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

    private fun initUser() = with(binding) {
        user = User()
        user!!.name = sigInName.text.toString()
        user!!.email = sigInEmail.text.toString()
        user!!.password = sigInPassword.text.toString()
    }

    private fun saveUser() = with(binding) {
        when {
            sigInName.text.toString().isEmpty() -> {
                closeProgressBar()
                textInputName.error = getString(R.string.label_sign_insert_name)
            }
            sigInEmail.text.toString().isEmpty() -> {
                closeProgressBar()
                textInputEmail.error = getString(R.string.txt_email)
            }
            sigInPassword.text.toString().isEmpty() -> {
                closeProgressBar()
                textInputPassword.error = getString(R.string.label_insert_password)
            }
            mSelectedUri == null -> {
                closeProgressBar()
                showSnackBar(resources.getString(R.string.label_sign_insert_photo), signUpProgress)
            }
            else -> {
                initUser()
                mAuth!!.createUserWithEmailAndPassword(
                    user!!.email,
                    user!!.password
                ).addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        closeProgressBar()
                        savePhotoFireBase()
                    }
                }.addOnFailureListener(requireActivity()) { e: Exception ->
                    closeProgressBar()
                    FirebaseCrashlytics.getInstance().recordException(e)
                    showSnackBar(e.message, signUpProgress)
                }
            }
        }
    }

    private fun savePhotoFireBase() = with(binding) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        if (mSelectedUri == null) {
            showSnackBar(resources.getString(R.string.label_sign_insert_photo), signUpProgress)
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
                    e.message?.let { toast(it) }
                }
                .addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        }
    }

    private fun checkPermission() {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        } else {
            openGallery.launch("image/*")
        }
    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val bitmap: Bitmap
        mSelectedUri = uri
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, mSelectedUri)
            binding.imgPhoto.setImageDrawable(BitmapDrawable(this.resources, bitmap))
            binding.btnSelectPhoto.alpha = ALPHA.toFloat()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        mAuth!!.signOut()
        toast(getString(R.string.label_account_create))
        closeProgressBar()
        findNavController().navigateUp()
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(requireContext(), requireView())
            saveUser()
            return true
        }
        return false
    }

    private fun openProgressBar() = with(binding) {
        signUpProgress.show()
    }

    private fun closeProgressBar() = with(binding) {
        signUpProgress.hide()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignUpBinding.inflate(inflater, container, false)

    companion object {
        private const val ALPHA = 0
    }
}