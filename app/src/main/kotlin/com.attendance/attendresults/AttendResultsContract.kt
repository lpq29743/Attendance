package com.attendance.attendresults

import com.attendance.BasePresenter
import com.attendance.BaseView
import com.attendance.data.AttendResult

/**
 * Created by peiqin on 3/4/2017.
 */

interface AttendResultsContract {

    interface View : BaseView<Presenter> {

        fun showTip(tip: String)

        fun getAttendResultsSuccess(list: List<AttendResult>)

    }

    interface Presenter : BasePresenter {

        fun getAttendResultList()

    }

}
