package com.attendance.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.attendance.R;
import com.attendance.utils.SharedFileUtil;

import android.os.Bundle;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

    private long splashDelay = 2000;
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                sharedFileUtil.putBoolean("localMode", false);
                boolean hasLogin = sharedFileUtil.getBoolean("hasLogin");
                if (hasLogin) {
                    intent.setClass(SplashActivity.this,
                            MainActivity.class);
                } else {
                    intent.setClass(SplashActivity.this,
                            LoginActivity.class);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }

        };
        Timer timer = new Timer();
        timer.schedule(task, splashDelay);
    }

}