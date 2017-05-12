package com.attendance.attendresults;

import com.attendance.AttendanceApplication;
import com.attendance.R;
import com.attendance.data.source.remote.AttendResultsRemoteDataSource;
import com.attendance.util.NetWorkUtil;

/**
 * Created by peiqin on 3/4/2017.
 */

public class AttendResultsPresenter implements AttendResultsContract.Presenter {

    private final AttendResultsContract.View view;

    public AttendResultsPresenter(AttendResultsContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void getAttendResultList() {

        Boolean netStatus = new NetWorkUtil().checkNetWorkEx(AttendanceApplication.getContext());
        if (!netStatus) {
            view.showTip(AttendanceApplication.getContext().getString(R.string.network_corrupt));
            return;
        }

        AttendResultsRemoteDataSource.getInstance().getAttendResultList(new GetAttendResultsSubscriber(view));

    }

}