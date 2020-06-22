package com.rendersoncs.reportform.repository.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.google.android.material.snackbar.Snackbar;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.view.services.util.InternetCheck;
import com.rendersoncs.reportform.view.services.util.SnackBarHelper;

public class NetworkConnectedService {
        InternetCheck internetCheck;

    public void isConnected(Activity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Download list FireBase
            internetCheck = new InternetCheck(internet -> {
                if (internet){
                    DownloadJsonFireBaseAsyncTask async = new DownloadJsonFireBaseAsyncTask();
                    async.execute();
                }
            });
        } else {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.floatButton), activity.getString(R.string.txt_check_networking), Snackbar.LENGTH_LONG)
                    .setAction(activity.getResources().getString(R.string.check), view -> {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            activity.startActivity(intent);
                        });
            SnackBarHelper.configSnackBar(activity, snackbar);
            snackbar.show();
        }
    }
}
