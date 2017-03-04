package com.attendance.activity;

import android.app.Application;
import android.content.Context;

import com.attendance.exception.CrashHandler;
import com.attendance.utils.SharedFileUtil;

public class AttendanceApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }

}