package com.rendersoncs.reportform.view.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.view.services.constants.ReportConstants

class FullPhotoFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_full_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.image_full)

        if (arguments == null) {
            Toast.makeText(activity, resources.getString(R.string.label_error_return_photo_full), Toast.LENGTH_SHORT).show()
        }

        val bytes = arguments!!.getByteArray(ReportConstants.ITEM.PHOTO)!!
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Glide.with(activity!!).load(bitmap).centerCrop().into(imageView)
        return view
    }
}