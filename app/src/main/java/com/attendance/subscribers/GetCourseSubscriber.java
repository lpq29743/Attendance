package com.attendance.subscribers;

import com.attendance.contract.MainContract;
import com.attendance.entities.ConstParameter;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by peiqin on 3/5/2017.
 */

public class GetCourseSubscriber<Boolean> extends Subscriber<Boolean> {

    private MainContract.View view;

    public GetCourseSubscriber(MainContract.View view) {
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
            view.showTip(ConstParameter.NETWORK_CORRUPT);
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

        if (result.equals("true")) {
            view.getCourseSuccess();
            view.showTip(ConstParameter.GET_SUCCESS);
        } else {
            view.showTip(ConstParameter.GET_FAILED);
        }

    }

}
