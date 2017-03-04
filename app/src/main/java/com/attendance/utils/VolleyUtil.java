package com.attendance.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyUtil {
    private static VolleyUtil mInstance;//一个实例
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private VolleyUtil(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    //异步获取实例
    public static synchronized VolleyUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtil(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        getRequestQueue().start();
    }
}
