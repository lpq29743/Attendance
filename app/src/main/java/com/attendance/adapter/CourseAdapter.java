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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class CourseAdapter extends BaseAdapter {
    private List<CourseBean> courseList;
    private Context context;
    private CourseDao courseDao;

    public CourseAdapter(Context context) {
        this.context = context;
        courseList = new ArrayList<>();
        courseDao = new CourseDao(context);
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course, null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.course_name_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        CourseBean item = courseList.get(position);
        String name = item.getName();
        vh.name.setText(name);
        return convertView;
    }

    // 读取本地数据库设置数据
    public void setCourseList() {
        courseList.clear();
        // 查询所有数据
        Cursor cursor = courseDao.findAll();
        while (cursor.moveToNext()) {
            CourseBean course = new CourseBean();
            course.setId(cursor.getInt(cursor.getColumnIndex("id")));
            course.setName(cursor.getString(cursor.getColumnIndex("name")));
            courseList.add(course);
        }
    }

    private class ViewHolder {
        public TextView name;
    }
}
