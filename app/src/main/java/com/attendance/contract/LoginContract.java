package com.attendance.contract;

import com.attendance.BasePresenter;
import com.attendance.BaseView;

/**
 * Created by peiqin on 3/4/2017.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {
        void loginSuccess();
    }

    interface Presenter extends BasePresenter {
        void login(String username, String password);
    }

}
