package com.rendersoncs.reportform.view.activitys;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.view.services.photo.ResizeImage;

import java.util.ArrayList;

class CheckAnswerList {
    private ResizeImage resizeImage = new ResizeImage();

    // get Title and Description
    void checkAnswerList(int i,
                         ArrayList<ReportItems> reportItems,
                         ArrayList<String> listTitle,
                         ArrayList<String> listDescription) {

        String title = reportItems.get(i).getTitle();
        listTitle.add(title);
        Log.i("LOG", "getTitle " + listTitle);

        String description = reportItems.get(i).getDescription();
        listDescription.add(description);
        Log.i("LOG", "getDescription " + listDescription);
    }

    boolean checkAnswerNote(Activity activity,
                            int i,
                            ArrayList<ReportItems> reportItems,
                            ArrayList<String> listNotes) {

        String note = reportItems.get(i).getNote();
        if (note == null) {
            note = activity.getResources().getString(R.string.label_not_observation);
            listNotes.add(note);
        } else {
            listNotes.add(note);
            Log.i("LOG", "getNotes " + listNotes);
        }
        return false;
    }

    // check answer list radio buttons
    void checkAnswerRadiosButtons(Activity activity,
                                  int i,
                                  ArrayList<ReportItems> reportItems,
                                  ArrayList<String> listRadio) {

        if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM1) {
            String C = activity.getResources().getString(R.string.according);
            listRadio.add(C);
        }
        if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM2) {
            String NA = activity.getResources().getString(R.string.not_applicable);
            listRadio.add(NA);
        }
        if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM3) {
            String NC = activity.getResources().getString(R.string.not_according);
            listRadio.add(NC);
        }
    }

    // check answer list with photos
    boolean checkAnswerPhoto(Activity activity,
                             int i,
                             ArrayList<ReportItems> reportItems,
                             ArrayList<String> listPhoto) {

        Bitmap bitmapPhoto = reportItems.get(i).getPhotoId();
        if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM1 && bitmapPhoto == null
                || reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM2 && bitmapPhoto == null
                || reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM3 && bitmapPhoto == null) {

            Drawable d = activity.getResources().getDrawable(R.drawable.walpaper_not_photo);

            Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);

            String encodeImage = resizeImage.getEncoded64Image(b);
            listPhoto.add(encodeImage);

        } /*else if (bitmapPhoto == null) {
            return true;
        }*/ else {
            String encodeImage = resizeImage.getEncoded64Image(bitmapPhoto);
            listPhoto.add(encodeImage);
            Log.i("List ", "List Photo " + listPhoto.size() + " item");
        }
        return false;
    }
}
