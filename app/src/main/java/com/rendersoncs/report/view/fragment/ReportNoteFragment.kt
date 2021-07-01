package com.rendersoncs.report.view.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rendersoncs.report.databinding.FragmentReportNoteBinding
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportNoteFragment : BaseFragment<FragmentReportNoteBinding, ReportViewModel>(),
    TextView.OnEditorActionListener {
    override val viewModel: ReportViewModel by activityViewModels()
    private val args: ReportNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() = with(binding) {
        if (args.note == "null") txtNote.setText("") else txtNote.setText(args.note)

        txtNote.setOnEditorActionListener(this@ReportNoteFragment)
        btnAddNote.setOnClickListener {
            insertNote()
        }
    }

    private fun insertNote() = with(binding) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "noteTest",
            txtNote.text.toString()
        )
        findNavController().navigateUp()
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReportNoteBinding.inflate(
            inflater,
            container,
            false
    )

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(requireContext(), requireView())
            insertNote()
            return true
        }
        return false
    }
}