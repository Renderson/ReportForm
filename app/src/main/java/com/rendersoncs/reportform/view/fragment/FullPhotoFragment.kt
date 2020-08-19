package com.rendersoncs.reportform.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.services.constants.ReportConstants

class FullPhotoFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_full_image, container, false)
        val imageFull = view.findViewById<ImageView>(R.id.imageFull)
        val conformity = view.findViewById<TextView>(R.id.conformityImage)

        if (arguments == null) {
            Toast.makeText(activity, resources.getString(R.string.label_error_return), Toast.LENGTH_SHORT).show()
        }

        if (requireArguments().getString(ReportConstants.ITEM.CONFORMITY) == null){
            conformity.text = ""
        } else {
            conformity.text = requireArguments().getString(ReportConstants.ITEM.CONFORMITY)!!
        }

        val photo = requireArguments().getString(ReportConstants.ITEM.PHOTO)!!
        Glide.with(requireActivity()).load(photo).centerCrop().into(imageFull)
        return view
    }
}