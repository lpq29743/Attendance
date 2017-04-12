package com.attendance.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView

import com.attendance.R
import com.attendance.customview.StatusBarCompat

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by peiqin on 9/25/2016.
 */
class CourseActivity : AppCompatActivity() {

    @BindView(R.id.title_tv)
    internal var mTitleTv: TextView? = null
    @BindView(R.id.course_name_tv)
    internal var mCourseNameTv: TextView? = null
    @BindView(R.id.teacher_name_tv)
    internal var mTeacherNameTv: TextView? = null
    @BindView(R.id.student_num_tv)
    internal var mStudentNumTv: TextView? = null
    @BindView(R.id.attendance_detail_tv)
    internal var mAttDetailTv: TextView? = null
    @BindView(R.id.next_location_tv)
    internal var mNextLocationTv: TextView? = null
    @BindView(R.id.next_time_tv)
    internal var mNextTimeTv: TextView? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        initView()
    }

    private fun initView() {
        ButterKnife.bind(this)
        initToolBar()
        initTextView()
    }

    private fun initToolBar() {
        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary))
        setSupportActionBar(toolbar)
        //隐藏Toolbar的标题
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun initTextView() {
        val intent = intent
        mTitleTv!!.setText(R.string.course_detail)
        mCourseNameTv!!.text = intent.getStringExtra("name")
        mTeacherNameTv!!.setText(R.string.fake_teacher)
        mAttDetailTv!!.setText(R.string.click_to_see)
        mStudentNumTv!!.setText(R.string.stu_num)
        mNextLocationTv!!.setText(R.string.next_location)
        mNextTimeTv!!.setText(R.string.next_time)
    }

    @OnClick(R.id.back_iv)
    fun back() {
        finish()
    }

    @OnClick(R.id.view_report_tv)
    fun viewReport() {
        val intent = Intent(this@CourseActivity, TeaStatActivity::class.java)
        startActivity(intent)
    }

}