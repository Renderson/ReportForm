package com.rendersoncs.report.view.report

import android.animation.ObjectAnimator
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.model.ReportItems
import kotlinx.android.synthetic.main.item_report_check_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class ReportCheckListAdapter(
        private var reportItems: List<ReportItems>) :
        RecyclerView.Adapter<ReportCheckListAdapter.ReportViewHolder>(), Filterable {

    private var reportItemsFiltered = reportItems
    val expandState: SparseBooleanArray = SparseBooleanArray()

    init {
        for (i in reportItems.indices) {
            expandState.append(i, false)
        }
    }

    private var onItemClickedReport: ReportListener? = null
    fun setOnItemListenerClicked(onItemClickedReport: ReportListener?) {
        this.onItemClickedReport = onItemClickedReport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_check_list,
                parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(reportViewHolder: ReportViewHolder, i: Int) {
        val position = reportViewHolder.layoutPosition
        val reportItems = reportItemsFiltered[position]
        reportViewHolder.bindView(reportViewHolder, position, reportItems)
    }

    override fun getItemCount() = reportItemsFiltered.count()

    inner class ReportViewHolder(rowView: View) : RecyclerView.ViewHolder(rowView),
            View.OnClickListener {

        private val expandableLayout: LinearLayout = rowView.expandableLayout
        private var tvTitleList: TextView = rowView.textView_title
        private var tvDescription: TextView = rowView.textView_subTitle
        private var takePhoto: ImageView = rowView.photo
        private var resultPhoto: ImageView = rowView.result_photo
        private var check: ImageView = rowView.action_check
        private var note: ImageView = rowView.note
        private var resetItem: ImageView = rowView.action_reset_item
        private var buttonLayoutArrow: RelativeLayout = rowView.btnArrow
        private var mRadioButtonConform: RadioButton = rowView.radio_conform
        private var mRadioButtonNotApplicable: RadioButton = rowView.radio_not_applicable
        private var mRadioButtonNotConform: RadioButton = rowView.radio_not_conform

        override fun onClick(v: View) {
            when (v.id) {
                R.id.radio_conform -> if (onItemClickedReport != null)
                    onItemClickedReport!!.radioItemChecked(reportItemsFiltered[layoutPosition],
                            ReportConstants.ITEM.OPT_NUM1)
                R.id.radio_not_applicable -> if (onItemClickedReport != null)
                    onItemClickedReport!!.radioItemChecked(reportItemsFiltered[layoutPosition],
                            ReportConstants.ITEM.OPT_NUM2)
                R.id.radio_not_conform -> if (onItemClickedReport != null)
                    onItemClickedReport!!.radioItemChecked(reportItemsFiltered[layoutPosition],
                            ReportConstants.ITEM.OPT_NUM3)
            }
        }

        fun bindView(reportViewHolder: ReportViewHolder, position: Int, reportItems: ReportItems) {
            val isExpanded = expandState[position]
            reportViewHolder.setIsRecyclable(false)

            tvTitleList.text = reportItems.title
            tvDescription.text = reportItems.description

            expandableLayout.visibility = if (isExpanded) View.GONE else View.VISIBLE
            buttonLayoutArrow.rotation = if (expandState[position]) 0f else 180f

            buttonLayoutArrow.setOnClickListener {
                onClickButton(expandableLayout, buttonLayoutArrow, position)
            }
            clickItemsListener(reportItems, position)
            answersItems(reportItems)
        }

        private fun clickItemsListener(reportItems: ReportItems, position: Int) {
            tvTitleList.setOnClickListener {
                onItemClickedReport!!.updateList(reportItems)
            }

            tvTitleList.setOnLongClickListener {
                onItemClickedReport!!.removeItem(reportItems)
                false
            }

            note.setOnClickListener { onItemClickedReport!!.insertNote(reportItems) }
            takePhoto.setOnClickListener { onItemClickedReport!!.takePhoto(reportItems) }
            resultPhoto.setOnClickListener { onItemClickedReport!!.fullPhoto(reportItems) }
            resetItem.setOnClickListener { onItemClickedReport!!.resetItem(reportItems, position) }
        }

        private fun answersItems(reportItems: ReportItems) {
            if (reportItems.photoPath == null ||
                    reportItems.photoPath.toString() == ReportConstants.PHOTO.NOT_PHOTO) {
                resultPhoto.imageAlpha = R.drawable.image
                check.setColorFilter(ContextCompat.getColor(check.context, R.color.white))
                resetItem.visibility = View.GONE
            } else {
                mRadioButtonConform.isChecked = true
                resultPhoto.let {
                    Glide.with(resultPhoto.context).load(reportItems.photoPath).centerCrop().into(it) }
            }
            if (reportItems.note == null || reportItems.note!!.isEmpty()) {
                note.imageAlpha = R.drawable.ic_action_note
            } else {
                note.setColorFilter(ContextCompat.getColor(note.context, R.color.colorIconNote))
                resetItem.visibility = View.VISIBLE
            }

            mRadioButtonConform.isChecked = reportItems.isOpt1
            mRadioButtonNotApplicable.isChecked = reportItems.isOpt2
            mRadioButtonNotConform.isChecked = reportItems.isOpt3

            if (mRadioButtonConform.isChecked) {
                /*resultPhoto.setBackgroundResource(R.color.colorRadioC);*/
                check.setBackgroundColor(ContextCompat.getColor(check.context, R.color.colorRadioC))
                resetItem.visibility = View.VISIBLE
            }
            if (mRadioButtonNotApplicable.isChecked) {
                /*resultPhoto.setBackgroundResource(R.color.colorRadioNA);*/
                check.setBackgroundColor(ContextCompat.getColor(check.context, R.color.colorRadioNA))
                resetItem.visibility = View.VISIBLE
            } else if (mRadioButtonNotConform.isChecked) {
                /*resultPhoto.setBackgroundResource(R.color.colorRadioNC);*/
                check.setBackgroundColor(ContextCompat.getColor(check.context, R.color.colorRadioNC))
                resetItem.visibility = View.VISIBLE
            }
        }

        init {
            tvTitleList.setOnClickListener(this)
            takePhoto.setOnClickListener(this)
            mRadioButtonConform.setOnClickListener(this)
            mRadioButtonNotApplicable.setOnClickListener(this)
            mRadioButtonNotConform.setOnClickListener(this)
        }
    }

    private fun onClickButton(expandableLayout: LinearLayout, buttonLayout: RelativeLayout, i: Int) {

        //Expand CardView
        if (expandableLayout.visibility == View.GONE) {
            createRotateAnimator(buttonLayout, 0f, 180f).start()
            expandableLayout.visibility = View.VISIBLE
            expandState.put(i, false)
        } else {
            createRotateAnimator(buttonLayout, 180f, 0f).start()
            expandableLayout.visibility = View.GONE
            expandState.put(i, true)
        }
    }

    //Animation Expand
    private fun createRotateAnimator(target: View, from: Float, to: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "rotation", from, to)
        animator.duration = 300
        animator.interpolator = LinearInterpolator()
        return animator
    }

    // Set result image
    fun setImageInItem(report: ReportItems, photoPath: String?) {
        report.photoPath = photoPath
        Log.i("LOG", "ImagePath2 $photoPath")
        notifyDataSetChanged()
    }

    // Set result note
    fun insertNote(report: ReportItems, note: String?) {
        report.note = note
        Log.i("LOG", "Note $note")
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                reportItemsFiltered = if (charString.isEmpty()) {
                    reportItems
                } else {
                    val filteredList: MutableList<ReportItems> = ArrayList()
                    for (row in reportItems) {
                        if (row.title!!.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT))) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = reportItemsFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                reportItemsFiltered = filterResults.values as List<ReportItems>
                notifyDataSetChanged()
            }
        }
    }
}