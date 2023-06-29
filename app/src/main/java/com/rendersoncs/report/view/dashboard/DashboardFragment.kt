package com.rendersoncs.report.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rendersoncs.report.R
import com.rendersoncs.report.data.net.NetworkConnectedService
import com.rendersoncs.report.databinding.FragmentDashboardBinding
import com.rendersoncs.report.infrastructure.util.SnackBarHelper
import com.rendersoncs.report.infrastructure.util.ViewState
import com.rendersoncs.report.infrastructure.util.hide
import com.rendersoncs.report.infrastructure.util.show
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, ReportViewModel>() {

    override val viewModel: ReportViewModel by activityViewModels()

    private val adapter by lazy {
        DashboardAdapter(listener = this::onClickItem)
    }

    private val snackBarHelper = SnackBarHelper()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllReports()
        onObserveReport()
        setupRV()
        initViews()

        // check connection netWorking
        NetworkConnectedService().isConnected(requireActivity()) {
            snackBarHelper.showSnackBar(requireActivity(), binding.btnCreateReport)
        }
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    private fun setupRV() = with(binding) {
        reportRv.apply {
            adapter = this@DashboardFragment.adapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initViews() = with(binding) {
        btnCreateReport.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("reportEdit", -1)
            }
            findNavController().navigate(R.id.action_dashboardFragment_to_newReportFragment, bundle)
        }
    }

    private fun onObserveReport() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is ViewState.Loading -> {
                }
                is ViewState.Success -> {
                    showAllViews()
                    onReportLoad(uiState.report)
                }
                is ViewState.Error -> {
                    showHideViews()
                }
                is ViewState.Empty -> {
                    showHideViews()
                }
            }
        }
    }

    private fun showAllViews() = with(binding) {
        reportRv.show()
        emptyState.hide()
    }

    private fun showHideViews() = with(binding) {
        reportRv.hide()
        emptyState.show()
    }

    private fun onReportLoad(list: List<Report>) = adapter.differ.submitList(list)

    private fun onClickItem(report: Report) {
        val bundle = Bundle().apply {
            putSerializable("reportResume", report)
        }
        findNavController().navigate(
            R.id.action_dashboardFragment_to_reportResumeFragment, bundle
        )
    }
}