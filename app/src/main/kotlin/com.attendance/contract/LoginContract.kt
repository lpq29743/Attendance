package com.attendance.contract

import com.attendance.BasePresenter
import com.attendance.BaseView

/**
 * Created by peiqin on 3/4/2017.
 */

interface LoginContract {

    interface View : BaseView<Presenter> {

        fun showTip(tip: String)

        fun showProgress(msg: String)

        fun cancelProgress()

        fun startMainActivity()

    }

    interface Presenter : BasePresenter {

        fun login(username: String, password: String, isTeacher: Boolean, isRemPassword: Boolean)

        fun writeData()

    }

}
