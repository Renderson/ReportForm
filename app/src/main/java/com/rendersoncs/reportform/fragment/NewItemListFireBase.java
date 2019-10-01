package com.rendersoncs.reportform.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;

import java.util.HashMap;
import java.util.Map;

public class NewItemListFireBase extends DialogFragment {
    private EditText mTitleList;
    private EditText mDescriptionList;
    private String title;
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
        if (getArguments() != null) {
            alertButton = getResources().getString(R.string.change);
        } else {
            alertButton = getResources().getString(R.string.insert);
        }

        initFirebase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton(getResources().getString(R.string.txt_cancel), (dialog, which) -> {
                    Toast.makeText(getActivity(), getResources().getString(R.string.txt_cancel), Toast.LENGTH_SHORT).show();
                    NewItemListFireBase.this.getDialog().cancel();
                })

                .setPositiveButton(alertButton, (dialog, which) -> {

                    if (getArguments() != null) {
                        updateItemList();
                    } else {
                        insertNewItemList();
                    }
                });

        return builder.create();
    }

    private void checkItems() {
        if (getArguments() != null) {
            title = getArguments().getString("title");
            mTitleList.setText(title);

            String description = getArguments().getString("desc");
            mDescriptionList.setText(description);
        }

    }

    private void updateItemList() {
        String upTitle = mTitleList.getText().toString();
        String upDescription = mDescriptionList.getText().toString();

        DatabaseReference databaseReference = LibraryClass.getFirebase().child("users").child(user.getId()).child("list");
        Query query = databaseReference.orderByChild("title").equalTo(title);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().child("title").setValue(upTitle);
                    ds.getRef().child("description").setValue(upDescription);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), getResources().getString(R.string.label_error_update_list), Toast.LENGTH_SHORT).show();
            }
        });
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
