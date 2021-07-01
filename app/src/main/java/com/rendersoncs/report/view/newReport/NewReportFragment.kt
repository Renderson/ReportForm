package com.rendersoncs.report.view.newReport

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentNewReportBinding
import com.rendersoncs.report.infrastructure.util.transformIntoDatePicker
import com.rendersoncs.report.model.ReportNew
import com.rendersoncs.report.view.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NewReportFragment :
    BaseFragment<FragmentNewReportBinding, ReportViewModel>() {
    override val viewModel: ReportViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewReportBinding.inflate(inflater, container, false)

    private fun initViews() {
        with(binding) {
            addNewReport.emailId.addTextChangedListener(validateTextWatcher)
            addNewReport.dateId.transformIntoDatePicker(
                requireContext(),
                "dd/MM/yyyy",
                Date()
            )

            btnNewReport.setOnClickListener {
                binding.addNewReport.apply {
                    val (company, email, date, controller) = getReportContent()

                    when {
                        company.isEmpty() -> {
                            this.companyId.error = "Company must note be empty"
                        }
                        email.isEmpty() && !isValidateEmail -> {
                            this.emailId.error = getString(R.string.txt_email)
                        }
                        date.isEmpty() -> {
                            this.dateId.error = "Date must not be empty"
                        }
                        controller.isEmpty() -> {
                            this.controllerId.error = "Controller must not be empty"
                        }
                        else -> {
                            val reportNew = ReportNew(
                                company,
                                email,
                                date,
                                controller
                            )
                            val bundle = Bundle().apply {
                                putSerializable("reportNew", reportNew)
                            }
                            findNavController().navigate(
                                R.id.action_addNewReportFragment_to_reportActivity, bundle
                            )
                        }
                    }
                }
            }
        }
    }

    // validate text for free button
    private val validateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding) {
                btnNewReport.isEnabled = isValidateEmail
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val isValidateEmail: Boolean
        get() = with(binding) {
            var validEmail = true
            val email = addNewReport.emailId.text.toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validEmail = false
                addNewReport.emailId.error = getString(R.string.txt_email)
            } else {
                addNewReport.emailId.error = null
            }
            return validEmail
        }

    private fun getReportContent(): ReportNew = binding.addNewReport.let {
        val company = it.companyId.text.toString()
        val email = it.emailId.text.toString()
        val date = it.dateId.text.toString()
        val controller = it.controllerId.text.toString()

        return ReportNew(company, email, date, controller)
    }
}