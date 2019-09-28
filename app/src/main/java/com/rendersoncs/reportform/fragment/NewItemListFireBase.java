package com.rendersoncs.reportform.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;

import java.util.HashMap;
import java.util.Map;

public class NewItemListFireBase extends DialogFragment {
    private EditText mTitleList;
    private EditText mDescriptionList;
    private FirebaseAuth mAuth;
    private User user;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_new_list_firebase, null);

        mTitleList = view.findViewById(R.id.txt_title_list);
        mDescriptionList = view.findViewById(R.id.txt_description_list);

        mTitleList.addTextChangedListener(validateTextWatcher);
        mDescriptionList.addTextChangedListener(validateTextWatcher);

        String alertButton;
        if (getArguments() != null){
            alertButton = "Alterar";
        } else {
            alertButton = "Inserir";
        }

        initFirebase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
                        NewItemListFireBase.this.getDialog().cancel();
                    }
                })

                .setPositiveButton(alertButton, (dialog, which) -> {

                    if (getArguments() != null){
                        updateItemList();
                    } else {
                        insertNewItemList();
                    }
                });

        return builder.create();
    }

    private void checkItems(){
        if (getArguments() != null) {
            String title = getArguments().getString("title");
            Log.d("TestFrag2", title);
            mTitleList.setText(title);

            String description = getArguments().getString("desc");
            mDescriptionList.setText(description);
        }

    }

    private void updateItemList(){
        String upTitle = mTitleList.getText().toString();
        String upDescription = mDescriptionList.getText().toString();


//        DatabaseReference databaseReference = LibraryClass.getFirebase().child("users").child(user.getId()).child("list");
//        databaseReference.orderByChild()

    }

    private void insertNewItemList() {
        String mTitle = mTitleList.getText().toString();
        String mDescription = mDescriptionList.getText().toString();

        final String key = FirebaseDatabase.getInstance().getReference().child("users").child(user.getId()).child("list").push().getKey();
        HashMap<String, String> dataMap = new HashMap<>();
        Map<String, Object> childUpdates = new HashMap<>();
        dataMap.put("title", mTitle);
        dataMap.put("description", mDescription);
        childUpdates.put("/users/" + user.getId() + "/list/" + key, dataMap);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = new User();
        user.setId(mAuth.getCurrentUser().getUid());
    }

    // validate text for free button
    private TextWatcher validateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String titleValidate = mTitleList.getText().toString().trim();
            String descriptionValidate = mDescriptionList.getText().toString().trim();

            AlertDialog dialog = (AlertDialog) getDialog();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!titleValidate.isEmpty() && !descriptionValidate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        checkItems();
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dismiss();
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Toast.makeText(getActivity(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
    }
}
