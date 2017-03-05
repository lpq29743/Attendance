package com.attendance.contract;

import com.attendance.BasePresenter;
import com.attendance.BaseView;

/**
 * Created by peiqin on 3/4/2017.
 */

public interface SplashContract {

    interface View extends BaseView<Presenter> {

        void startLoginActivity();

        void startMainActivity();

    }

    interface Presenter extends BasePresenter {

        void startTimeTask();

    }

}
