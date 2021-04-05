//package com.rendersoncs.reportform.view.activitys.login;
//
//import android.app.Application;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import com.twitter.sdk.android.Twitter;
//import com.twitter.sdk.android.core.TwitterAuthConfig;
//
//import io.fabric.sdk.android.Fabric;
//
//public class CustomApplication extends Application {
//
//    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
//    private static final String TWITTER_KEY = "AKEDlMTtiYboJcurTrGjqw9bY";
//    private static final String TWITTER_SECRET = "IX7tp4PEAeLwBAl7Q8NmQWA9poSpurrnn1n7RFLSBFTwCeUDJq";
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
//    }
//}
