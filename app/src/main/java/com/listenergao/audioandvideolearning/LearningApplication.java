package com.listenergao.audioandvideolearning;

import android.app.Application;
import android.content.Context;

public class LearningApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }


}
