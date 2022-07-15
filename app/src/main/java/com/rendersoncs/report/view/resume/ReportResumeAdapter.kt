package com.rendersoncs.report.view.resume

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ItemReportResumeListBinding
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.model.ReportResumeItems
import kotlinx.android.synthetic.main.item_report_resume_list.view.*

class ReportResumeAdapter(private val repoResumeList:
                          ArrayList<ReportResumeItems>,
                          private val context: Context) :
        RecyclerView.Adapter<ReportResumeAdapter.ViewHolder>() {

    private var onItemClickResume: ReportResumeListener? = null

    fun setOnItemListenerClicked(onItemClickResume: ReportResumeListener) {
        this.onItemClickResume = onItemClickResume
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val binding = ItemReportResumeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val position = holder.layoutPosition
        val resumeItems = repoResumeList[position]

        holder.resumeTitle.text = resumeItems.title
        holder.resumeDescription.text = resumeItems.description
        holder.resumeConformity.text = resumeItems.conformity
        holder.resumeNote.text = resumeItems.note

        holder.resumePhoto.setOnClickListener {
            onItemClickResume!!.detailPhoto(resumeItems)
        }

        this.decorationItems(holder, resumeItems)
    }

    private fun decorationItems(holder: ViewHolder, resumeItems: ReportResumeItems) {
        val photo = resumeItems.photo

        if (photo.isEmpty() || photo == ReportConstants.PHOTO.NOT_PHOTO) {
            holder.resumePhoto.imageAlpha = R.drawable.ic_broken_image
        } else {
            Glide.with(holder.resumePhoto.context).load(photo).centerCrop().into(holder.resumePhoto)
        }

        val according = context.resources.getString(R.string.according)
        val notApplicable = context.resources.getString(R.string.not_applicable)

        when (holder.resumeConformity.text) {
            according -> {
                holder.resumeConformity.apply {
                    setTextColor(
                            ContextCompat.getColor(this.context,
                                    R.color.colorRadioC))
                }
            }
            notApplicable -> {
                holder.resumeConformity.apply {
                    setTextColor(
                            ContextCompat.getColor(this.context,
                                    R.color.colorRadioNA))
                }
            }
            else -> {
                holder.resumeConformity.apply {
                    setTextColor(
                            ContextCompat.getColor(this.context,
                                    R.color.colorRadioNC))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return repoResumeList.size
    }

    class ViewHolder(val binding: ItemReportResumeListBinding) : RecyclerView.ViewHolder(binding.root) {
        val resumeTitle = binding.joTitle
        val resumeDescription = binding.joDescription
        val resumeConformity = binding.joConformity
        val resumeNote = binding.joNote
        val resumePhoto = binding.joPhoto
    }
}