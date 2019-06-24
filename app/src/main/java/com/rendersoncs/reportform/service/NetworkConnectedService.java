package com.rendersoncs.reportform.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.google.android.material.snackbar.Snackbar;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.async.DownloadJsonFireBaseAsyncTask;
import com.rendersoncs.reportform.util.SnackBarHelper;

public class NetworkConnectedService {
    private Context context;

    public NetworkConnectedService(Context context){
        this.context = context;
    }

    public void isConnected(Activity activity){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Download list FireBase
            DownloadJsonFireBaseAsyncTask async = new DownloadJsonFireBaseAsyncTask(context);
            async.execute();
        } else {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.floatButton), activity.getString(R.string.txt_check_networking), Snackbar.LENGTH_LONG)
                    .setAction("VERIFICAR", view -> {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            activity.startActivity(intent);
                        });
            SnackBarHelper.configSnackBar(activity, snackbar);
            snackbar.show();
        }
    }
}
