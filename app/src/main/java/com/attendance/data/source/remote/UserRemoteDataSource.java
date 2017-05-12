package com.attendance.data.source.remote;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peiqin on 3/5/2017.
 */

public class UserRemoteDataSource {

    private UserRemoteDataSource() {
    }

    private static class SingletonHolder {
        private static final UserRemoteDataSource INSTANCE = new UserRemoteDataSource();
    }

    public static UserRemoteDataSource getInstance() {
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

}
