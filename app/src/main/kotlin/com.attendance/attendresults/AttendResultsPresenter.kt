package com.attendance.attendresults

import com.attendance.AttendanceApplication
import com.attendance.R
import com.attendance.data.source.remote.AttendResultsRemoteDataSource
import com.attendance.attendresults.GetAttendResultsSubscriber
import com.attendance.utils.NetWorkUtil

/**
 * Created by peiqin on 3/4/2017.
 */

class AttendResultsPresenter(private val view: AttendResultsContract.View) : AttendResultsContract.Presenter {

    init {
        this.view.setPresenter(this)
    }

    override fun getAttendResultList() {

        val netStatus = NetWorkUtil().checkNetWorkEx(AttendanceApplication.context)
        if (!netStatus) {
            view.showTip(AttendanceApplication.context.getString(R.string.network_corrupt))
            return
        }

        AttendResultsRemoteDataSource.Companion.instance.getAttendResultList(GetAttendResultsSubscriber(view))

    }

}