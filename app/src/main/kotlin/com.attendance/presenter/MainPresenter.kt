package com.attendance.presenter

import com.attendance.AttendanceApplication
import com.attendance.contract.MainContract
import com.attendance.entities.ConstParameter
import com.attendance.http.HttpMethods
import com.attendance.subscribers.GetCourseSubscriber
import com.attendance.utils.NetWorkUtil

/**
 * Created by peiqin on 3/4/2017.
 */

class MainPresenter(private val view: MainContract.View) : MainContract.Presenter {

    init {
        this.view.setPresenter(this)
    }

    override fun getCourseList() {

        val netStatus = NetWorkUtil().checkNetWorkEx(AttendanceApplication.context)
        if (!netStatus) {
            view.showTip(ConstParameter.NETWORK_CORRUPT)
            view.stopRefresh()
            return
        }

        HttpMethods.Companion.instance.getCourseList(GetCourseSubscriber(view))

    }

}