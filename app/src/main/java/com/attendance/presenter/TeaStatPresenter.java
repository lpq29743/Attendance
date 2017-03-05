package com.attendance.presenter;

import com.attendance.AttendanceApplication;
import com.attendance.contract.MainContract;
import com.attendance.contract.TeaStatContract;
import com.attendance.entities.ConstParameter;
import com.attendance.http.HttpMethods;
import com.attendance.subscribers.GetCourseSubscriber;
import com.attendance.subscribers.GetStatSubscriber;
import com.attendance.utils.NetWorkUtil;

/**
 * Created by peiqin on 3/4/2017.
 */

public class TeaStatPresenter implements TeaStatContract.Presenter {

    private final TeaStatContract.View view;

    public TeaStatPresenter(TeaStatContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void getStatList() {

        Boolean netStatus = new NetWorkUtil().checkNetWorkEx(AttendanceApplication.getContext());
        if (!netStatus) {
            view.showTip(ConstParameter.NETWORK_CORRUPT);
            return;
        }

        HttpMethods.getInstance().getStatList(new GetStatSubscriber(view));

    }

}