package com.attendance.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBUtil private constructor(context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {

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
        private var dbUtil: DBUtil? = null

        fun getInstance(context: Context): SQLiteDatabase {
            if (dbUtil == null) {
                // 指定数据库名为attendance，需修改时在此修改；此处使用默认工厂；指定版本为1
                dbUtil = DBUtil(context, "attendance", 1)
            }
            return dbUtil!!.readableDatabase
        }
    }

}

