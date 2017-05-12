package com.attendance.courses

import com.attendance.AttendanceApplication
import com.attendance.R
import com.attendance.courses.CoursesContract

import java.net.ConnectException
import java.net.SocketTimeoutException

import rx.Subscriber

/**
 * Created by peiqin on 3/5/2017.
 */

class GetCoursesSubscriber(private val view: CoursesContract.View) : Subscriber<Boolean>() {

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
            view.showTip(AttendanceApplication.context.getString(R.string.network_corrupt))
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
            view.showTip(AttendanceApplication.context.getString(R.string.get_success))
        } else {
            view.showTip(AttendanceApplication.context.getString(R.string.get_failed))
        }

    }

}
