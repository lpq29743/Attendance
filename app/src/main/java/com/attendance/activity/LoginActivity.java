package com.attendance.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.attendance.R;
import com.attendance.contract.LoginContract;
import com.attendance.presenter.LoginPresenter;
import com.attendance.utils.AESUtil;
import com.attendance.utils.SharedFileUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity implements LoginContract.View {

    @BindView(R.id.username_et)
    EditText mUsernameEt;
    @BindView(R.id.password_et)
    EditText mPasswordEt;
    @BindView(R.id.rem_password_cb)
    CheckBox mRemPasswordCb;
    @BindView(R.id.identity_rg)
    RadioGroup mIdentityRg;
    @BindView(R.id.teacher_btn)
    RadioButton mTeaBtn;
    @BindView(R.id.student_btn)
    RadioButton mStuBtn;

    private String username = "";
    private String password = "";
    private boolean isTeacher = false;
    private boolean isRemPassword = false;

    private MyEditorActionListener myEditorActionListener;
    private SharedFileUtil sharedFileUtil;
    private ProgressDialog progressDialog;
    private LoginContract.Presenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        presenter = new LoginPresenter(this);
    }

    private void initView() {
        ButterKnife.bind(this);

        sharedFileUtil = new SharedFileUtil();
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
            mRemPasswordCb.setChecked(true);
            password = AESUtil.decryptWithBase64(sharedFileUtil.getString("password"));
            mPasswordEt.setText(password);
        }

        myEditorActionListener = new MyEditorActionListener();
        mUsernameEt.setOnEditorActionListener(myEditorActionListener);
        mPasswordEt.setOnEditorActionListener(myEditorActionListener);

        mIdentityRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (radioGroup.getCheckedRadioButtonId()) {
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

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showTip(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(String msg) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setProgress(50);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void cancelProgress() {
        progressDialog.cancel();
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
        LoginActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.login_btn)
    public void login() {
        username = mUsernameEt.getText().toString();
        password = mPasswordEt.getText().toString();
        if (mRemPasswordCb.isChecked()) {
            isRemPassword = true;
        } else {
            isRemPassword = false;
        }
        presenter.login(username, password, isTeacher, isRemPassword);
    }

    private class MyEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.username_et:
                    // 密码输入框获取焦点
                    mPasswordEt.requestFocus();
                    break;
                case R.id.password_et:
                    presenter.login(username, password, isTeacher, isRemPassword);
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
