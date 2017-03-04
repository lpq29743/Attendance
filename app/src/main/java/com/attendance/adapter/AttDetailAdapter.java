package com.attendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.attendance.R;
import com.attendance.dao.AttDetailDao;
import com.attendance.entities.AttDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peiqin on 10/15/2016.
 */
public class AttDetailAdapter extends BaseAdapter {
    private List<AttDetailBean> attDetailList;
    private Context context;
    private AttDetailDao attDetailDao;

    public AttDetailAdapter(Context context) {
        this.context = context;
        attDetailList = new ArrayList<>();
        attDetailDao = new AttDetailDao(context);
    }

    @Override
    public int getCount() {
        return attDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return attDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_att_detail, null);
            vh = new ViewHolder();
            vh.mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
            vh.mDayTv = (TextView) convertView.findViewById(R.id.day_tv);
            vh.mLocationTv = (TextView) convertView.findViewById(R.id.location_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        AttDetailBean item = attDetailList.get(position);
        String time = item.getBeginTime() + "-" + item.getEndTime();
        String day = "";
        switch (item.getDayOfWeek()) {
            case 1:
                day = "星期一";
                break;
            case 2:
                day = "星期二";
                break;
            case 3:
                day = "星期三";
                break;
            case 4:
                day = "星期四";
                break;
            case 5:
                day = "星期五";
                break;
            case 6:
                day = "星期六";
                break;
            case 7:
                day = "星期日";
                break;
        }
        String location = item.getLocationName();
        vh.mTimeTv.setText(time);
        vh.mDayTv.setText(day);
        vh.mLocationTv.setText(location);
        return convertView;
    }

    // 读取本地数据库设置数据
    public void setAttDetailList(int course_id) {
        attDetailList.clear();
        // 查询所有数据
        Cursor cursor = attDetailDao.findAll(course_id);
        while (cursor.moveToNext()) {
            AttDetailBean attDetailBean = new AttDetailBean();
            attDetailBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            attDetailBean.setBeginTime(cursor.getString(cursor.getColumnIndex("begin_time")));
            attDetailBean.setEndTime(cursor.getString(cursor.getColumnIndex("end_time")));
            attDetailBean.setDayOfWeek(cursor.getInt(cursor.getColumnIndex("day_of_week")));
            attDetailBean.setCourseId(cursor.getInt(cursor.getColumnIndex("course_id")));
            attDetailBean.setLocationId(cursor.getInt(cursor.getColumnIndex("location_id")));
            attDetailBean.setLocationName(cursor.getString(cursor.getColumnIndex("location_name")));
            attDetailList.add(attDetailBean);
        }
    }

    private class ViewHolder {
        public TextView mTimeTv, mDayTv, mLocationTv;
    }

}