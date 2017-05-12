package com.attendance.login

import com.attendance.AttendanceApplication
import com.attendance.R
import com.attendance.login.LoginContract

import java.net.ConnectException
import java.net.SocketTimeoutException

import rx.Subscriber

/**
 * Created by peiqin on 3/5/2017.
 */

class LoginSubscriber(private val presenter: LoginContract.Presenter, private val view: LoginContract.View) : Subscriber<Boolean>() {

    override fun onStart() {
        view.showProgress(AttendanceApplication.context.getString(R.string.login_loading))
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
            view.showTip(AttendanceApplication.context.getString(R.string.network_corrupt))
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
            view.showTip(AttendanceApplication.context.getString(R.string.login_success))
            view.startMainActivity()
        } else {
            view.showTip(AttendanceApplication.context.getString(R.string.login_failed))
        }

    }

}
