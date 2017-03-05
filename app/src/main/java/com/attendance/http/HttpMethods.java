package com.attendance.http;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

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

    public void login(Subscriber<List<Subject>> subscriber, String username, String password, boolean isTeacher) {

        boolean result = false;
        if (username.equals("1") && password.equals("1") && isTeacher) {
            result = true;
        } else if (username.equals("2") && password.equals("2") && !isTeacher) {
            result = true;
        }
        Observable observable = Observable.just(result);
        toSubscribe(observable, subscriber);

    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

}
