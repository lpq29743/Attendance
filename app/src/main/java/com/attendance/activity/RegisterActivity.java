package com.attendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.attendance.R;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 9/7/2016.
 */
public class RegisterActivity extends Activity {

    @BindView(R.id.name_et)
    EditText mNameEt;
    @BindView(R.id.username_et)
    EditText mUsernameEt;
    @BindView(R.id.password_et)
    EditText mPasswordEt;
    @BindView(R.id.password_confirm_et)
    EditText mPasswordConfirmEt;
    @BindView(R.id.identity)
    RadioGroup idGroup;
    @BindView(R.id.student)
    RadioButton mStuBtn;

    private String name, username, password, password_confirm;
    private boolean isTeacher = false;

    private MyEditorActionListener myEditorActionListener = new MyEditorActionListener();
    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    public ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        mStuBtn.setChecked(true);

        mNameEt.setOnEditorActionListener(myEditorActionListener);
        mUsernameEt.setOnEditorActionListener(myEditorActionListener);
        mPasswordEt.setOnEditorActionListener(myEditorActionListener);
        mPasswordConfirmEt.setOnEditorActionListener(myEditorActionListener);

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

    public void register() {
        //数据检验及网络情况检测
        name = mNameEt.getText().toString();
        username = mUsernameEt.getText().toString();
        password = mPasswordEt.getText().toString();
        password_confirm = mPasswordConfirmEt.getText().toString();
        Boolean netStatus = netWorkUtils.checkNetWorkEx(RegisterActivity.this);
        if ("".compareTo(name) == 0 || "".compareTo(username) == 0 || "".compareTo(password) == 0 || "".compareTo(password_confirm) == 0) {
            Toast.makeText(RegisterActivity.this, "填写资料不完整", Toast.LENGTH_SHORT).show();
        } else if (password_confirm.compareTo(password) != 0) {
            Toast.makeText(RegisterActivity.this, "两次填写的密码不一致", Toast.LENGTH_SHORT).show();
        } else if (!netStatus) {
            Toast.makeText(RegisterActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
        } else {
            if (isTeacher) {
                registerPost(name, username, password, isTeacher);
            } else {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("将上传设备码用于签到身份证明，若此机不常用，请点击取消")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        registerPost(name, username, password, isTeacher);
                                    }
                                }).setNegativeButton("取消", null).create()
                        .show();
            }
        }
    }

    public void registerPost(final String name, final String username, final String password, final boolean isTeacher) {

        showProgressDialog();

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            //本地数据测试
            final String teacher_username = "1";
            final String student_username = "2";
            progressDialog.cancel();
            if ((isTeacher && username.equals(teacher_username)) || (!isTeacher && username.equals(student_username))) {
                writeData(username, name, isTeacher);
                Toast.makeText(RegisterActivity.this, ConstParameter.REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                RegisterActivity.this.startActivity(intent);
                RegisterActivity.this.finish();
                RegisterActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(RegisterActivity.this, ConstParameter.REGISTER_FAILED, Toast.LENGTH_LONG).show();
            }
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/register.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(RegisterActivity.this);
            StringRequest mRegisterRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.cancel();
                    if (response.equals("success")) {
                        writeData(username, name, isTeacher);
                        Toast.makeText(RegisterActivity.this, ConstParameter.REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        RegisterActivity.this.startActivity(intent);
                        RegisterActivity.this.finish();
                        RegisterActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else if (response.equals("failed")) {
                        Toast.makeText(RegisterActivity.this, ConstParameter.REGISTER_FAILED, Toast.LENGTH_SHORT).show();
                    } else if (response.equals("error")) {
                        Toast.makeText(RegisterActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.cancel();
                    Toast.makeText(RegisterActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> registerMap = new HashMap<>();
                    registerMap.put("username", username);
                    registerMap.put("password", password);
                    registerMap.put("name", name);
                    registerMap.put("isTeacher", isTeacher + "");
                    if (!isTeacher) {
                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        registerMap.put("androidKey", tm.getDeviceId());
                    }
                    return registerMap;
                }
            };
            volleyUtil.addToRequestQueue(mRegisterRequest);
        }
    }

    private void writeData(String username, String name, boolean isTeacher) {
        sharedFileUtil.putBoolean("hasLogin", true);
        sharedFileUtil.putString("username", username);
        sharedFileUtil.putString("name", name);
        sharedFileUtil.putBoolean("isTeacher", isTeacher);
    }

    private void showProgressDialog() {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(RegisterActivity.this);
        // 设置进度条风格，风格为圆形，旋转的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage("注册中……");
        // 设置初始位置
        progressDialog.setProgress(50);
        // 设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(true);
        // 让ProgressDialog显示
        progressDialog.show();
    }

    @OnClick(R.id.login_tv)
    public void startLogin() {
        //进入RegisterActivity
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(intent);
        RegisterActivity.this.finish();
        RegisterActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.register_btn)
    public void startRegister() {
        RegisterActivity.this.register();
    }

    private class MyEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.name_et:
                    mUsernameEt.requestFocus();
                    break;
                case R.id.username_et:
                    mPasswordEt.requestFocus();
                    break;
                case R.id.password_et:
                    mPasswordConfirmEt.requestFocus();
                    break;
                case R.id.password_confirm_et:
                    RegisterActivity.this.register();
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
