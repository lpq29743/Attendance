package com.attendance.data.source.remote

import com.attendance.AttendanceApplication
import com.attendance.data.Course
import com.attendance.data.source.local.CoursesLocalDataSource

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by peiqin on 3/5/2017.
 */

class CoursesRemoteDataSource private constructor() {

    private object SingletonHolder {
        val INSTANCE = CoursesRemoteDataSource()
    }

    fun getCourseList(subscriber: Subscriber<Boolean>) {

        val coursesLocalDataSource = CoursesLocalDataSource(AttendanceApplication.context)
        coursesLocalDataSource.delAll()
        for (i in 0..4) {
            coursesLocalDataSource.insert(i, "course " + i)
        }

        val result = true
        val observable = Observable.just(result)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }

    companion object {

        val instance: CoursesRemoteDataSource
            get() = SingletonHolder.INSTANCE
    }

}