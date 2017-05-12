package com.attendance.courses

import com.attendance.BasePresenter
import com.attendance.BaseView

/**
 * Created by peiqin on 3/4/2017.
 */

interface CoursesContract {

    interface View : BaseView<Presenter> {

        fun showTip(tip: String)

        fun startRefresh()

        fun stopRefresh()

        fun getCourseSuccess()

    }

    interface Presenter : BasePresenter {

        fun getCourseList()

    }

}
