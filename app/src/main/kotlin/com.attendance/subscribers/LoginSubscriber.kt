package com.attendance.subscribers

import com.attendance.contract.LoginContract
import com.attendance.entities.ConstParameter

import java.net.ConnectException
import java.net.SocketTimeoutException

import rx.Subscriber

/**
 * Created by peiqin on 3/5/2017.
 */

class LoginSubscriber(private val presenter: LoginContract.Presenter, private val view: LoginContract.View) : Subscriber<Boolean>() {

    override fun onStart() {
        view.showProgress(ConstParameter.LOGIN_LOADING)
    }

    override fun onCompleted() {
        view.cancelProgress()
        presenter.writeData()
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
        view.cancelProgress()

        if (!this.isUnsubscribed) {
            this.unsubscribe()
        }

    }

    override fun onNext(result: Boolean?) {

        if (result!!) {
            view.showTip(ConstParameter.LOGIN_SUCCESS)
            view.startMainActivity()
        } else {
            view.showTip(ConstParameter.LOGIN_FAILED)
        }

    }

}
