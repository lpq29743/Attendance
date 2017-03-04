package com.attendance.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.attendance.R;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.AESUtil;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    @BindView(R.id.username_et)
    EditText mUsernameEt;
    @BindView(R.id.password_et)
    EditText mPasswordEt;
    @BindView(R.id.remember_passwd)
    CheckBox mRemPassword;
    @BindView(R.id.identity)
    RadioGroup idGroup;
    @BindView(R.id.teacher)
    RadioButton mTeaBtn;
    @BindView(R.id.student)
    RadioButton mStuBtn;

    private String username, password;
    private boolean isTeacher = false;
    private boolean isRemPassword = false;

    private MyEditorActionListener myEditorActionListener = new MyEditorActionListener();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    public ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获取写入内存卡权限
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        username = sharedFileUtil.getString("username");
        mUsernameEt.setText(username);
        isTeacher = sharedFileUtil.getBoolean("isTeacher");
        if (isTeacher) {
            mTeaBtn.setChecked(true);
        } else {
            mStuBtn.setChecked(true);
        }
        isRemPassword = sharedFileUtil.getBoolean("isRemPassword");
        if (isRemPassword) {
            mRemPassword.setChecked(true);
            password = AESUtil.decryptWithBase64(sharedFileUtil.getString("password"));
            mPasswordEt.setText(password);
        }

        mUsernameEt.setOnEditorActionListener(myEditorActionListener);
        mPasswordEt.setOnEditorActionListener(myEditorActionListener);

        //RadioButton事件
        idGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup idgroup, int checkedId) {
                switch (idgroup.getCheckedRadioButtonId()) {
                    case R.id.teacher:
                        isTeacher = true;
                        break;
                    case R.id.student:
                        isTeacher = false;
                        break;
                }

            }
        });

    }

    public void login() {
        //数据检验及网络情况检测
        username = mUsernameEt.getText().toString();
        password = mPasswordEt.getText().toString();
        Boolean netStatus = netWorkUtils.checkNetWorkEx(LoginActivity.this);
        if ("".compareTo(username) == 0 || "".compareTo(password) == 0) {
            Toast.makeText(this, "帐号或密码不能为空", Toast.LENGTH_SHORT).show();
        } else if (!netStatus) {
            Toast.makeText(this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
        } else {
            loginPost(username, password, isTeacher);
        }
    }

    public void loginPost(final String username, final String password, final boolean isTeacher) {

        showProgressDialog();

        //本地数据测试
        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            Map<String, String> map = new HashMap<>();
            final String teacher_username = "1";
            final String teacher_password = "123";
            final String student_username = "2";
            final String student_password = "456";
            if (isTeacher && username.equals(teacher_username) && password.equals(teacher_password)) {
                map.put("result", "Success");
                map.put("name", "大牛");
            } else if (!isTeacher && username.equals(student_username) && password.equals(student_password)) {
                map.put("result", "Success");
                map.put("name", "小白");
            } else
                map.put("result", "Failed");
            progressDialog.cancel();
            if (map.get("result").equals("Success")) {
                Toast.makeText(this, ConstParameter.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                writeData(map.get("name"));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
                LoginActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(this, ConstParameter.LOGIN_FAILED, Toast.LENGTH_SHORT).show();
            }
        } else {
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

    }

    private void writeData(final String name) {
        sharedFileUtil.putBoolean("hasLogin", true);
        sharedFileUtil.putString("username", username);
        sharedFileUtil.putString("name", name);
        if (mRemPassword.isChecked() == true) {
            //加密后再将密码放入SharePreferences
            String password_after_encrypt = AESUtil.encryptWithBase64(password);
            sharedFileUtil.putString("password", password_after_encrypt);
            sharedFileUtil.putBoolean("isRemPassword", true);
        }
        sharedFileUtil.putBoolean("isTeacher", isTeacher);
    }

    private void showProgressDialog() {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(LoginActivity.this);
        // 设置进度条风格，风格为圆形，旋转的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage("登录中……");
        // 设置初始位置
        progressDialog.setProgress(50);
        // 设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(true);
        // 让ProgressDialog显示
        progressDialog.show();
    }

    @OnClick(R.id.login_btn)
    public void startLogin() {
        LoginActivity.this.login();
    }

    @OnClick(R.id.register_tv)
    public void startRegister() {
        //进入RegisterActivity
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
        LoginActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private class MyEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.username_et:
                    //密码输入框获取焦点
                    mPasswordEt.requestFocus();
                    break;
                case R.id.password_et:
                    LoginActivity.this.login();
                    break;
            }
            return false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this
                    .getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

}
