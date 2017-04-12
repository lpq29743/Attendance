package com.attendance.contract

import com.attendance.BasePresenter
import com.attendance.BaseView
import com.attendance.entities.ResultBean

/**
 * Created by peiqin on 3/4/2017.
 */

interface TeaStatContract {

    interface View : BaseView<Presenter> {

        fun showTip(tip: String)

        fun getStatSuccess(list: List<ResultBean>)

    }

    interface Presenter : BasePresenter {

        fun getStatList()

    }

}
