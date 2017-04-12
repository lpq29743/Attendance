package com.attendance.presenter

import com.attendance.contract.SplashContract
import com.attendance.utils.SharedFileUtil

import java.util.Timer
import java.util.TimerTask

/**
 * Created by peiqin on 3/4/2017.
 */

class SplashPresenter(private val view: SplashContract.View) : SplashContract.Presenter {

    init {
        this.view.setPresenter(this)
    }

    override fun startTimeTask() {

        val splashDelay: Long = 2000
        val task = object : TimerTask() {

            override fun run() {
                val hasLogin = SharedFileUtil().getBoolean("hasLogin")
                if (hasLogin) {
                    view.startMainActivity()
                } else {
                    view.startLoginActivity()
                }
            }

        }
        val timer = Timer()
        timer.schedule(task, splashDelay)

    }

}