package com.attendance.subscribers;

import com.attendance.contract.LoginContract;
import com.attendance.entities.ConstParameter;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by peiqin on 3/5/2017.
 */

public class LoginSubscriber<Boolean> extends Subscriber<Boolean> {

    private LoginContract.View view;

    public LoginSubscriber(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void onStart() {
        view.showProgress(ConstParameter.LOGIN_LOADING);
    }

    @Override
    public void onCompleted() {
        view.cancelProgress();
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
        view.cancelProgress();

        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }

    }

    @Override
    public void onNext(Boolean result) {

        if (result.equals("true")) {
            view.showTip(ConstParameter.LOGIN_SUCCESS);
            view.startMainActivity();
        } else {
            view.showTip(ConstParameter.LOGIN_FAILED);
        }

    }

}
