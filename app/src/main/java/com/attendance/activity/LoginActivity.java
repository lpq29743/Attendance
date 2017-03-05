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
import com.attendance.contract.LoginContract;
import com.attendance.entities.ConstParameter;
import com.attendance.presenter.LoginPresenter;
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

public class LoginActivity extends Activity implements LoginContract.View {

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

    private boolean isRemPassword = false;

    private MyEditorActionListener myEditorActionListener = new MyEditorActionListener();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private LoginContract.Presenter presenter;

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

        presenter = new LoginPresenter(this);

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

    @Override
    public void loginSuccess() {
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void showTip(String tip){
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

    public void showProgress(String msg) {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setProgress(50);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @OnClick(R.id.login_btn)
    public void startLogin() {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        presenter.login(username, password, isTeacher);
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
