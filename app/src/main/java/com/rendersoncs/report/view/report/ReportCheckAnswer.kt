package com.rendersoncs.report.view.report

import android.app.Activity
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.model.ReportItems
import java.util.*

internal class ReportCheckAnswer {

    // get Title and Description
    fun checkAnswerList(i: Int,
                        reportItems: ArrayList<ReportItems>,
                        listTitle: ArrayList<String?>,
                        listDescription: ArrayList<String?>) {
        val title = reportItems[i].title
        listTitle.add(title)
        val description = reportItems[i].description
        listDescription.add(description)
    }

    // get Note
    fun checkAnswerNote(activity: Activity,
                        i: Int,
                        reportItems: ArrayList<ReportItems>,
                        listNotes: ArrayList<String?>) {
        var note = reportItems[i].note
        if (note == null) {
            note = activity.resources.getString(R.string.label_not_observation)
            listNotes.add(note)
        } else {
            listNotes.add(note)
        }
    }

    // check answer list radio buttons
    fun checkAnswerRadiosButtons(activity: Activity,
                                 i: Int,
                                 reportItems: ArrayList<ReportItems>,
                                 listRadio: ArrayList<String?>) {
        if (reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM1) {
            val according = activity.resources.getString(R.string.according)
            listRadio.add(according)
        }
        if (reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM2) {
            val notAccording = activity.resources.getString(R.string.not_applicable)
            listRadio.add(notAccording)
        }
        if (reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM3) {
            val notCompliant = activity.resources.getString(R.string.not_according)
            listRadio.add(notCompliant)
        }
    }

    // check answer list with photos
    fun checkAnswerPhoto(i: Int,
                         reportItems: ArrayList<ReportItems>,
                         listPhoto: ArrayList<String?>) {
        val photo = reportItems[i].photoPath
        if (reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM1 &&
                photo == null ||
                reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM2 &&
                photo == null ||
                reportItems[i].selectedAnswerPosition == ReportConstants.ITEM.OPT_NUM3 && photo == null) {
            listPhoto.add(ReportConstants.PHOTO.NOT_PHOTO)
        } else {
            listPhoto.add(photo.toString())
        }
    }
}
