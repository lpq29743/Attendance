package com.attendance.splash

import com.attendance.R
import com.attendance.splash.SplashContract
import com.attendance.splash.SplashPresenter

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.app.Activity
import com.attendance.courses.CoursesActivity
import com.attendance.login.LoginActivity

class SplashActivity : Activity(), SplashContract.View {

    private var presenter: SplashContract.Presenter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (!isTaskRoot) {
            finish()
            return
        }
        presenter = SplashPresenter(this)
        presenter!!.startTimeTask()
    }

    override fun setPresenter(presenter: SplashContract.Presenter) {
        this.presenter = presenter
    }

    override fun startLoginActivity() {
        val intent = Intent()
        intent.setClass(this@SplashActivity,
                LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out)
    }

    override fun startMainActivity() {
        val intent = Intent()
        intent.setClass(this@SplashActivity,
                CoursesActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out)
    }

}