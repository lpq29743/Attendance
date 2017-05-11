package com.attendance.subscribers

import com.attendance.contract.TeaStatContract
import com.attendance.entities.ConstParameter
import com.attendance.entities.ResultBean

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.ArrayList

import rx.Subscriber

/**
 * Created by peiqin on 3/5/2017.
 */

class GetStatSubscriber(private val view: TeaStatContract.View) : Subscriber<ResultBean>() {
    private var list: MutableList<ResultBean> = ArrayList<ResultBean>()

    override fun onStart() {
    }

    override fun onCompleted() {
        view.getStatSuccess(list)
        view.showTip(ConstParameter.GET_SUCCESS)
        if (!this.isUnsubscribed) {
            this.unsubscribe()
        }
    }

    override fun onError(e: Throwable) {
        if (e is SocketTimeoutException || e is ConnectException) {
            view.showTip(ConstParameter.NETWORK_CORRUPT)
        } else {
            view.showTip("error:" + e.message)
        }

        if (!this.isUnsubscribed) {
            this.unsubscribe()
        }
    }

    override fun onNext(resultBean: ResultBean) {
        list.add(resultBean)
    }

}
