package com.attendance.entities;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/31.
 */
public class CourseBean {

    private int id;  //课程id
    private String name;  //课程名称

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
