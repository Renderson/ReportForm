package com.rendersoncs.reportform.view.services.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;
import com.rendersoncs.reportform.R;

public class SnackBarHelper {

    public static void configSnackBar(Context context, Snackbar snack){
        addMargins(snack);
        setRoundBordersBg(context, snack);
        ViewCompat.setElevation(snack.getView(), 6f);
    }

    private static void addMargins (Snackbar snackbar) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            params.setMargins(12, 12, 12, 12);
        }
        params.setMargins(8, 8, 8, 8);
        snackbar.getView().setLayoutParams(params);
    }

    private static void setRoundBordersBg (Context context, Snackbar snackbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.getView().setBackground(context.getDrawable(R.drawable.bg_snackbar));
        }
    }

    public void showSnackBar(Activity activity, int id, int label) {
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(id),
                        activity.getString(label), Snackbar.LENGTH_LONG);
        SnackBarHelper.configSnackBar(activity, snackbar);
        snackbar.show();
    }
}
