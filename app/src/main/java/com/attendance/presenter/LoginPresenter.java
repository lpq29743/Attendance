package com.attendance.presenter;

import android.app.Application;

import com.attendance.AttendanceApplication;
import com.attendance.contract.LoginContract;
import com.attendance.utils.NetWorkUtil;

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
    public void login(String username, String password) {

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

        String url = ConstParameter.SERVER_ADDRESS + "/login.php";
        VolleyUtil volleyUtil = VolleyUtil.getInstance(LoginActivity.this);

        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("username", username);
        loginMap.put("password", password);
        loginMap.put("isTeacher", isTeacher + "");
        JSONObject loginObject = new JSONObject(loginMap);

        JsonObjectRequest mLoginRequest = new JsonObjectRequest(Request.Method.POST, url, loginObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.cancel();
                        Toast.makeText(LoginActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }
                });
        volleyUtil.addToRequestQueue(mLoginRequest);
    }

    loginPost(username, password, isTeacher);

}

}
