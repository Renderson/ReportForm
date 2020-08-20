package com.rendersoncs.reportform.view.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.itens.DetailPhoto
import kotlinx.android.synthetic.main.fragment_bottom_sheet_detaill_photo.*

class BottomSheetDetailPhotoFragment : BottomSheetDialogFragment() {
    private lateinit var detail: DetailPhoto

    private var bottomSheet: View? = null
    //private var bottomSheetPeekHeight = 0

    private fun newInstance(): BottomSheetDetailPhotoFragment? {
        return BottomSheetDetailPhotoFragment()
    }

    override fun getTheme(): Int {
        return R.style.Theme_MaterialComponents_DayNight_BottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgressBar(View.VISIBLE)
    }

    override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater
                .inflate(R.layout.fragment_bottom_sheet_detaill_photo, container, false)
        bottomSheet = view.findViewById(R.id.contentBottomSheet)

        detail = arguments?.getSerializable("modelDetail") as DetailPhoto

        this.newInstance()
        return view
    }

    override fun onResume() {
        super.onResume()
        this.setUpBottomSheet()
        this.initView()
    }

    private fun initView() {
            showProgressBar(View.GONE)

            Glide.with(requireActivity()).load(detail.photo).centerCrop().into(imageDetail)
            titleDetail.text = detail.title
            descriptionDetail.text = detail.description
            noteDetail.text = detail.note
            conformityDetail.text = detail.conformed

        if (imageDetail.drawable == null){
            showProgressBar(View.VISIBLE)
        }
    }

    private fun showProgressBar(view: Int) {
        progressDetail.visibility = view
        progressText.visibility = view
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior
                .from(requireView().parent as View)

        //bottomSheetBehavior.peekHeight = bottomSheetPeekHeight
        //bottomSheetBehavior.isHideable = false

        val childLayoutParams = bottomSheet?.layoutParams
        val displayMetrics = DisplayMetrics()

        requireActivity()
                .windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)

        if (childLayoutParams != null) {
            childLayoutParams.height = displayMetrics.heightPixels * 100 / 100
        }

        bottomSheet?.layoutParams = childLayoutParams
    }

}