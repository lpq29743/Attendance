package com.attendance.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.attendance.R
import com.attendance.login.LoginContract
import com.attendance.login.LoginPresenter
import com.attendance.utils.SharedFileUtil

import butterknife.bindView
import com.attendance.courses.CoursesActivity

class LoginActivity : Activity(), LoginContract.View {

    internal val mUsernameEt: EditText by bindView(R.id.username_et)
    internal val mPasswordEt: EditText by bindView(R.id.password_et)
    internal val mRemPasswordCb: CheckBox by bindView(R.id.rem_password_cb)
    internal val mIdentityRg: RadioGroup by bindView(R.id.identity_rg)
    internal val mTeaBtn: RadioButton by bindView(R.id.teacher_btn)
    internal val mStuBtn: RadioButton by bindView(R.id.student_btn)
    internal val mLoginBtn: Button by bindView(R.id.login_btn)

    private var username = ""
    private var password = ""
    private var isTeacher = false
    private var isRemPassword = false

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

        sharedFileUtil = SharedFileUtil()
        username = sharedFileUtil!!.getString("username")
        mUsernameEt.setText(username)
        isTeacher = sharedFileUtil!!.getBoolean("isTeacher")
        if (isTeacher) {
            mTeaBtn.isChecked = true
        } else {
            mStuBtn.isChecked = true
        }
        isRemPassword = sharedFileUtil!!.getBoolean("isRemPassword")
        if (isRemPassword) {
            mRemPasswordCb.isChecked = true
            mPasswordEt.setText(password)
        }

        mIdentityRg.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.teacher_btn -> isTeacher = true
                R.id.student_btn -> isTeacher = false
            }
        }

        mLoginBtn.setOnClickListener {
            login()
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
        val intent = Intent(this@LoginActivity, CoursesActivity::class.java)
        this@LoginActivity.startActivity(intent)
        this@LoginActivity.finish()
        this@LoginActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun login() {
        username = mUsernameEt.text.toString()
        password = mPasswordEt.text.toString()
        isRemPassword = mRemPasswordCb.isChecked
        presenter!!.login(username, password, isTeacher, isRemPassword)
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
