package com.rendersoncs.reportform.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.graphics.ColorUtils;

import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.rendersoncs.reportform.itens.Repo;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TakePhotoReport {
    public Context context;
    Activity activity;
    Repo repo;
    ImageView imageView;

    public void testToast(Context context, int position) {

        Toast.makeText(context, "Test Camera " + position, Toast.LENGTH_SHORT).show();
    }
}
