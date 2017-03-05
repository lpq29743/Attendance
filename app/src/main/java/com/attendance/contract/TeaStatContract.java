package com.attendance.contract;

import com.attendance.BasePresenter;
import com.attendance.BaseView;
import com.attendance.entities.ResultBean;

import java.util.List;

/**
 * Created by peiqin on 3/4/2017.
 */

public interface TeaStatContract {

    interface View extends BaseView<Presenter> {

        void showTip(String tip);

        void getStatSuccess(List<ResultBean> list);

    }

    interface Presenter extends BasePresenter {

        void getStatList();

    }

}
