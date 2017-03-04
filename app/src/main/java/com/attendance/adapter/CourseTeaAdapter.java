package com.attendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.attendance.R;
import com.attendance.dao.CourseDao;
import com.attendance.entities.CourseBean;
import com.attendance.entities.CourseTeaBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class CourseTeaAdapter extends BaseAdapter {
    private List<CourseTeaBean> courseTeaList;
    private Context context;
    private CourseDao courseDao;

    public CourseTeaAdapter(Context context) {
        this.context = context;
        courseTeaList = new ArrayList<>();
        courseDao = new CourseDao(context);
    }

    @Override
    public int getCount() {
        return courseTeaList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseTeaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course_tea, null);
            vh = new ViewHolder();
            vh.mCourseNameTv = (TextView) convertView.findViewById(R.id.course_name_tv);
            vh.mTeacherNameTv = (TextView) convertView.findViewById(R.id.teacher_name_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        CourseTeaBean item = courseTeaList.get(position);
        String courseName = item.getName();
        String teacherName = item.getTeacherName();
        vh.mCourseNameTv.setText(courseName);
        vh.mTeacherNameTv.setText(teacherName);
        return convertView;
    }

    // 根据搜索结果设置数据
    public void setSearchList(List<CourseTeaBean> list) {
        courseTeaList = list;
    }

    // 读取本地数据库设置数据
    public void setCourseList() {
        courseTeaList.clear();
        // 查询所有数据
        Cursor cursor = courseDao.findAll();
        while (cursor.moveToNext()) {
            CourseTeaBean course = new CourseTeaBean();
            course.setId(cursor.getInt(cursor.getColumnIndex("id")));
            course.setName(cursor.getString(cursor.getColumnIndex("name")));
            course.setTeacherName(cursor.getString(cursor.getColumnIndex("teacher_name")));
            courseTeaList.add(course);
        }
    }

    private class ViewHolder {
        public TextView mCourseNameTv, mTeacherNameTv;
    }
}
