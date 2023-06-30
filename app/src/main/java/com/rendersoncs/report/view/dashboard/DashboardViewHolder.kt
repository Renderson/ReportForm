package com.rendersoncs.report.view.dashboard

import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ItemMainListBinding
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.extension.StringExtension.limitsText
import com.rendersoncs.report.model.Report
import java.util.Locale

class DashboardViewHolder(val binding: ItemMainListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(report: Report, listener: (Report) -> Unit) = with(binding) {

        companyView.text = limitsText(report.company, ReportConstants.CHARACTERS.LIMITS_TEXT)
        dateView.text = report.date
        resultView.text = report.result

        val result = itemView.context.getString(R.string.according)

        if (report.result == result.uppercase(Locale.ROOT)) {
            val colorAccording = ContextCompat.getColor(binding.viewResult.context, R.color.colorRadioC)
            changeColorShape(colorAccording)
        } else {
            val colorNotAccording = ContextCompat.getColor(binding.viewResult.context, R.color.colorRadioNC)
            changeColorShape(colorNotAccording)
        }

        itemView.setOnClickListener {
            listener.invoke(report)
        }
    }

    private fun changeColorShape(color: Int) {
        val bgShape = binding.viewResult.background as GradientDrawable
        bgShape.setColor(color)
    }
}