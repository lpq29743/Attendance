package com.attendance.courses;

import com.attendance.AttendanceApplication;
import com.attendance.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by peiqin on 3/5/2017.
 */

public class GetCoursesSubscriber extends Subscriber<Boolean> {

    private CoursesContract.View view;

    public GetCoursesSubscriber(CoursesContract.View view) {
        this.view = view;
    }

    @Override
    public void onStart() {
        view.startRefresh();
    }

    @Override
    public void onCompleted() {
        view.stopRefresh();
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    @Override
    public void onError(Throwable e) {

        if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
            view.showTip(AttendanceApplication.getContext().getString(R.string.network_corrupt));
        } else {
            view.showTip("error:" + e.getMessage());
        }
        view.stopRefresh();

        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }

    }

    @Override
    public void onNext(Boolean result) {

        if (result) {
            view.getCourseSuccess();
            view.showTip(AttendanceApplication.getContext().getString(R.string.get_success));
        } else {
            view.showTip(AttendanceApplication.getContext().getString(R.string.get_failed));
        }

    }

}
