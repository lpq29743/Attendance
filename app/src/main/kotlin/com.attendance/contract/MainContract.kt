package com.attendance.contract

import com.attendance.BasePresenter
import com.attendance.BaseView

/**
 * Created by peiqin on 3/4/2017.
 */

interface MainContract {

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
