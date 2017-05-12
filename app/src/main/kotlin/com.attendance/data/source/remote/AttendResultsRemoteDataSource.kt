package com.attendance.data.source.remote

import com.attendance.data.AttendResult

import java.util.ArrayList

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by peiqin on 3/5/2017.
 */

class AttendResultsRemoteDataSource private constructor() {

    private object SingletonHolder {
        val INSTANCE = AttendResultsRemoteDataSource()
    }

    fun getAttendResultList(subscriber: Subscriber<AttendResult>) {

        val list = ArrayList<AttendResult>()
        for (i in 0..4) {
            list.add(AttendResult("student" + "", i.toString() + "", i.toString() + "", i.toString() + "", i.toString() + ""))
        }

        val observable = Observable.from(list)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }

    companion object {
        val instance: AttendResultsRemoteDataSource
            get() = SingletonHolder.INSTANCE
    }

}