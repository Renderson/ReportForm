package com.rendersoncs.reportform.view.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.activitys.ReportActivity
import com.rendersoncs.reportform.view.services.constants.ReportConstants
import java.text.SimpleDateFormat
import java.util.*

class NewReportFragment : DialogFragment() {
    private var dateTv: TextView? = null
    private var companyTv: TextInputEditText? = null
    private var emailTv: TextInputEditText? = null
    private var controllerTv: TextInputEditText? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_new_report, null)

        this.init(view)
        this.checkArguments()
        this.configFormatDate()

        val builder = alertDialog(view)

        this.selectedDate()

        return builder.create()
    }

    private fun init(view: View) {
        companyTv = view.findViewById(R.id.company_id)
        emailTv = view.findViewById(R.id.email_id)
        controllerTv = view.findViewById(R.id.controller_id)
        dateTv = view.findViewById(R.id.date_Id)

        companyTv!!.addTextChangedListener(validateTextWatcher)
        emailTv!!.addTextChangedListener(validateTextWatcher)
    }

    private fun alertDialog(view: View?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
                .setNegativeButton(R.string.cancel) { _, _ ->
                    //Toast.makeText(getContext(), R.string.txt_canceled, Toast.LENGTH_SHORT).show();
                    dismiss()
                }
                .setPositiveButton(R.string.start) { _, _ ->
                    this.sendValues()
                }
        return builder
    }

    private fun configFormatDate() {
        // Get date current 19/2/2018
        val date = System.currentTimeMillis()
        val sdf = SimpleDateFormat(getString(R.string.date), Locale("pt", "br"))
        val dateString = sdf.format(date)
        dateTv!!.text = dateString
    }

    private fun checkArguments() {
        if (arguments == null) {
            controllerTv!!.setText("")
        } else {
            val controllerBundle = requireArguments().getString(ReportConstants.ITEM.CONTROLLER)
            controllerTv!!.setText(controllerBundle)
            controllerTv!!.addTextChangedListener(validateTextWatcher)
        }
    }

    private fun selectedDate() {
        dateTv!!.setOnClickListener {
            val mCalendar = Calendar.getInstance()
            val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
            val mMonth = mCalendar.get(Calendar.MONTH)
            val mYear = mCalendar.get(Calendar.YEAR)
            val space = getString(R.string.space_date)

            val mDatePickerDialog = DatePickerDialog(requireActivity(),
                    OnDateSetListener { _, year, month, day ->
                dateTv!!.text = ("" + day + space
                        + (month + 1) + space
                        + year)
            }, mYear, mMonth, mDay)
            //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mDatePickerDialog.show()
        }
    }

    private fun sendValues() {
        val intent = Intent(activity, ReportActivity::class.java)

        // Passing typed values to ReportActivity
        val company: String = companyTv!!.text.toString()
        val email = emailTv!!.text.toString()
        val controller = controllerTv!!.text.toString()
        val date1 = dateTv!!.text.toString()

        val bundle = Bundle()
        bundle.putString(ReportConstants.ITEM.COMPANY, company)
        bundle.putString(ReportConstants.ITEM.EMAIL, email)
        bundle.putString(ReportConstants.ITEM.CONTROLLER, controller)
        bundle.putString(ReportConstants.ITEM.DATE, date1)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    // validate text for free button
    private val validateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val companyValidate = companyTv!!.text.toString().trim { it <= ' ' }
            val controllerValidate = controllerTv!!.text.toString().trim { it <= ' ' }
            val dialog = dialog as AlertDialog?
            dialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = companyValidate.isNotEmpty() &&
                    isValidateEmail &&
                    controllerValidate.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val isValidateEmail: Boolean
        get() {
            var validEmail = true
            val email = emailTv!!.text.toString().trim { it <= ' ' }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailTv!!.error = getString(R.string.txt_email)
                validEmail = false
            } else {
                emailTv!!.error = null
            }
            return validEmail
        }

    override fun onResume() {
        super.onResume()
        val dialog = dialog as AlertDialog?
        dialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    override fun onDetach() {
        super.onDetach()
        dismiss()
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }
}