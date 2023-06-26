package com.rendersoncs.report.view.resume

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import com.rendersoncs.report.infrastructure.util.ItemCallBack
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ItemReportResumeListBinding
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.model.ReportResumeItems

class ReportResumeAdapter(private val onClickItem: (ReportResumeItems) -> Unit) :
        RecyclerView.Adapter<ReportResumeAdapter.ViewHolder>() {

    val differ = AsyncListDiffer(this, ItemCallBack<ReportResumeItems>())

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val binding = ItemReportResumeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val position = holder.layoutPosition
        val resumeItems = differ.currentList[position]

        holder.resumeTitle.text = resumeItems.title
        holder.resumeDescription.text = resumeItems.description
        holder.resumeConformity.text = when (resumeItems.conformity) {
            1 -> { holder.resumeConformity.context.getString(R.string.according) }
            2 -> { holder.resumeConformity.context.getString(R.string.not_applicable) }
            else -> holder.resumeConformity.context.getString(R.string.not_according)
        }
        holder.resumeNote.text = resumeItems.note.ifEmpty {
            holder.resumeNote.context.getString(R.string.label_not_observation)
        }

        holder.resumePhoto.setOnClickListener {
            onClickItem.invoke(resumeItems)
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

        val according = holder.itemView.context.resources.getString(R.string.according)
        val notApplicable = holder.itemView.context.resources.getString(R.string.not_applicable)

        when (holder.resumeConformity.text) {
            according -> {
                holder.resumeConformity.apply {
                    setTextColor(
                            ContextCompat.getColor(holder.resumeConformity.context,
                                    R.color.colorRadioC))
                }
            }
            notApplicable -> {
                holder.resumeConformity.apply {
                    setTextColor(
                            ContextCompat.getColor(holder.resumeConformity.context,
                                    R.color.colorRadioNA))
                }
            }
            else -> {
                holder.resumeConformity.apply {
                    setTextColor(
                            ContextCompat.getColor(holder.resumeConformity.context,
                                    R.color.colorRadioNC))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(val binding: ItemReportResumeListBinding) : RecyclerView.ViewHolder(binding.root) {
        val resumeTitle = binding.joTitle
        val resumeDescription = binding.joDescription
        val resumeConformity = binding.joConformity
        val resumeNote = binding.joNote
        val resumePhoto = binding.joPhoto
    }
}