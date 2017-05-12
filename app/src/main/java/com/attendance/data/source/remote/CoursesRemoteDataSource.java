package com.attendance.data.source.remote;

import com.attendance.AttendanceApplication;
import com.attendance.data.Course;
import com.attendance.data.source.local.CoursesLocalDataSource;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peiqin on 3/5/2017.
 */

public class CoursesRemoteDataSource {

    private CoursesRemoteDataSource() {
    }

    private static class SingletonHolder {
        private static final CoursesRemoteDataSource INSTANCE = new CoursesRemoteDataSource();
    }

    public static CoursesRemoteDataSource getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void getCourseList(Subscriber<Boolean> subscriber) {

        CoursesLocalDataSource coursesLocalDataSource = new CoursesLocalDataSource(AttendanceApplication.getContext());
        coursesLocalDataSource.delAll();
        for (int i = 0; i < 5; i++) {
            Course course = new Course(new Long(i), "course " + i);
            coursesLocalDataSource.insert(course);
        }

        boolean result = true;
        Observable observable = Observable.just(result);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

}
