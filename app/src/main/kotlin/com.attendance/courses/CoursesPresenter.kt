package com.attendance.courses

import com.attendance.AttendanceApplication
import com.attendance.R
import com.attendance.courses.CoursesContract
import com.attendance.courses.GetCoursesSubscriber
import com.attendance.data.source.local.CoursesLocalDataSource
import com.attendance.data.source.remote.CoursesRemoteDataSource
import com.attendance.utils.NetWorkUtil

/**
 * Created by peiqin on 3/4/2017.
 */

class CoursesPresenter(private val view: CoursesContract.View) : CoursesContract.Presenter {

    init {
        this.view.setPresenter(this)
    }

    override fun getCourseList() {

        val netStatus = NetWorkUtil().checkNetWorkEx(AttendanceApplication.context)
        if (!netStatus) {
            view.showTip(AttendanceApplication.context.getString(R.string.network_corrupt))
            view.stopRefresh()
            return
        }

        CoursesRemoteDataSource.Companion.instance.getCourseList(GetCoursesSubscriber(view))

    }

}