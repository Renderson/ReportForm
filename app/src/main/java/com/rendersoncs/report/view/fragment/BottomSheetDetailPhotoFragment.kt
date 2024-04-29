package com.rendersoncs.report.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentBottomSheetDetaillPhotoBinding
import com.rendersoncs.report.model.ReportDetailPhoto

class BottomSheetDetailPhotoFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetDetaillPhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var detail: ReportDetailPhoto
    private val args: BottomSheetDetailPhotoFragmentArgs by navArgs()

    override fun getTheme(): Int {
        return com.google.android.material.R.style.Theme_MaterialComponents_DayNight_BottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar(View.VISIBLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetDetaillPhotoBinding.inflate(
                inflater,
                container,
                false)
        detail = args.modelDetail
        return binding.root
    }

    private fun initView() {

        binding.run {
            showProgressBar(View.GONE)

            Glide.with(requireActivity()).load(detail.photo).centerCrop().into(imageDetail)
            titleDetail.text = detail.title
            descriptionDetail.text = detail.description
            noteDetail.text = detail.note
            conformityDetail.text = detail.conformed

            if (imageDetail.drawable == null) {
                showProgressBar(View.VISIBLE)
            }
        }
    }

    private fun showProgressBar(view: Int) {
        binding.run {
            progressDetail.visibility = view
            progressText.visibility = view
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onResume() {
        super.onResume()
        this.initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}