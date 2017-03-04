package com.attendance.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.attendance.utils.DBUtil;

/**
 * Created by Administrator on 2016/10/15.
 */
public class AttDetailDao {

    private SQLiteDatabase db;

    public AttDetailDao(Context context) {
        db = DBUtil.getInstance(context);
    }

    /**
     * 查询所有数据
     *
     * @return Cursor
     */
    public Cursor findAll(int course_id) {
        Cursor cursor = db.rawQuery("select * from att_detail where course_id = "
                + course_id + " order by id desc", null);
        return cursor;
    }

    /**
     * 添加数据
     */
    public void insert(int id, String begin_time, String end_time, int day_of_week, int course_id, int location_id, String location_name) {
        db.execSQL("insert into att_detail values('" + id + "','" + begin_time + "','"
                + end_time + "','"  + day_of_week + "','" + course_id + "','" + location_id
                + "','" + location_name + "')");
    }

    /**
     * 删除考勤
     *
     * @param id
     */
    public void del(int id) {
        db.execSQL("delete from att_detail where id=" + id);
    }

    /**
     * 删除所有数据
     */
    public void delAll() {
        db.execSQL("delete from att_detail");
    }

//    /**
//     * 根据id查询数据
//     *
//     * @param id
//     * @return Cursor
//     */
//    public Cursor findById(int id) {
//        Cursor cursor = db.rawQuery("select * from course where id=" + id, null);
//        return cursor;
//    }
//
//    /**
//     * 修改数据
//     */
//    public void update(int id, String name) {
//        db.execSQL("update course set name='" + name + "'where id=" + id);
//    }

}
