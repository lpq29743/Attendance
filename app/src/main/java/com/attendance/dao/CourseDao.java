package com.attendance.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.attendance.utils.DBUtil;

/**
 * Created by peiqin on 9/18/2016.
 */
public class CourseDao {

    private SQLiteDatabase db;

    public CourseDao(Context context) {
        db = DBUtil.getInstance(context);
    }

    /**
     * 查询所有数据
     *
     * @return Cursor
     */
    public Cursor findAll() {
        Cursor cursor = db.rawQuery("select * from course order by id desc", null);
        return cursor;
    }

    /**
     * 添加数据
     */
    public void insert(int id, String name, String teacherName) {
        db.execSQL("insert into course values('" + id + "','" + name + "','" + teacherName +"')");
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public void del(int id) {
        db.execSQL("delete from course where id=" + id);
    }

    /**
     * 删除数据
     */
    public void delAll() {
        db.execSQL("delete from course");
    }

    /**
     * 修改数据
     */
    public void update(int id, String name) {
        db.execSQL("update course set name='" + name + "'where id=" + id);
    }

}
