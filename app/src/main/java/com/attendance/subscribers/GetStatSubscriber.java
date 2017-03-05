package com.attendance.subscribers;

import com.attendance.contract.TeaStatContract;
import com.attendance.entities.ConstParameter;
import com.attendance.entities.ResultBean;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by peiqin on 3/5/2017.
 */

public class GetStatSubscriber extends Subscriber<ResultBean> {

    private TeaStatContract.View view;
    private List<ResultBean> list;

    public GetStatSubscriber(TeaStatContract.View view) {
        this.view = view;
    }

    @Override
    public void onStart() {
        list = new ArrayList<>();
    }

    @Override
    public void onCompleted() {
        view.getStatSuccess(list);
        view.showTip(ConstParameter.GET_SUCCESS);
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

        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }

    }

    @Override
    public void onNext(ResultBean resultBean) {

        list.add(resultBean);

    }

}
