package com.attendance.subscribers

import com.attendance.contract.MainContract
import com.attendance.entities.ConstParameter

import java.net.ConnectException
import java.net.SocketTimeoutException

import rx.Subscriber

/**
 * Created by peiqin on 3/5/2017.
 */

class GetCourseSubscriber(private val view: MainContract.View) : Subscriber<Boolean>() {

    override fun onStart() {
        view.startRefresh()
    }

    override fun onCompleted() {
        view.stopRefresh()
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
        view.stopRefresh()

        if (!this.isUnsubscribed) {
            this.unsubscribe()
        }

    }

    override fun onNext(result: Boolean?) {

        if (result!!) {
            view.getCourseSuccess()
            view.showTip(ConstParameter.GET_SUCCESS)
        } else {
            view.showTip(ConstParameter.GET_FAILED)
        }

    }

}
