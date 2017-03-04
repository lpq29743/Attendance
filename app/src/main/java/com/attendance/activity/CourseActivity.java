package com.attendance.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.attendance.R;
import com.attendance.customview.StatusBarCompat;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 9/25/2016.
 */
public class CourseActivity extends AppCompatActivity {

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
    @BindView(R.id.first_tv)
    TextView mFirstTv;
    @BindView(R.id.second_tv)
    TextView mSecondTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //经纬度
    private double mLongitude;
    private double mLatitude;
    private int course_id;
    private boolean isTeacher;
    private String courseName, nextAttend, nextFinish, nextWeekDay;
    private LocationClient mLocationClient;
    private ProgressDialog progressDialog;

    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        initToolBar();
        initTextView();
        if (!isTeacher) {
            //坐标获取
            initLocation();
        }
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                //获取经纬度
                mLatitude = bdLocation.getLatitude();
                mLongitude = bdLocation.getLongitude();
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
    }

    private void initToolBar() {
        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initTextView() {
        Intent intent = getIntent();
        course_id = intent.getIntExtra("id", 0);
        courseName = intent.getStringExtra("name");
        mTitleTv.setText(R.string.course_detail);
        mCourseNameTv.setText(courseName);
        mTeacherNameTv.setText(intent.getStringExtra("teacherName"));
        mAttDetailTv.setText(getString(R.string.click_to_see));

        isTeacher = sharedFileUtil.getBoolean("isTeacher");
        if (!isTeacher) {
            mFirstTv.setText(R.string.attend_check);
            mSecondTv.setText(R.string.finish_check);
        }
    }

    private void initData() {
        boolean localMode = sharedFileUtil.getBoolean("localMode");

        mStudentNumTv.setText("0");
        mNextLocationTv.setText("空");
        mNextTimeTv.setText("空");

        if (localMode) {
            //本地数据测试
        } else {
            Boolean netStatus = netWorkUtils.checkNetWorkEx(CourseActivity.this);
            if (!netStatus) {
                Toast.makeText(CourseActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
            } else {
                String url = ConstParameter.SERVER_ADDRESS + "/getCourseInfo.php";
                VolleyUtil volleyUtil = VolleyUtil.getInstance(CourseActivity.this);

                final Map<String, String> getCourseInfoMap = new HashMap<>();
                getCourseInfoMap.put("courseId", course_id + "");
                final JSONObject getCourseInfoObject = new JSONObject(getCourseInfoMap);

                JsonObjectRequest mGetCourseInfoRequest = new JsonObjectRequest(Request.Method.POST, url, getCourseInfoObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if (jsonObject.getString("result").equals("success")) {
                                        mStudentNumTv.setText(jsonObject.getString("stuNum"));
                                        mNextLocationTv.setText(jsonObject.getString("nextLocation"));
                                        mNextTimeTv.setText(jsonObject.getString("nextTime"));
                                        nextWeekDay = jsonObject.getString("nextTime").substring(0, 3);
                                        nextAttend = jsonObject.getString("nextTime").substring(4, 9);
                                        nextFinish = jsonObject.getString("nextTime").substring(10, 15);
                                        Toast.makeText(CourseActivity.this, ConstParameter.GET_SUCCESS, Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.getString("result").equals("failed")) {
                                        mStudentNumTv.setText(jsonObject.getString("stuNum"));
                                        Toast.makeText(CourseActivity.this, ConstParameter.GET_SUCCESS, Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.getString("result").equals("error")) {
                                        Toast.makeText(CourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(CourseActivity.this, ConstParameter.GET_FAILED, Toast.LENGTH_SHORT).show();
                    }
                });
                volleyUtil.addToRequestQueue(mGetCourseInfoRequest);
            }

        }
    }

    private void check(boolean isAttend) {
        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            //本地数据测试
        } else {
            Boolean netStatus = netWorkUtils.checkNetWorkEx(CourseActivity.this);
            Boolean isOpen = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
            if (!netStatus) {
                Toast.makeText(CourseActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
            } else if (Build.VERSION.SDK_INT <= 22 || isOpen) {
                Toast.makeText(CourseActivity.this, "定位失败，需关闭模拟位置功能", Toast.LENGTH_SHORT).show();
            } else {
                new CheckTask().execute(isAttend);
            }
        }
    }

    private class CheckTask extends AsyncTask<Boolean, Void, Boolean> {
        boolean isAttend;

        @Override
        protected void onPreExecute() {
            showProgressDialog("签到中");
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            isAttend = params[0];
            String timeUrl = "http://www.beijing-time.org";
            String mCheckTime;
            isAttend = params[0];
            if (isAttend) {
                mCheckTime = nextAttend;
            } else {
                mCheckTime = nextFinish;
            }
            try {
                URL url = new URL(timeUrl);// 取得资源对象
                URLConnection uc = url.openConnection();// 生成连接对象
                uc.connect();// 发出连接
                long ld = uc.getDate();// 读取网站日期时间
                Date date = new Date(ld);
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE", Locale.CHINA);
                if (sdf1.format(date).equals(nextWeekDay)) {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);// 输出北京时间
                    Date date0 = sdf3.parse(sdf2.format(date) + " " + mCheckTime);
                    long diff = date.getTime() - date0.getTime();
                    if (isAttend && diff < 0 && diff > -1000 * 60 * 10) {
                        return true;
                    } else if (!isAttend && diff > 0 && diff < -1000 * 60 * 10) {
                        return true;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                progressDialog.cancel();
                Toast.makeText(CourseActivity.this, "不好意思，现在不是签到时间", Toast.LENGTH_SHORT).show();
            } else {
                String url = "";
                if (isAttend) {
                    url = ConstParameter.SERVER_ADDRESS + "/attendCheck.php";
                } else {
                    url = ConstParameter.SERVER_ADDRESS + "/finishCheck.php";
                }
                VolleyUtil volleyUtil = VolleyUtil.getInstance(CourseActivity.this);

                final Map<String, String> checkMap = new HashMap<>();
                checkMap.put("courseId", course_id + "");
                checkMap.put("username", sharedFileUtil.getString("username"));
                checkMap.put("longitude", mLongitude + "");
                checkMap.put("latitude", mLatitude + "");
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                checkMap.put("androidKey", tm.getDeviceId());
                final JSONObject checkObject = new JSONObject(checkMap);

                JsonObjectRequest mCheckRequest = new JsonObjectRequest(Request.Method.POST, url, checkObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    progressDialog.cancel();
                                    if (jsonObject.getString("result").equals("success")) {
                                        Toast.makeText(CourseActivity.this, ConstParameter.SIGN_SUCCESS, Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.getString("result").equals("failed")) {
                                        Toast.makeText(CourseActivity.this, ConstParameter.SIGN_FAILED, Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.getString("result").equals("error")) {
                                        Toast.makeText(CourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.cancel();
                        Toast.makeText(CourseActivity.this, volleyError.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });
                volleyUtil.addToRequestQueue(mCheckRequest);
            }
        }

    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(CourseActivity.this);
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
        Intent intent = new Intent();
        intent.putExtra("courseName", courseName);
        intent.putExtra("course_id", course_id);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.course_name_layout)
    public void startCourseNameActivity() {
        if (isTeacher) {
            Intent intent0 = new Intent(CourseActivity.this, CourseNameActivity.class);
            intent0.putExtra("courseName", courseName);
            intent0.putExtra("course_id", course_id);
            startActivityForResult(intent0, 0);
        } else {
            Toast.makeText(CourseActivity.this, "课程名不可修改", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.teacher_name_layout)
    public void startTeaNameActivity() {
        Toast.makeText(CourseActivity.this, "任课老师不可修改", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.student_num_layout)
    public void startStuNumActivity() {
        Toast.makeText(CourseActivity.this, "学生人数不可修改", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.attendance_detail_layout)
    public void startAttDetailActivity() {
        Intent intent1 = new Intent(CourseActivity.this, AttDetailActivity.class);
        intent1.putExtra("course_id", course_id);
        startActivityForResult(intent1, 1);
    }

    @OnClick(R.id.first_tv)
    public void attendCheck() {
        if (!isTeacher) {
            check(true);
        }
    }

    @OnClick(R.id.second_tv)
    public void finishCheck() {
        if (!isTeacher) {
            check(false);
        }
    }

    @OnClick(R.id.view_report_tv)
    public void viewReport() {
        if (!isTeacher) {
            Intent intent2 = new Intent(CourseActivity.this, StuStatActivity.class);
            intent2.putExtra("course_id", course_id);
            startActivity(intent2);
        } else {
            Intent intent3 = new Intent(CourseActivity.this, TeaStatActivity.class);
            intent3.putExtra("course_id", course_id);
            startActivity(intent3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    courseName = data.getStringExtra("courseName");
                    mCourseNameTv.setText(courseName);
                }
                break;
            case 1:
                initData();
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("courseName", courseName);
        intent.putExtra("course_id", course_id);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始定位
        if (!isTeacher && !mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //定位停止
        if (!isTeacher)
            mLocationClient.stop();
    }

}

