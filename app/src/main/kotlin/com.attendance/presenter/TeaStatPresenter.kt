package com.attendance.presenter

import com.attendance.AttendanceApplication
import com.attendance.contract.TeaStatContract
import com.attendance.entities.ConstParameter
import com.attendance.http.HttpMethods
import com.attendance.subscribers.GetStatSubscriber
import com.attendance.utils.NetWorkUtil

/**
 * Created by peiqin on 3/4/2017.
 */

class TeaStatPresenter(private val view: TeaStatContract.View) : TeaStatContract.Presenter {

    init {
        this.view.setPresenter(this)
    }

    override fun getStatList() {

        val netStatus = NetWorkUtil().checkNetWorkEx(AttendanceApplication.context)
        if (!netStatus) {
            view.showTip(ConstParameter.NETWORK_CORRUPT)
            return
        }

        HttpMethods.Companion.instance.getStatList(GetStatSubscriber(view))

    }

}