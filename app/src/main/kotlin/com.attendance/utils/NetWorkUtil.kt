package com.attendance.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetWorkUtil {

    /**
     * 检测当前是否有网络
     * @param context
     * *
     * @return true 有网络，false 无网络
     */
    fun checkNetWorkEx(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkinfo = manager.activeNetworkInfo
        if (networkinfo == null || !networkinfo.isAvailable) {
            return false
        } else if (networkinfo.type == ConnectivityManager.TYPE_WIFI) {
            return true
        } else if (networkinfo.type == ConnectivityManager.TYPE_MOBILE) {
            return true
        }
        return false
    }

}
