package com.attendance.data

/**
 * Created by peiqin on 5/12/2017.
 */
class Course {
    var id: Int? = 0
    var name: String? = null

    constructor(id: Int?, name: String) {
        this.id = id
        this.name = name
    }

    constructor() {}
}
