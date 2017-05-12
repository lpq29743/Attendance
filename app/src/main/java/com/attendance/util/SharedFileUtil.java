package com.attendance.util;

import com.attendance.AttendanceApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedFileUtil {

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharedFileUtil() {
		sp = AttendanceApplication.getContext().getSharedPreferences("Attendance", Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public void putString(String key, String value) {
		if (key == null || key.equals(""))
			throw new IllegalArgumentException("Key can't be null or empty string");
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key) {
		if (key == null || key.equals(""))
			throw new IllegalArgumentException("Key can't be null or empty string");
		return sp.getString(key, "");
	}

	public int getInt(String key) {
		return sp.getInt(key, 0);
	}

	public void putInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void putBoolean(String key, boolean value) {
		if (key == null || key.equals(""))
			throw new IllegalArgumentException("Key can't be null or empty string");
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key) {
		return sp.getBoolean(key, false);
	}

	public boolean isContainsKey(String key) {
		return sp.contains(key);
	}

	public long getLong(String key) {
		return sp.getLong(key, 0);
	}

	public void putLong(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

}
