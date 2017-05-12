package com.attendance.data.source.local;

import android.content.Context;

import com.attendance.data.Course;
import com.attendance.data.CourseDao;
import com.attendance.data.DaoMaster;
import com.attendance.data.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by peiqin on 9/18/2016.
 */
public class CoursesLocalDataSource {

    private CourseDao courseDao;

    public CoursesLocalDataSource(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        DaoMaster daoMaster = new DaoMaster(dbHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        courseDao = daoSession.getCourseDao();
    }

    public List<Course> findAll() {
        QueryBuilder<Course> qb = courseDao.queryBuilder();
        List<Course> list = qb.list();
        return list;
    }

    public void insert(Course course) {
        courseDao.insert(course);
    }

    public void delAll() {
        courseDao.deleteAll();
    }

}
