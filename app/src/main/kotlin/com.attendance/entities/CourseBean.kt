package com.attendance.entities

/**
 * Created by Administrator on 2016/8/31.
 */
class CourseBean {

    var id: Int = 0  //课程id
    var name: String? = null  //课程名称

    constructor() {

    }

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

}
