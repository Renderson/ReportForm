package com.rendersoncs.reportform.view.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.view.activitys.ReportActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.rendersoncs.reportform.R;

public class ReportFormDialog extends DialogFragment {

    private Calendar mCalendar;
    private DatePickerDialog mDatePickerDialog;
    private TextView dateTv;
    private TextInputEditText companyTv;
    private TextInputEditText emailTv;
    private TextInputEditText controllerTv;

    private int mDay, mMonth, mYear;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog, null);

        companyTv = view.findViewById(R.id.company_id);
        emailTv = view.findViewById(R.id.email_id);
        controllerTv = view.findViewById(R.id.controller_id);
        dateTv = view.findViewById(R.id.date_Id);

        companyTv.addTextChangedListener(validateTextWatcher);
        emailTv.addTextChangedListener(validateTextWatcher);

        if (getArguments() == null){
            controllerTv.setText("");
        } else {
            String controllerBundle = getArguments().getString(ReportConstants.ITEM.CONTROLLER);
            controllerTv.setText(controllerBundle);
            controllerTv.addTextChangedListener(validateTextWatcher);
        }

        // Get date current 19/2/2018
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date), new Locale("pt", "br"));
        String dateString = sdf.format(date);
        dateTv.setText(dateString);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)

                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    //Toast.makeText(getContext(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
                    dismiss();
                })

                .setPositiveButton(R.string.start, (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), ReportActivity.class);

                    // Passing typed values to ReportActivity
                    String company = "";
                    String email = "";
                    String date1 = "";
                    String controller = "";

                    company = companyTv.getText().toString();
                    email = emailTv.getText().toString();
                    controller = controllerTv.getText().toString();
                    date1 = dateTv.getText().toString();

                    Bundle bundle = new Bundle();
                    bundle.putString(ReportConstants.ITEM.COMPANY, company);
                    bundle.putString(ReportConstants.ITEM.EMAIL, email);
                    bundle.putString(ReportConstants.ITEM.CONTROLLER, controller);
                    bundle.putString(ReportConstants.ITEM.DATE, date1);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    //Toast.makeText(getContext(), R.string.txt_new_report, Toast.LENGTH_SHORT).show();
                });

        dateTv.setOnClickListener(v -> {

            mCalendar = Calendar.getInstance();
            mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            mMonth = mCalendar.get(Calendar.MONTH);
            mYear = mCalendar.get(Calendar.YEAR);

            mDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view1, int year, int month, int day) {

                    dateTv.setText(day + getString(R.string.space_date) + (month + 1) + getString(R.string.space_date) + year);

                }
            }, mYear, mMonth, mDay);
            //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mDatePickerDialog.show();
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

            String companyValidate = companyTv.getText().toString().trim();
            String controllerValidate = controllerTv.getText().toString().trim();

            AlertDialog dialog = (AlertDialog) getDialog();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!companyValidate.isEmpty() &&
                    isValidateEmail() &&
                    !controllerValidate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private boolean isValidateEmail() {

        boolean validEmail = true;
        String email = emailTv.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTv.setError(getString(R.string.txt_email));
            validEmail = false;
        } else {
            emailTv.setError(null);
        }
        return validEmail;
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //Toast.makeText(getContext(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
    }
}
