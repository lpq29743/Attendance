package com.attendance.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.attendance.R;
import com.attendance.customview.StatusBarCompat;
import com.attendance.dao.CourseDao;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 9/28/2016.
 */
public class CourseNameActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.right_tv)
    TextView mModifyTv;
    @BindView(R.id.modify_name_et)
    EditText mModifyNameEt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;

    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private int course_id;
    private String courseName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_name);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        initToolBar();
        initViewEffect();
        initTextView();
    }

    private void initToolBar() {
        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initViewEffect() {
        mModifyTv.setText(getString(R.string.modify));
        mModifyNameEt.setFocusable(true);
        mModifyNameEt.setFocusableInTouchMode(true);
        mModifyNameEt.requestFocus(mModifyNameEt.getText().length());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) CourseNameActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 200);
    }

    private void initTextView() {
        Intent intent = getIntent();
        course_id = intent.getIntExtra("course_id", 0);
        courseName = intent.getStringExtra("courseName");
        mTitleTv.setText(R.string.modify_course_name);
        mModifyNameEt.setText(courseName);
        mModifyNameEt.setSelection(mModifyNameEt.getText().length());
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(CourseNameActivity.this);
        // 设置进度条风格，风格为圆形，旋转的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage(message);
        // 设置初始位置
        progressDialog.setProgress(50);
        // 设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(true);
        // 让ProgressDialog显示
        progressDialog.show();
    }

    @OnClick(R.id.back_iv)
    public void back() {
        setResult(-1);
        finish();
    }

    @OnClick(R.id.right_tv)
    public void modify() {
        courseName = mModifyNameEt.getText().toString();
        modCourseName();
        Intent intent0 = new Intent(CourseNameActivity.this, CourseActivity.class);
        intent0.putExtra("courseName", courseName);
        setResult(0, intent0);
        finish();
    }

    /**
     * 修改课程名字
     */
    private void modCourseName() {

        showProgressDialog("修改中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            // 本地数据测试

            // 本地修改课程
            CourseDao courseDao = new CourseDao(this);
            courseDao.update(course_id, courseName);
            progressDialog.cancel();
            Toast.makeText(CourseNameActivity.this, ConstParameter.MOD_SUCCESS, Toast.LENGTH_SHORT).show();
        } else {
            // 服务器端修改课程
            String url = ConstParameter.SERVER_ADDRESS + "/modCourseName.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(CourseNameActivity.this);
            StringRequest mModCourseNameRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.cancel();
                    if (response.equals("success")) {
                        Toast.makeText(CourseNameActivity.this, ConstParameter.MOD_SUCCESS, Toast.LENGTH_SHORT).show();
                        CourseDao courseDao = new CourseDao(CourseNameActivity.this);
                        courseDao.update(course_id, courseName);
                    } else if (response.equals("failed")) {
                        Toast.makeText(CourseNameActivity.this, ConstParameter.MOD_FAILED, Toast.LENGTH_SHORT).show();
                    } else if (response.equals("error")) {
                        Toast.makeText(CourseNameActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CourseNameActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.cancel();
                    Toast.makeText(CourseNameActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> modCourseMap = new HashMap<>();
                    modCourseMap.put("course_id", course_id + "");
                    modCourseMap.put("courseName", courseName);
                    return modCourseMap;
                }
            };
            volleyUtil.addToRequestQueue(mModCourseNameRequest);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    @Override
    public void onBackPressed() {
        setResult(-1);
        finish();
        super.onBackPressed();
    }
}
