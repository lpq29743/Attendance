package com.attendance.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import com.attendance.utils.DBUtil

/**
 * Created by peiqin on 9/18/2016.
 */
class CourseDao(context: Context) {

    private val db: SQLiteDatabase

    init {
        db = DBUtil.getInstance(context)
    }

    /**
     * 查询所有数据

     * @return Cursor
     */
    fun findAll(): Cursor {
        val cursor = db.rawQuery("select * from course order by id desc", null)
        return cursor
    }

    /**
     * 添加数据
     */
    fun insert(id: Int, name: String) {
        db.execSQL("insert into course values('$id','$name')")
    }

    /**
     * 删除数据
     */
    fun delAll() {
        db.execSQL("delete from course")
    }

}
