package com.rendersoncs.report.view.newReport

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentNewReportBinding
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.util.*
import com.rendersoncs.report.model.ReportNew
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewReportFragment :
    BaseFragment<FragmentNewReportBinding, ReportViewModel>() {

    override val viewModel: ReportViewModel by activityViewModels()
    private val args: NewReportFragmentArgs by navArgs()

    private lateinit var pref: SharedPreferences
    private var uiStateJobName: Job? = null
    private val date = Calendar.getInstance().time
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var company: Boolean = false
    private var email: Boolean = false
    private var controller: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.reportEdit != -1) {
            viewModel.getReportByID(args.reportEdit)
        }

        pref = requireActivity().getSharedPreferences(
            ReportConstants.FIREBASE.FIRE_USERS,
            MODE_PRIVATE
        )
        viewModel.getNameShared(pref)

        setObservers()
        initViews()
        setListener()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewReportBinding.inflate(inflater, container, false)

    private fun setObservers() {
        viewModel.detailState.observe(viewLifecycleOwner) { detailState ->
            when (detailState) {
                DetailState.Loading -> {}
                is DetailState.Success -> {
                    binding.addNewReport.companyId.setText(detailState.report.company)
                    binding.addNewReport.emailId.setText(detailState.report.email)
                }
                is DetailState.Error -> {
                    toast("Error")
                }
                DetailState.Empty -> {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun initViews() {
        with(binding.addNewReport) {

            dateId.transformIntoDatePicker(
                requireContext(),
                "dd/MM/yyyy",
                Date()
            )
            dateId.setText(dateTimeFormat.format(date))

            uiStateJobName = lifecycleScope.launchWhenStarted {
                viewModel.name.collect {
                    controllerId.setText(it)
                }
            }

            companyId.afterTextChanged {
                if (it.isEmpty()) {
                    company = false
                    enableButton()
                    textInputCompany.error = "Company must note be empty"
                } else {
                    company = true
                    enableButton()
                    textInputCompany.error = null
                }
            }

            emailId.onTextChanged {
                if (isValidateEmail(emailId.text.toString())) {
                    email = true
                    textInputEmail.error = null
                    enableButton()
                } else {
                    email = false
                    textInputEmail.error = getString(R.string.txt_email)
                    enableButton()
                }
            }

            controllerId.afterTextChanged {
                if (it.isEmpty()) {
                    controller = false
                    enableButton()
                    textInputController.error = "Controller must not be empty"
                } else {
                    controller = true
                    enableButton()
                    textInputController.error = null
                }
            }
        }
    }

    private fun enableButton() = with(binding) {
        if (company && controller && email) {
            btnNewReport.enable()
        } else {
            btnNewReport.disable()
        }
    }

    private fun setListener() = with(binding) {
        btnNewReport.setOnClickListener {
            hideKeyboard()
            binding.addNewReport.apply {
                val (id, company, email, date, controller) = getReportContent()
                val reportNew = ReportNew(
                    id,
                    company,
                    email,
                    date,
                    controller
                )
                val bundle = Bundle().apply {
                    putSerializable("report", reportNew)
                }
                findNavController().navigate(
                    R.id.action_addNewReportFragment_to_reportActivity, bundle
                )
            }
        }
    }

    private fun getReportContent(): ReportNew = binding.addNewReport.let {
        val id = if (args.reportEdit == -1) null else args.reportEdit
        val company = it.companyId.text.toString()
        val email = it.emailId.text.toString()
        val date = it.dateId.text.toString()
        val controller = it.controllerId.text.toString()

        return ReportNew(id, company, email, date, controller)
    }

    override fun onResume() {
        super.onResume()
        enableButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        uiStateJobName?.cancel()
    }
}