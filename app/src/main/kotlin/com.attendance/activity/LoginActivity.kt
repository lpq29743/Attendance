package com.attendance.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

import com.attendance.R
import com.attendance.contract.LoginContract
import com.attendance.presenter.LoginPresenter
import com.attendance.utils.SharedFileUtil

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class LoginActivity : Activity(), LoginContract.View {

    @BindView(R.id.username_et)
    internal var mUsernameEt: EditText? = null
    @BindView(R.id.password_et)
    internal var mPasswordEt: EditText? = null
    @BindView(R.id.rem_password_cb)
    internal var mRemPasswordCb: CheckBox? = null
    @BindView(R.id.identity_rg)
    internal var mIdentityRg: RadioGroup? = null
    @BindView(R.id.teacher_btn)
    internal var mTeaBtn: RadioButton? = null
    @BindView(R.id.student_btn)
    internal var mStuBtn: RadioButton? = null

    private var username = ""
    private var password = ""
    private var isTeacher = false
    private var isRemPassword = false

    private var myEditorActionListener: MyEditorActionListener? = null
    private var sharedFileUtil: SharedFileUtil? = null
    private var progressDialog: ProgressDialog? = null
    private var presenter: LoginContract.Presenter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        presenter = LoginPresenter(this)
    }

    private fun initView() {
        ButterKnife.bind(this)

        sharedFileUtil = SharedFileUtil()
        username = sharedFileUtil!!.getString("username")
        mUsernameEt!!.setText(username)
        isTeacher = sharedFileUtil!!.getBoolean("isTeacher")
        if (isTeacher) {
            mTeaBtn!!.isChecked = true
        } else {
            mStuBtn!!.isChecked = true
        }
        isRemPassword = sharedFileUtil!!.getBoolean("isRemPassword")
        if (isRemPassword) {
            mRemPasswordCb!!.isChecked = true
            mPasswordEt!!.setText(password)
        }

        myEditorActionListener = MyEditorActionListener()
        mUsernameEt!!.setOnEditorActionListener(myEditorActionListener)
        mPasswordEt!!.setOnEditorActionListener(myEditorActionListener)

        mIdentityRg!!.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.teacher_btn -> isTeacher = true
                R.id.student_btn -> isTeacher = false
            }
        }

    }

    override fun setPresenter(presenter: LoginContract.Presenter) {
        this.presenter = presenter
    }

    override fun showTip(tip: String) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress(msg: String) {
        progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setMessage(msg)
        progressDialog!!.progress = 50
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()
    }

    override fun cancelProgress() {
        progressDialog!!.cancel()
    }

    override fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        this@LoginActivity.startActivity(intent)
        this@LoginActivity.finish()
        this@LoginActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @OnClick(R.id.login_btn)
    fun login() {
        username = mUsernameEt!!.text.toString()
        password = mPasswordEt!!.text.toString()
        if (mRemPasswordCb!!.isChecked) {
            isRemPassword = true
        } else {
            isRemPassword = false
        }
        presenter!!.login(username, password, isTeacher, isRemPassword)
    }

    private inner class MyEditorActionListener : TextView.OnEditorActionListener {

        override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
            when (v.id) {
                R.id.username_et ->
                    // 密码输入框获取焦点
                    mPasswordEt!!.requestFocus()
                R.id.password_et -> login()
            }
            return false
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (null != this.currentFocus) {
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager.hideSoftInputFromWindow(this
                    .currentFocus!!.windowToken, 0)
        }
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog!!.isShowing)
            progressDialog!!.cancel()
    }

}
