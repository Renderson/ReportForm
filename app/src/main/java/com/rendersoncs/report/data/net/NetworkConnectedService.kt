package com.rendersoncs.report.data.net

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.rendersoncs.report.infrastructure.util.InternetCheck

class NetworkConnectedService {
    var internetCheck: InternetCheck? = null
    fun isConnected(activity: Activity, func: () -> Unit) {
        val cm = (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            // Download list FireBase
            internetCheck = InternetCheck { internet: Boolean ->
                if (internet) {
                    val async = DownloadJsonFireBaseAsyncTask()
                    async.execute()
                }
            }
        } else { func.invoke() }
    }
}