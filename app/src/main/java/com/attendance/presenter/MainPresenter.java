package com.attendance.presenter;

import com.attendance.AttendanceApplication;
import com.attendance.contract.MainContract;
import com.attendance.entities.ConstParameter;
import com.attendance.http.HttpMethods;
import com.attendance.subscribers.GetCourseSubscriber;
import com.attendance.utils.NetWorkUtil;

/**
 * Created by peiqin on 3/4/2017.
 */

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void getCourseList() {

        Boolean netStatus = new NetWorkUtil().checkNetWorkEx(AttendanceApplication.getContext());
        if (!netStatus) {
            view.showTip(ConstParameter.NETWORK_CORRUPT);
            view.stopRefresh();
            return;
        }

        HttpMethods.getInstance().getCourseList(new GetCourseSubscriber(view));

    }

}