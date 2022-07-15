package com.rendersoncs.report.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.rendersoncs.report.infrastructure.util.SnackBarHelper

abstract class BaseBindingFragment<T : ViewBinding, VM : ViewModel>(private val layoutRes: Int) : Fragment() {

    private var _binding: T? = null
    val binding: T get() = _binding!!

    protected abstract val viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root

    }

    fun showSnackBar(message: String?, processBar: ProgressBar) {
        val snackbar = Snackbar.make(
            processBar,
            message!!,
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null)
        SnackBarHelper.configSnackBar(requireContext(), snackbar)
        snackbar.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}