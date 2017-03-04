package com.attendance.entities;

/**
 * Created by Administrator on 2016/8/31.
 */
public class CourseTeaBean {

    private int id;  //课程id
    private String name;  //课程名称
    private String teacherName; //教师名称

    public CourseTeaBean() {

    }

    public CourseTeaBean(int id, String name, String teacherName) {
        this.id = id;
        this.name = name;
        this.teacherName = teacherName;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

}
