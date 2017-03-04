package com.attendance.entities;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/31.
 */
public class LocationBean {

    private int id;  //地点id
    private String name;  //地点名称

    public LocationBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
