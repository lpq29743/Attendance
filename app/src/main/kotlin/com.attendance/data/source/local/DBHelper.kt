package com.attendance.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper private constructor(context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val create_course_table_sql = "create table course(id integer primary key, name varchar(60) not null);"
            db.execSQL(create_course_table_sql)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (newVersion) {
            2 -> {
            }
            else -> {
            }
        }
        val drop_course_table_sql = "drop table if exists course;"
        db.execSQL(drop_course_table_sql)
        onCreate(db)
    }

    companion object {
        private var dbHelper: DBHelper? = null

        fun getInstance(context: Context): SQLiteDatabase {
            if (dbHelper == null) {
                dbHelper = DBHelper(context, "attendance", 1)
            }
            return dbHelper!!.readableDatabase
        }
    }

}
