package com.rendersoncs.report.infrastructure.util

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import androidx.databinding.ViewStubProxy
import com.google.android.material.textfield.TextInputEditText
import com.rendersoncs.report.R
import java.text.SimpleDateFormat
import java.util.*

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun TextInputEditText.transformIntoDatePicker(
        context: Context,
        format: String,
        maxDate: Date? = null
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

    setOnClickListener {
        DatePickerDialog(
                context,
                datePickerOnDataSetListener,
                myCalendar
                        .get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = afterTextChanged(s.toString())
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = onTextChanged(s.toString())
    })
}

fun Button.enable() {
    this.isEnabled = true
    this.background.alpha = 255
}

fun Button.disable() {
    this.isEnabled = false
    this.background.alpha = 128
}

fun isValidateEmail(s: String): Boolean {
    var validEmail = true
    val email = s.trim { it <= ' ' }
    validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return validEmail
}