package com.rendersoncs.report.view.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.util.component1
import androidx.core.util.component2
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
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, ReportViewModel>() {

    override val viewModel: ReportViewModel by activityViewModels()

    private lateinit var reportListAdapter: DashboardAdapter
    private lateinit var listener: DashboardListener
    private lateinit var report: Report

    private val snackBarHelper = SnackBarHelper()

    private val requestLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) openPdf(report) else toast(getString(R.string.label_permission_camera_denied))
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllReports()
        onObserveReport()
        listenerItems()
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
        reportListAdapter = DashboardAdapter(listener)
        reportRv.apply {
            adapter = reportListAdapter
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

    private fun onReportLoad(list: List<Report>) =
            reportListAdapter.differ.submitList(list)

    private fun listenerItems() {
        listener = object : DashboardListener {
            override fun onClickReport(report: Report) {
                val bundle = Bundle().apply {
                    putSerializable("reportResume", report)
                }
                findNavController().navigate(
                        R.id.action_dashboardFragment_to_reportResumeFragment, bundle
                )
            }

            override fun onOpenPdf(report: Report) {
                openPdf(report)
            }

            override fun onDeleteReport(report: Report) {
                viewModel.deletePhotosDirectory(report)

                val (subject, uri) = viewModel.getDocument(report)
                activity?.contentResolver?.delete(uri, subject, null)

                viewModel.deleteReportByID(report.id)
            }

            override fun onShareReport(report: Report) {
                shareDocument(report)
            }

            override fun onEditReport(id: Int) {}
        }
    }

    private fun openPdf(item: Report) {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            this.report = item
            return
        }
        val (subject, uri) = viewModel.getDocument(item)

        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(this)
        }
    }

    private fun shareDocument(item: Report) {
        val (subject, uri) = viewModel.getDocument(item)

        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "pdf/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(item.email))
            putExtra(Intent.EXTRA_TEXT,
                    getString(
                            R.string.label_attach_report,
                            item.company) + " " + item.date)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(this, resources.getString(R.string.share)))
        }
    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}