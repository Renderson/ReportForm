package com.rendersoncs.reportform.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rendersoncs.reportform.R
import com.rendersoncs.reportform.itens.ReportResumeItems
import com.rendersoncs.reportform.view.adapter.listener.OnItemClickResume
import com.rendersoncs.reportform.view.services.constants.ReportConstants
import kotlinx.android.synthetic.main.activity_report_resume_list.view.*

class ReportResumeAdapter(private val repoResumeList:
                          List<ReportResumeItems>,
                          private val context: Context) :
        RecyclerView.Adapter<ReportResumeAdapter.ViewHolder>() {

    private var onItemClickResume: OnItemClickResume? = null

    fun setOnItemListenerClicked(onItemClickResume: OnItemClickResume) {
        this.onItemClickResume = onItemClickResume
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_report_resume_list,
                parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val position = holder.adapterPosition
        val resumeItems = repoResumeList[position]

        holder.resumeTitle.text = resumeItems.title
        holder.resumeDescription.text = resumeItems.description
        holder.resumeConformity.text = resumeItems.conformity
        holder.resumeNote.text = resumeItems.note

        holder.resumePhoto.setOnClickListener {
            onItemClickResume!!.fullPhoto(resumeItems) }

        this.decorationItems(holder, resumeItems)
    }

    private fun decorationItems(holder: ViewHolder, resumeItems: ReportResumeItems) {
        val photo = resumeItems.photo

        if (photo.isEmpty() || photo == ReportConstants.PHOTO.NOT_PHOTO){
            holder.resumePhoto.imageAlpha = R.drawable.ic_broken_image
        } else {
            Glide.with(context).load(photo).centerCrop().into(holder.resumePhoto)
        }

        val according = context.resources.getString(R.string.according)
        val notApplicable = context.resources.getString(R.string.not_applicable)

        when (holder.resumeConformity.text) {
            according -> {
                holder.resumeConformity.setTextColor(ContextCompat.getColor(context, R.color.colorRadioC))
            }
            notApplicable -> {
                holder.resumeConformity.setTextColor(ContextCompat.getColor(context, R.color.colorRadioNA))
            }
            else -> {
                holder.resumeConformity.setTextColor(ContextCompat.getColor(context, R.color.colorRadioNC))
            }
        }
    }

    override fun getItemCount(): Int {
        return repoResumeList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resumeTitle = itemView.jo_title!!
        val resumeDescription = itemView.jo_description!!
        val resumeConformity = itemView.jo_conformity!!
        val resumeNote = itemView.jo_note!!
        val resumePhoto = itemView.jo_photo!!
    }
}