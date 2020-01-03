package com.rendersoncs.reportform.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.photo.ResizeImage;

import java.util.ArrayList;

public class CheckAnswerList {
    private ResizeImage resizeImage = new ResizeImage();

    // get Title and Description
    public void checkAnswerList(int i,
                                ArrayList<ReportItems> reportItems,
                                ArrayList listTitle,
                                ArrayList listDescription) {

        String title = reportItems.get(i).getTitle();
        listTitle.add(title);
        Log.i("LOG", "getTitle " + listTitle);

        String description = reportItems.get(i).getDescription();
        listDescription.add(description);
        Log.i("LOG", "getDescription " + listDescription);
    }

    public boolean checkAnswerNote(Activity activity,
                                   int i,
                                   ArrayList<ReportItems> reportItems,
                                   ArrayList listNotes) {

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
    public void checkAnswerRadiosButtons(Activity activity,
                                         int i,
                                         ArrayList<ReportItems> reportItems,
                                         ArrayList listRadio) {

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
    public boolean checkAnswerPhoto(Activity activity,
                                    int i,
                                    ArrayList<ReportItems> reportItems,
                                    ArrayList listPhoto) {

        Bitmap bitmapPhoto = reportItems.get(i).getPhotoId();
        if (reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM1 && bitmapPhoto == null
                || reportItems.get(i).getSelectedAnswerPosition() == ReportConstants.ITEM.OPT_NUM2) {

            Drawable d = activity.getResources().getDrawable(R.drawable.walpaper_not_photo);

            Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);

            String encodeImage = resizeImage.getEncoded64Image(b);
            listPhoto.add(encodeImage);

        } else if (bitmapPhoto == null) {
            return true;
        } else {
            String encodeImage = resizeImage.getEncoded64Image(bitmapPhoto);
            listPhoto.add(encodeImage);
            Log.i("List ", "List Photo " + listPhoto.size() + " item");
        }
        return false;
    }
}
