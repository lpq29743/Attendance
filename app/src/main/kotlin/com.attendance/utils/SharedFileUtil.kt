package com.attendance.utils

import android.content.Context
import android.content.SharedPreferences

import com.attendance.AttendanceApplication

class SharedFileUtil {

    private val sp: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sp = AttendanceApplication.context.getSharedPreferences("Attendance", Context.MODE_PRIVATE)
        editor = sp.edit()
    }

    fun putString(key: String?, value: String) {
        if (key == null || key == "")
            throw IllegalArgumentException("Key can't be null or empty string")
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String?): String {
        if (key == null || key == "")
            throw IllegalArgumentException("Key can't be null or empty string")
        return sp.getString(key, "")
    }

    fun getInt(key: String): Int {
        return sp.getInt(key, 0)
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun putBoolean(key: String?, value: Boolean) {
        if (key == null || key == "")
            throw IllegalArgumentException("Key can't be null or empty string")
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key: String): Boolean {
        return sp.getBoolean(key, false)
    }

    fun isContainsKey(key: String): Boolean {
        return sp.contains(key)
    }

    fun getLong(key: String): Long {
        return sp.getLong(key, 0)
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

}
