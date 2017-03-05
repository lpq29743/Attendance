package com.attendance.contract;

import com.attendance.BasePresenter;
import com.attendance.BaseView;

/**
 * Created by peiqin on 3/4/2017.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void showTip(String tip);

        void startRefresh();

        void stopRefresh();

        void getCourseSuccess();

    }

    interface Presenter extends BasePresenter {

        void getCourseList();

    }

}
