package com.attendance.login;

import com.attendance.AttendanceApplication;
import com.attendance.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by peiqin on 3/5/2017.
 */

public class LoginSubscriber extends Subscriber<Boolean> {

    private LoginContract.View view;
    private LoginContract.Presenter presenter;

    public LoginSubscriber(LoginContract.Presenter presenter, LoginContract.View view) {
        this.view = view;
        this.presenter = presenter;
    }

    @Override
    public void onStart() {
        view.showProgress(AttendanceApplication.getContext().getString(R.string.login_loading));
    }

    @Override
    public void onCompleted() {
        view.cancelProgress();
        presenter.writeData();
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
        view.cancelProgress();

        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }

    }

    @Override
    public void onNext(Boolean result) {

        if (result) {
            view.showTip(AttendanceApplication.getContext().getString(R.string.login_success));
            view.startMainActivity();
        } else {
            view.showTip(AttendanceApplication.getContext().getString(R.string.login_failed));
        }

    }

}
