package com.attendance.attendresults;

import com.attendance.AttendanceApplication;
import com.attendance.R;
import com.attendance.data.AttendResult;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by peiqin on 3/5/2017.
 */

public class GetAttendResultsSubscriber extends Subscriber<AttendResult> {

    private AttendResultsContract.View view;
    private List<AttendResult> list;

    public GetAttendResultsSubscriber(AttendResultsContract.View view) {
        this.view = view;
    }

    @Override
    public void onStart() {
        list = new ArrayList<>();
    }

    @Override
    public void onCompleted() {
        view.getAttendResultsSuccess(list);
        view.showTip(AttendanceApplication.getContext().getString(R.string.get_success));
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

        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }

    }

    @Override
    public void onNext(AttendResult resultBean) {

        list.add(resultBean);

    }

}
