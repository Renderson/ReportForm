package com.rendersoncs.reportform.leaks;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MainApplication extends Application {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        if (LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context){
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }

}


