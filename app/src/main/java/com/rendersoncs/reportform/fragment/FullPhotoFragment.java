package com.rendersoncs.reportform.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.constants.ReportConstants;

public class FullPhotoFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_image, container, false);

        ImageView imageView = view.findViewById(R.id.image_full);

        if (getArguments() == null){
            Toast.makeText(getActivity(), getResources().getString(R.string.label_error_return_photo_full), Toast.LENGTH_SHORT).show();
        }

        byte[] bytes = getArguments().getByteArray(ReportConstants.ITEM.PHOTO);
        assert bytes != null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Glide.with(getActivity()).load(bitmap).centerCrop().into(imageView);

        return view;
    }
}
