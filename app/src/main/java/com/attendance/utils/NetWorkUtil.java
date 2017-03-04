package com.attendance.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

	public NetWorkUtil() {
		super();
	}
    
    /**
     * 检测当前是否有网络
     * @param context
     * @return true 有网络，false 无网络
     */
    public boolean checkNetWorkEx(Context context){
    	ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();    
        if (networkinfo == null || !networkinfo.isAvailable()) {
        	return false;
        } else if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
        	return true;
        } else if (networkinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        	return true;
        }
        return false; 
    }

}
