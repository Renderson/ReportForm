package com.rendersoncs.reportform.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.listener.OnInteractionListener;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    TextView resume, share, delete;

    Repo repoEntity;
    private OnInteractionListener listener;
    private ReportBusiness mReportBusiness;
    private static final String PACKAGE_FILE_PROVIDER = "com.rendersoncs.reportform.FileProvider";

    public BottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        resume = view.findViewById(R.id.sheet_resume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
                Log.i("item", "resumeClick : " + resume);
            }
        });

        return view;
    }

}
