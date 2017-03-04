package com.attendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.attendance.R;
import com.attendance.entities.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class LocationAdapter extends BaseAdapter {
    private List<LocationBean> locationList;
    private Context context;

    public LocationAdapter(Context context) {
        this.context = context;
        locationList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_location, null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.location_name_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        LocationBean item = locationList.get(position);
        String name = item.getName();
        vh.name.setText(name);
        return convertView;
    }

    // 读取本地数据库设置数据
    public void setLocationList(List<LocationBean> list) {
        locationList = list;
    }

    private class ViewHolder {
        public TextView name;
    }
}
