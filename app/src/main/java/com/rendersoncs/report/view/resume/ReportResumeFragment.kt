package com.rendersoncs.report.view.resume

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentReportResumeBinding
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.infrastructure.extension.StringExtension
import com.rendersoncs.report.infrastructure.util.DetailState
import com.rendersoncs.report.infrastructure.util.ResumeState
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportDetailPhoto
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ReportResumeFragment : BaseFragment<FragmentReportResumeBinding, ReportViewModel>() {

    override val viewModel: ReportViewModel by activityViewModels()
    private val args: ReportResumeFragmentArgs by navArgs()

    private val resumeAdapter by lazy { ReportResumeAdapter(this::detailPhoto) }

    private val listRadioC = ArrayList<String>()
    private val listRadioNA = ArrayList<String>()
    private val listRadioNC = ArrayList<String>()
    private lateinit var report: Report

    private val requestLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) toast(getString(R.string.txt_permission)) else toast(getString(R.string.label_permission_camera_denied))
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        report = args.reportResume
        getReport(report.id)
        observeReport()
        observerListResume()
        countRadioSelected()
        createPieChart()
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReportResumeBinding.inflate(inflater, container, false)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_detail_share -> shareDocument()
            R.id.action_detail_delete -> deleteReport()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareDocument() {
        val (subject, uri) = viewModel.getDocument(report)

        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "pdf/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(report.email))
            putExtra(Intent.EXTRA_TEXT,
                    getString(
                            R.string.label_attach_report,
                            report.company) + " " + report.date)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(this, resources.getString(R.string.share)))
        }
    }

    private fun deleteReport() {
        viewModel.deletePhotosDirectory(report)

        val (subject, uri) = viewModel.getDocument(report)
        activity?.contentResolver?.delete(uri, subject, null)

        viewModel.deleteReportByID(report.id)
        findNavController().navigateUp()
    }

    private fun getReport(id: Int) {
        viewModel.getReportByID(id)
    }

    private fun observeReport() {
        viewModel.detailState.observe(viewLifecycleOwner) { detailState ->
            when (detailState) {
                DetailState.Loading -> {}
                is DetailState.Success -> {
                    onDetailLoaded(detailState.report)
                    viewModel.getListReportResume(report)
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

    private fun onDetailLoaded(report: Report) = with(binding) {
        this.contentResume.apply {
            controllerResume.text = getString(R.string.label_report,
                report.controller.uppercase(Locale.ROOT)
            )
            emailResume.text = report.email
            dateResume.text = report.date
            companyResume.text = StringExtension.limitsText(report.company,
                    ReportConstants.CHARACTERS.LIMITS_TEXT)
            openPdf.setOnClickListener {
                openPdf(report)
            }
        }
        this.editReport.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("reportEdit", report.id)
            }
            findNavController().navigate(R.id.action_reportResume_to_editReport, bundle)
        }
    }

    private fun openPdf(item: Report) {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private fun observerListResume() {
        viewModel.resumeList.observe(viewLifecycleOwner) { list ->
            when (list) {
                is ResumeState.Loading -> {
                }

                is ResumeState.Success -> {
                    resumeAdapter.differ.submitList(list.report)
                    initRv()
                }

                is ResumeState.Error -> {
                    toast("Error")
                }
            }
        }
    }

    private fun initRv() = with(binding) {
        contentResume.resumeRv.apply {
            adapter = resumeAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun createPieChart() = with(binding) {
        this.contentResume.resumeGraph.pieChart.apply {
            this.setUsePercentValues(false)
            this.description.isEnabled = false
            this.setExtraOffsets(5f, 10f, 5f, 5f)

            // Rotation graphic
            this.dragDecelerationFrictionCoef = 0.95f

            // Design graphic
            this.isDrawHoleEnabled = false
            this.setHoleColor(Color.WHITE)
            this.transparentCircleRadius = 61f
        }

        val yValues = ArrayList<PieEntry>()
        val mxC = listRadioC.size
        val mxNA = listRadioNA.size
        val mxNC = listRadioNC.size

        yValues.add(PieEntry(mxC.toFloat(), resources.getString(R.string.according)))
        yValues.add(PieEntry(mxNA.toFloat(), resources.getString(R.string.not_applicable)))
        yValues.add(PieEntry(mxNC.toFloat(), resources.getString(R.string.not_according)))
        val dataSet = PieDataSet(yValues, "")

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextSize = 24f
        dataSet.setColors(ContextCompat.getColor(requireContext(), R.color.colorRadioC),
                ContextCompat.getColor(requireContext(), R.color.colorRadioNA),
                ContextCompat.getColor(requireContext(), R.color.colorRadioNC))

        // Animation
        binding.contentResume.resumeGraph.pieChart.animateY(800, Easing.EaseInCirc)
        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.WHITE)
        binding.contentResume.resumeGraph.pieChart.data = data
    }

    /*@ExperimentalStdlibApi
    private fun integerFormatter(dataSet: PieDataSet): PieData {
        val data = PieData(dataSet)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return floor(value.toDouble()) as Int.toString()
            }
        })
        return data
    }*/

    @SuppressLint("StringFormatMatches")
    private fun countRadioSelected() = lifecycleScope.launchWhenResumed {
        viewModel.reportResumeItems.observe(viewLifecycleOwner) { report ->
            report.forEach {
                if (it.conformity == resources.getString(R.string.according)) {
                    listRadioC.add(resources.getString(R.string.according))
                    println(it.conformity)
                }
                if (it.conformity == resources.getString(R.string.not_applicable)) {
                    listRadioNA.add(resources.getString(R.string.not_applicable))
                    println(it.conformity)
                }
                if (it.conformity == resources.getString(R.string.not_according)) {
                    listRadioNC.add(resources.getString(R.string.not_according))
                    println(it.conformity)
                }
            }
            val maxList = report.size
            binding.contentResume.resumeGraph.itemsResume.text =
                getString(R.string.item_selected, maxList)
            createPieChart()
        }
    }

    private fun detailPhoto(reportResume: ReportResumeItems) {
        val detailPhoto = ReportDetailPhoto(
                reportResume.photo,
                reportResume.title,
                reportResume.description,
                reportResume.note,
                ""
        )

        if (reportResume.photo == ReportConstants.PHOTO.NOT_PHOTO) {
            toast(resources.getString(R.string.label_nothing_image))
            return
        }
        val bundle = Bundle().apply {
            putSerializable("modelDetail", detailPhoto)
        }
        findNavController().navigate(
                R.id.action_reportResume_to_detailPhotoFragment, bundle
        )
    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}