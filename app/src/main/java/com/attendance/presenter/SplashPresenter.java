package com.attendance.presenter;

import com.attendance.contract.SplashContract;
import com.attendance.utils.SharedFileUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by peiqin on 3/4/2017.
 */

public class SplashPresenter implements SplashContract.Presenter {

    private final SplashContract.View view;

    public SplashPresenter(SplashContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void startTimeTask() {

        long splashDelay = 2000;
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                boolean hasLogin = new SharedFileUtil().getBoolean("hasLogin");
                if (hasLogin) {
                    view.startMainActivity();
                } else {
                    view.startLoginActivity();
                }
            }

        };
        Timer timer = new Timer();
        timer.schedule(task, splashDelay);

    }

}