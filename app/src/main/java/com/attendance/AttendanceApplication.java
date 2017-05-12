package com.attendance;

import android.app.Application;
import android.content.Context;

import com.attendance.data.Course;
import com.attendance.data.DaoMaster;
import com.attendance.data.DaoSession;

import org.greenrobot.greendao.database.Database;

public class AttendanceApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}