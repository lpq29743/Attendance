package com.attendance.http;

import com.attendance.AttendanceApplication;
import com.attendance.dao.CourseDao;
import com.attendance.entities.ResultBean;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peiqin on 3/5/2017.
 */

public class HttpMethods {

    // 构造方法私有
    private HttpMethods() {

    }

    // 在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    // 获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void login(Subscriber<Boolean> subscriber, String username, String password, boolean isTeacher) {

        boolean result = false;
        if (username.equals("1") && password.equals("1") && isTeacher) {
            result = true;
        }
        Observable observable = Observable.just(result);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    public void getCourseList(Subscriber<Boolean> subscriber) {

        CourseDao courseDao = new CourseDao(AttendanceApplication.getContext());
        courseDao.delAll();
        for (int i = 0; i < 5; i++) {
            courseDao.insert(i, "course " + i);
        }

        boolean result = true;
        Observable observable = Observable.just(result);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    public void getStatList(Subscriber<ResultBean> subscriber) {

        List<ResultBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new ResultBean("student" + "", i + "", i + "", i + "", i + ""));
        }

        Observable observable = Observable.from(list);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

}
