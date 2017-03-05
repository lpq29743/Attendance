package com.attendance.activity;

import com.attendance.R;
import com.attendance.contract.SplashContract;
import com.attendance.presenter.SplashPresenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.app.Activity;

public class SplashActivity extends Activity implements SplashContract.View {

    private SplashContract.Presenter presenter;

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
        presenter = new SplashPresenter(this);
        presenter.startTimeTask();
    }

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

}