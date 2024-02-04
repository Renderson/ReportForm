package com.rendersoncs.report.view.newReport

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
import com.rendersoncs.report.common.util.*
import com.rendersoncs.report.model.ReportNew
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewReportFragment :
    BaseFragment<FragmentNewReportBinding, ReportViewModel>()
{

    override val viewModel: ReportViewModel by activityViewModels()
    private val args: NewReportFragmentArgs by navArgs()

    private var uiStateJobName: Job? = null
    private val date = Calendar.getInstance().time
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var email: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.reportEdit != -1) {
            viewModel.getReportByID(args.reportEdit)
        }
        viewModel.getNameShared()

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
                    enableButton()
                    textInputCompany.error = getString(R.string.txt_enter_name_company)
                } else {
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
                    enableButton()
                    textInputController.error = getString(R.string.txt_enter_name_controller)
                } else {
                    enableButton()
                    textInputController.error = null
                }
            }
        }
    }

    private fun enableButton() = with(binding) {
        btnNewReport.isEnabled = addNewReport.companyId.text?.isNotEmpty() == true
                && email
                && addNewReport.controllerId.text?.isNotEmpty() == true
    }

    private fun setListener() = with(binding) {
        btnNewReport.setOnClickListener {
            hideKeyboard()
            binding.addNewReport.apply {
                val reportNew = ReportNew(
                    id = if (args.reportEdit == -1) null else args.reportEdit,
                    company = companyId.text.toString(),
                    email = emailId.text.toString(),
                    date =dateId.text.toString(),
                    controller = controllerId.text.toString()
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

    override fun onResume() {
        super.onResume()
        enableButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        uiStateJobName?.cancel()
    }
}