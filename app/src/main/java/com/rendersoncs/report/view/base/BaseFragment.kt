package com.rendersoncs.report.view.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.rendersoncs.report.infrastructure.util.SnackBarHelper.Companion.configSnackBar

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: VM
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB?

    fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(message: String?, processBar: ProgressBar) {
        val snackbar = Snackbar.make(
            processBar,
            message!!,
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null)
        configSnackBar(requireContext(), snackbar)
        snackbar.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun keyboardCloseTouchListener(recyclerView: RecyclerView) {
        recyclerView.setOnTouchListener { v: View, _: MotionEvent? ->
            val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    fun applicationContext(): Context = requireContext().applicationContext

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}