package com.attendance.http

import com.attendance.AttendanceApplication
import com.attendance.dao.CourseDao
import com.attendance.entities.ResultBean

import java.util.ArrayList

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by peiqin on 3/5/2017.
 */

class HttpMethods
private constructor() {

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

    fun getCourseList(subscriber: Subscriber<Boolean>) {

        val courseDao = CourseDao(AttendanceApplication.context)
        courseDao.delAll()
        for (i in 0..4) {
            courseDao.insert(i, "course " + i)
        }

        val result = true
        val observable = Observable.just(result)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }

    fun getStatList(subscriber: Subscriber<ResultBean>) {

        val list = ArrayList<ResultBean>()
        for (i in 0..4) {
            list.add(ResultBean("student" + "", i.toString() + "", i.toString() + "", i.toString() + "", i.toString() + ""))
        }

        val observable = Observable.from(list)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }

    // 在访问HttpMethods时创建单例
    private object SingletonHolder {
        val INSTANCE = HttpMethods()
    }

    companion object {

        // 获取单例
        val instance: HttpMethods
            get() = SingletonHolder.INSTANCE
    }

}
