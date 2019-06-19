package com.rendersoncs.reportform.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rendersoncs.reportform.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewItemListFireBase extends DialogFragment {
    private EditText mTitleList;
    private EditText mTextList;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_new_list_firebase, null);

        mTitleList = view.findViewById(R.id.txt_title_list);
        mTextList = view.findViewById(R.id.txt_text_list);

        mTitleList.addTextChangedListener(validateTextWatcher);
        mTextList.addTextChangedListener(validateTextWatcher);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
                        NewItemListFireBase.this.getDialog().cancel();
                    }
                })

                .setPositiveButton("Inserir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mTitle = mTitleList.getText().toString();
                        String mText = mTextList.getText().toString();

                        final String key = FirebaseDatabase.getInstance().getReference().child("Data").push().getKey();
                        HashMap<String, String> dataMap = new HashMap<String, String>();
                        Map<String, Object> childUpdates = new HashMap<>();
                        dataMap.put("title", mTitle);
                        dataMap.put("text", mText);
                        childUpdates.put("/Data/list/" + key, dataMap);
                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                    }
                });

        return builder.create();
    }

    // validate text for free button
    private TextWatcher validateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String titleValidate = mTitleList.getText().toString().trim();
            String textValidate = mTextList.getText().toString().trim();

            AlertDialog dialog = (AlertDialog) getDialog();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!titleValidate.isEmpty() && !textValidate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Toast.makeText(getActivity(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
    }
}
