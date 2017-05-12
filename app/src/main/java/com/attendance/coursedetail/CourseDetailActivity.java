package com.attendance.coursedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.attendance.R;
import com.attendance.attendresults.AttendResultsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 9/25/2016.
 */
public class CourseDetailActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.course_name_tv)
    TextView mCourseNameTv;
    @BindView(R.id.teacher_name_tv)
    TextView mTeacherNameTv;
    @BindView(R.id.student_num_tv)
    TextView mStudentNumTv;
    @BindView(R.id.attendance_detail_tv)
    TextView mAttDetailTv;
    @BindView(R.id.next_location_tv)
    TextView mNextLocationTv;
    @BindView(R.id.next_time_tv)
    TextView mNextTimeTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        initToolBar();
        initTextView();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initTextView() {
        Intent intent = getIntent();
        mTitleTv.setText(R.string.course_detail);
        mCourseNameTv.setText(intent.getStringExtra("name"));
        mTeacherNameTv.setText(R.string.fake_teacher);
        mAttDetailTv.setText(R.string.click_to_see);
        mStudentNumTv.setText(R.string.stu_num);
        mNextLocationTv.setText(R.string.next_location);
        mNextTimeTv.setText(R.string.next_time);
    }

    @OnClick(R.id.back_iv)
    public void back() {
        finish();
    }

    @OnClick(R.id.view_report_tv)
    public void viewReport() {
        Intent intent = new Intent(CourseDetailActivity.this, AttendResultsActivity.class);
        startActivity(intent);
    }

}