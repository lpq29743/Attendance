package com.attendance.data.source.remote;

import com.attendance.data.AttendResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peiqin on 3/5/2017.
 */

public class AttendResultsRemoteDataSource {

    private AttendResultsRemoteDataSource() {
    }

    private static class SingletonHolder {
        private static final AttendResultsRemoteDataSource INSTANCE = new AttendResultsRemoteDataSource();
    }

    public static AttendResultsRemoteDataSource getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void getAttendResultList(Subscriber<AttendResult> subscriber) {

        List<AttendResult> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new AttendResult("student" + "", i + "", i + "", i + "", i + ""));
        }

        Observable observable = Observable.from(list);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

}
