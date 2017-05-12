package com.attendance.data.source.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.attendance.data.Course
import com.attendance.utils.DBHelper

/**
 * Created by peiqin on 9/18/2016.
 */
class CoursesLocalDataSource(context: Context) {

    private val db: SQLiteDatabase

    init {
        db = DBHelper.getInstance(context)
    }

    fun findAll(): Cursor {
        val cursor = db.rawQuery("select * from course order by id desc", null)
        return cursor
    }

    fun insert(id: Int, name: String) {
        db.execSQL("insert into course values('$id','$name')")
    }

    fun delAll() {
        db.execSQL("delete from course")
    }

}