package com.attendance.presenter;

import com.attendance.AttendanceApplication;
import com.attendance.contract.LoginContract;
import com.attendance.entities.ConstParameter;
import com.attendance.http.AttendService;
import com.attendance.utils.NetWorkUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peiqin on 3/4/2017.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void login(String username, String password, String isTeacher) {

        if ("".compareTo(username) == 0 || "".compareTo(password) == 0) {
            view.showTip("帐号或密码不能为空");
            return;
        }

        Boolean netStatus = new NetWorkUtil().checkNetWorkEx(AttendanceApplication.getContext());
        if (!netStatus) {
            view.showTip("网络状况不佳，请检查网络情况");
            return;
        }

        view.showProgress("登录中……");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstParameter.SERVER_ADDRESS)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        AttendService attendService = retrofit.create(AttendService.class);

        attendService.login(username, password, isTeacher)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.cancel();
                        Toast.makeText(LoginActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String result) {
                        progressDialog.cancel();
                        try {
                            if (jsonObject.getString("result").equals("success")) {
                                writeData(jsonObject.getString("name"));
                                Toast.makeText(LoginActivity.this, ConstParameter.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(intent);
                                LoginActivity.this.finish();
                                LoginActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else if (jsonObject.getString("result").equals("failed")) {
                                Toast.makeText(LoginActivity.this, ConstParameter.LOGIN_FAILED, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getString("result").equals("error")) {
                                Toast.makeText(LoginActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

}