package com.attendance.contract

import com.attendance.BasePresenter
import com.attendance.BaseView

/**
 * Created by peiqin on 3/4/2017.
 */

interface SplashContract {

    interface View : BaseView<Presenter> {

        fun startLoginActivity()

        fun startMainActivity()

    }

    interface Presenter : BasePresenter {

        fun startTimeTask()

    }

}
