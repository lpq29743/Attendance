package com.attendance.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by peiqin on 2016/8/31.
 */
@Entity
public class Course {
    @Id
    private Long id;
    private String name;
    @Generated(hash = 831126491)
    public Course(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 1355838961)
    public Course() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
