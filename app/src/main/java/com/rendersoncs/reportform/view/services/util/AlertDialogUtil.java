package com.rendersoncs.reportform.view.services.util;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtil {


    public void showDialog(Activity activity, String title, String msg, String positiveLabel,
                           DialogInterface.OnClickListener positiveOnClick,
                           String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialogScore(Activity activity, int img, String title, String msg, String positiveLabel,
                           DialogInterface.OnClickListener positiveOnClick,
                           String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(img);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(isCancelAble);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
