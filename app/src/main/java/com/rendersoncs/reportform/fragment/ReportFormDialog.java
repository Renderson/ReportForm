package com.rendersoncs.reportform.fragment;

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
import android.widget.Toast;

import com.rendersoncs.reportform.view.ReportActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.rendersoncs.reportform.R;

public class ReportFormDialog extends DialogFragment {

    private Calendar mCalendar;
    private DatePickerDialog mDatePickerDialog;
    private TextView dateTv;
    private TextInputEditText companyTv;
    private TextInputEditText emailTv;

    private int mDay, mMonth, mYear;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog,null);

        companyTv = view.findViewById(R.id.company_id);
        emailTv = view.findViewById(R.id.email_id);
        dateTv = view.findViewById(R.id.date_Id);

        companyTv.addTextChangedListener(validateTextWatcher);
        emailTv.addTextChangedListener(validateTextWatcher);

        // Get date current 19/2/2018
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date));
        String dateString = sdf.format(date);
        dateTv.setText(dateString);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)

                .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
                        ReportFormDialog.this.getDialog().cancel();
                    }
                })

                .setPositiveButton(R.string.txt_start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ReportActivity.class);

                        /// Passando valores digitados para ReportActivity
                        String company = "";
                        String email = "";
                        String date = "";

                        company = companyTv.getText().toString();
                        email = emailTv.getText().toString();
                        date = dateTv.getText().toString();

                        Bundle bundle = new Bundle();
                        bundle.putString("company", company);
                        bundle.putString("email", email);
                        bundle.putString("date", date);
                        intent.putExtras(bundle);

                        startActivity(intent);

                        Toast.makeText(getActivity(), R.string.txt_new_report, Toast.LENGTH_SHORT).show();


                    }
                });

        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCalendar = Calendar.getInstance();
                mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                mMonth = mCalendar.get(Calendar.MONTH);
                mYear = mCalendar.get(Calendar.YEAR);

                mDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        dateTv.setText(day + getString(R.string.space_date) + (month + 1) + getString(R.string.space_date) + year);

                    }
                }, mYear, mMonth, mDay);
                //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                mDatePickerDialog.show();
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

            String companyValidate = companyTv.getText().toString().trim();

            AlertDialog dialog = (AlertDialog) getDialog();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!companyValidate.isEmpty() && isValidateEmail());
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

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Toast.makeText(getActivity(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
    }
}
