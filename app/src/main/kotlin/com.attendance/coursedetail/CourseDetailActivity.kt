package com.attendance.coursedetail

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife

import com.attendance.R

import butterknife.bindView
import com.attendance.attendresults.AttendResultsActivity

/**
 * Created by peiqin on 9/25/2016.
 */
class CourseDetailActivity : AppCompatActivity() {

    internal val mTitleTv: TextView by bindView(R.id.title_tv)
    internal val mCourseNameTv: TextView by bindView(R.id.course_name_tv)
    internal val mTeacherNameTv: TextView by bindView(R.id.teacher_name_tv)
    internal val mStudentNumTv: TextView by bindView(R.id.student_num_tv)
    internal val mAttDetailTv: TextView by bindView(R.id.attendance_detail_tv)
    internal val mNextLocationTv: TextView by bindView(R.id.next_location_tv)
    internal val mNextTimeTv: TextView by bindView(R.id.next_time_tv)
    internal val mReportTv: TextView by bindView(R.id.view_report_tv)
    internal val mBackIv: ImageView by bindView(R.id.back_iv)
    internal val toolbar: Toolbar by bindView(R.id.toolbar)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        initView()
    }

    private fun initView() {
        ButterKnife.bind(this)
        initTextView()

        mBackIv.setOnClickListener {
            finish()
        }

        mReportTv.setOnClickListener {
            val intent = Intent(this@CourseDetailActivity, AttendResultsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initTextView() {
        val intent = intent
        mTitleTv.setText(R.string.course_detail)
        mCourseNameTv.text = intent.getStringExtra("name")
        mTeacherNameTv.setText(R.string.fake_teacher)
        mAttDetailTv.setText(R.string.click_to_see)
        mStudentNumTv.setText(R.string.stu_num)
        mNextLocationTv.setText(R.string.next_location)
        mNextTimeTv.setText(R.string.next_time)
    }

}