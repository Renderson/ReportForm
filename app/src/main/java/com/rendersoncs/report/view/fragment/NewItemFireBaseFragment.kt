package com.rendersoncs.report.view.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.model.NewItemFireBase
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User
import java.util.*

class NewItemFireBaseFragment : DialogFragment() {
    private var mTitleList: EditText? = null
    private var mDescriptionList: EditText? = null
    private var key: String? = null
    private var user: User? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_new_list_firebase, null)

        mTitleList = view.findViewById(R.id.txt_title_list)
        mDescriptionList = view.findViewById(R.id.txt_description_list)
        mTitleList!!.addTextChangedListener(validateTextWatcher)
        mDescriptionList!!.addTextChangedListener(validateTextWatcher)

        val alertButton: String = changeTextButtonDialog()

        this.initFireBase()

        return showAlertDialog(builder, view, alertButton)
    }

    private fun showAlertDialog(builder: AlertDialog.Builder, view: View?, alertButton: String): AlertDialog {
        builder.setView(view)
                .setNegativeButton(resources.getString(R.string.cancel)) { _: DialogInterface?, _: Int ->
                    dismiss() }
                .setPositiveButton(alertButton) { _: DialogInterface?, _: Int ->
                    if (arguments != null) {
                        updateItemList()
                    } else {
                        insertNewItemList()
                    }
                }
        return builder.create()
    }

    private fun changeTextButtonDialog(): String {
        return if (arguments != null) {
            resources.getString(R.string.change)
        } else {
            resources.getString(R.string.insert)
        }
    }

    private fun checkItems() {
        if (arguments != null) {
            key = requireArguments().getString(ReportConstants.ITEM.KEY)
            val title = requireArguments().getString(ReportConstants.ITEM.TITLE)
            mTitleList!!.setText(title)
            val description = requireArguments().getString(ReportConstants.ITEM.DESCRIPTION)
            mDescriptionList!!.setText(description)
        }
    }

    private fun updateItemList() {
        val upTitle = mTitleList!!.text.toString()
        val upDescription = mDescriptionList!!.text.toString()
        val databaseReference = user!!.id?.let { id ->
            LibraryClass.getFirebase()
                    ?.child(ReportConstants
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
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, resources.getString(R.string.label_error_update_list), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun insertNewItemList() {
        val mTitle = mTitleList!!.text.toString()
        val mDescription = mDescriptionList!!.text.toString()
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
    }

    private fun initFireBase() {
        val mAuth = FirebaseAuth.getInstance()
        user = User()
        user!!.id = mAuth.currentUser!!.uid
    }

    // validate text for free button
    private val validateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val titleValidate = mTitleList!!.text.toString().trim { it <= ' ' }
            val descriptionValidate = mDescriptionList!!.text.toString().trim { it <= ' ' }
            val dialog = (dialog as AlertDialog?)!!
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = titleValidate.isNotEmpty() && descriptionValidate.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onResume() {
        super.onResume()
        checkItems()
        val dialog = (dialog as AlertDialog?)!!
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    override fun onDetach() {
        super.onDetach()
        dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        //Toast.makeText(getActivity(), R.string.canceled, Toast.LENGTH_SHORT).show();
    }
}