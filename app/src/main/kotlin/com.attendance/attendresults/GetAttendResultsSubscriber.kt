package com.attendance.attendresults

import android.app.Application
import com.attendance.AttendanceApplication
import com.attendance.R
import com.attendance.data.AttendResult

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.ArrayList

import rx.Subscriber

/**
 * Created by peiqin on 3/5/2017.
 */

class GetAttendResultsSubscriber(private val view: AttendResultsContract.View) : Subscriber<AttendResult>() {
    private var list: MutableList<AttendResult> = ArrayList<AttendResult>()

    override fun onStart() {
    }

    override fun onCompleted() {
        view.getAttendResultsSuccess(list)
        view.showTip(AttendanceApplication.context.getString(R.string.get_success))
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

        if (!this.isUnsubscribed) {
            this.unsubscribe()
        }
    }

    override fun onNext(attendResult: AttendResult) {
        list.add(attendResult)
    }

}
