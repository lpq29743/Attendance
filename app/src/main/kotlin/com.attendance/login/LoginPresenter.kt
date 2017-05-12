package com.attendance.login

import android.util.Log

import com.attendance.AttendanceApplication
import com.attendance.R
import com.attendance.data.source.remote.UserRemoteDataSource
import com.attendance.utils.NetWorkUtil
import com.attendance.utils.SharedFileUtil

/**
 * Created by peiqin on 3/4/2017.
 */

class LoginPresenter(private val view: LoginContract.View) : LoginContract.Presenter {

    private var username: String = ""
    private var password: String = ""
    private var isTeacher: Boolean = false
    private var isRemPassword: Boolean = false

    init {
        this.view.setPresenter(this)
    }

    override fun login(username: String, password: String, isTeacher: Boolean, isRemPassword: Boolean) {

        Log.e("Login", username)
        Log.e("Login", password)

        this.username = username
        this.password = password
        this.isTeacher = isTeacher
        this.isRemPassword = isRemPassword

        if ("".compareTo(username) == 0 || "".compareTo(password) == 0) {
            view.showTip(AttendanceApplication.context.getString(R.string.login_warning))
            return
        }

        val netStatus = NetWorkUtil().checkNetWorkEx(AttendanceApplication.context)
        if (!netStatus) {
            view.showTip(AttendanceApplication.context.getString(R.string.network_corrupt))
            return
        }

        UserRemoteDataSource.Companion.instance.login(LoginSubscriber(this, view), username, password, isTeacher)

    }

    override fun writeData() {
        val sharedFileUtil = SharedFileUtil()
        sharedFileUtil.putBoolean("hasLogin", true)
        sharedFileUtil.putString("username", username)
        if (isRemPassword) {
            sharedFileUtil.putString("password", password)
            sharedFileUtil.putBoolean("isRemPassword", true)
        }
        sharedFileUtil.putBoolean("isTeacher", isTeacher)
    }

}