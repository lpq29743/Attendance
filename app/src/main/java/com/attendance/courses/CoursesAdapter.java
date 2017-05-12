package com.attendance.courses;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.attendance.R;
import com.attendance.data.source.local.CoursesLocalDataSource;
import com.attendance.data.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class CoursesAdapter extends BaseAdapter {
    private List<Course> courseList;
    private Context context;
    private CoursesLocalDataSource coursesLocalDataSource;

    public CoursesAdapter(Context context) {
        this.context = context;
        courseList = new ArrayList<>();
        coursesLocalDataSource = new CoursesLocalDataSource(context);
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
        Course item = courseList.get(position);
        String name = item.getName();
        vh.name.setText(name);
        return convertView;
    }

    public void setCourseList() {
        courseList = coursesLocalDataSource.findAll();
    }

    private class ViewHolder {
        public TextView name;
    }
}
