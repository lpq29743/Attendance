package com.attendance.login;

import android.util.Log;

import com.attendance.AttendanceApplication;
import com.attendance.R;
import com.attendance.data.source.remote.UserRemoteDataSource;
import com.attendance.util.AESUtil;
import com.attendance.util.NetWorkUtil;
import com.attendance.util.SharedFileUtil;

/**
 * Created by peiqin on 3/4/2017.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private String username;
    private String password;
    private boolean isTeacher;
    private boolean isRemPassword;
    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void login(String username, String password, boolean isTeacher, boolean isRemPassword) {

        Log.e("Login", username);
        Log.e("Login", password);

        this.username = username;
        this.password = password;
        this.isTeacher = isTeacher;
        this.isRemPassword = isRemPassword;

        if ("".compareTo(username) == 0 || "".compareTo(password) == 0) {
            view.showTip(AttendanceApplication.getContext().getString(R.string.login_warning));
            return;
        }

        Boolean netStatus = new NetWorkUtil().checkNetWorkEx(AttendanceApplication.getContext());
        if (!netStatus) {
            view.showTip(AttendanceApplication.getContext().getString(R.string.network_corrupt));
            return;
        }

        UserRemoteDataSource.getInstance().login(new LoginSubscriber(this, view), username, password, isTeacher);

    }

    @Override
    public void writeData() {
        SharedFileUtil sharedFileUtil = new SharedFileUtil();
        sharedFileUtil.putBoolean("hasLogin", true);
        sharedFileUtil.putString("username", username);
        if (isRemPassword) {
            //加密后再将密码放入SharePreferences
            String password_after_encrypt = AESUtil.encryptWithBase64(password);
            sharedFileUtil.putString("password", password_after_encrypt);
            sharedFileUtil.putBoolean("isRemPassword", true);
        }
        sharedFileUtil.putBoolean("isTeacher", isTeacher);
    }

}