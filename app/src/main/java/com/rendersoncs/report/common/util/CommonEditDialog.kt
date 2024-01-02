package com.rendersoncs.report.common.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.databinding.CustomEditTextDialogBinding
import com.rendersoncs.report.model.NewItemFireBase
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import java.util.HashMap

class CommonEditDialog(private val context: Context) {

    private val binding: CustomEditTextDialogBinding = CustomEditTextDialogBinding.inflate(
        LayoutInflater.from(context))
    private val dialog = Dialog(context).apply {
        setupCommonEditDialog(binding)
    }

    private var key: String? = null
    private var user: User? = null

    fun showEditTextDialog(
        title: String = "",
        description: String = "",
        key: String = "",
        msg: () -> Unit,
        error: (() -> Unit)? = null,
    ) {

        this.initFireBase()

        binding.confirm.text = if (title != "") context.getString(R.string.change) else context.getString(R.string.insert)

        with(binding) {
            txtTitleList.onTextChanged {
                val titleValidate = binding.txtTitleList.text.toString().trim { it <= ' ' }
                val descriptionValidate = binding.txtDescriptionList.text.toString().trim { it <= ' ' }
                binding.confirm.isEnabled = titleValidate.isNotEmpty() && descriptionValidate.isNotEmpty()
            }
            txtDescriptionList.onTextChanged {
                val titleValidate = binding.txtTitleList.text.toString().trim { it <= ' ' }
                val descriptionValidate = binding.txtDescriptionList.text.toString().trim { it <= ' ' }
                binding.confirm.isEnabled = titleValidate.isNotEmpty() && descriptionValidate.isNotEmpty()
            }
            confirm.setOnClickListener {
                if (title != "") {
                    updateItemList(msg, error)
                } else {
                    insertNewItemList()
                }
            }
            cancel.setOnClickListener { dialog.dismiss() }
        }
        checkItems(title, description, key)
        avoidException()
    }

    private fun initFireBase() {
        val mAuth = FirebaseAuth.getInstance()
        user = User()
        user!!.id = mAuth.currentUser!!.uid
    }

    private fun updateItemList(msg: () -> Unit, error: (() -> Unit)?) {
        val upTitle = binding.txtTitleList.text.toString()
        val upDescription = binding.txtDescriptionList.text.toString()
        val databaseReference = user?.id?.let { id ->
            LibraryClass.getFirebase()
                ?.child(
                    ReportConstants
                    .FIREBASE.FIRE_USERS)
                ?.child(id)
                ?.child(ReportConstants.FIREBASE.FIRE_LIST) }
        val query = databaseReference?.orderByChild(ReportConstants.ITEM.KEY)?.equalTo(key)

        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    ds.ref.child(ReportConstants.ITEM.TITLE).setValue(upTitle)
                    ds.ref.child(ReportConstants.ITEM.DESCRIPTION).setValue(upDescription)
                }
                dialog.dismiss()
                msg.invoke()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                error?.invoke()
                dialog.dismiss()
                //Toast.makeText(activity, resources.getString(R.string.label_error_update_list), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun insertNewItemList() {
        val mTitle = binding.txtTitleList.text.toString()
        val mDescription = binding.txtDescriptionList.text.toString()
        val key = user!!.id?.let { id ->
            FirebaseDatabase.getInstance()
                .reference.child(ReportConstants.FIREBASE.FIRE_USERS)
                .child(id).child(ReportConstants.FIREBASE.FIRE_LIST).push().key
        }
        val childUpdates: MutableMap<String, NewItemFireBase> = HashMap()

        val newItem = NewItemFireBase(
            title = mTitle,
            description = mDescription,
            key = key!!
        )

        childUpdates["/" + ReportConstants.FIREBASE.FIRE_USERS + "/" + user!!.id + "/"
                + ReportConstants.FIREBASE.FIRE_LIST + "/" + key] = newItem
        FirebaseDatabase.getInstance().reference.updateChildren(childUpdates as Map<String, Any>)
        dialog.dismiss()
    }

    private fun checkItems(title: String, description: String, key: String) {
        this.key = key
        binding.txtTitleList.setText(title)
        binding.txtDescriptionList.setText(description)
    }

    private fun avoidException() {
        runCatching {
            if (dialog.isShowing.not()) {
                dialog.show()
            }
        }
    }
}

fun Dialog.setupCommonEditDialog(binding: CustomEditTextDialogBinding) {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.setCancelable(false)
    this.setContentView(binding.root)
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}