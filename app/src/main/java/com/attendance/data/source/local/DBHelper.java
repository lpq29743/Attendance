package com.attendance.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.attendance.data.DaoMaster;

/**
 * Created by peiqin on 5/12/2017.
 */

public class DBHelper {

    private final static String dbName = "attendance";
    private static DBHelper mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBHelper(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBHelper.class) {
                if (mInstance == null) {
                    mInstance = new DBHelper(context);
                }
            }
        }
        return mInstance;
    }

    public SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    public SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

}
