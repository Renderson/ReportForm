package com.rendersoncs.reportform.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.adapter.checkListAdapter.ReportRecyclerView;
import com.rendersoncs.reportform.constants.ReportConstants;

import java.util.Objects;

public class ReportNoteFragment extends DialogFragment {

    private ReportRecyclerView mAdapter;
    private EditText note;
    private String getNote;
    private int position;


    public ReportNoteFragment(ReportRecyclerView mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_report_note, null);

        note = view.findViewById(R.id.txt_note);

        if (getArguments() != null) {
            position = getArguments().getInt(ReportConstants.ITEM.POSITION);
            getNote = getArguments().getString(ReportConstants.ITEM.NOTE);
            note.setText(getNote);
        }

        String alertButton;
        if (getNote != null) {
            alertButton = getResources().getString(R.string.change);
        } else {
            alertButton = getResources().getString(R.string.insert);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                })
                .setPositiveButton(alertButton, (dialog, which) -> {

                    if (getNote == null) {
                        insertNewNote();
                    } else {
                        updateNote();
                    }
                });

        return builder.create();
    }

    private void insertNewNote() {
        String newNote = note.getText().toString();
        mAdapter.insertNote(position, newNote);
        Log.d("NoteFrag ", "InsertNote " + position + newNote);
    }

    private void updateNote() {
        if (getArguments() != null) {
            String updateNote = note.getText().toString();
            mAdapter.insertNote(position, updateNote);
            Log.d("NoteFrag ", "UpdateNote " + position + updateNote);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dismiss();
    }
}
