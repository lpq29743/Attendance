package com.attendance.data.source.remote

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by peiqin on 3/5/2017.
 */

class UserRemoteDataSource private constructor() {

    private object SingletonHolder {
        val INSTANCE = UserRemoteDataSource()
    }

    fun login(subscriber: Subscriber<Boolean>, username: String, password: String, isTeacher: Boolean) {

        var result = false
        if (username == "1" && password == "1" && isTeacher) {
            result = true
        }
        val observable = Observable.just(result)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }

    companion object {

        val instance: UserRemoteDataSource
            get() = SingletonHolder.INSTANCE
    }

}