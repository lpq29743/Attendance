package com.attendance;

/**
 * Created by peiqin on 3/4/2017.
 */

public interface BaseView<T> {

    void setPresenter(T presenter);

    void showTip(String tip);

    void showProgress(String msg);

}
