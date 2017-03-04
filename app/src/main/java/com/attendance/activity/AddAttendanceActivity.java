package com.attendance.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.attendance.R;
import com.attendance.customview.StatusBarCompat;
import com.attendance.dao.AttDetailDao;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 10/16/2016.
 */
public class AddAttendanceActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.begin_time_tv)
    TextView mBeginTimeTv;
    @BindView(R.id.end_time_tv)
    TextView mEndTimeTv;
    @BindView(R.id.location_tv)
    TextView mLocationTv;
    @BindView(R.id.right_tv)
    TextView mConfirmTv;
    @BindView(R.id.day_spin)
    Spinner mDaySpin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private NetWorkUtil netWorkUtils = new NetWorkUtil();

    private String beginTime, endTime, locationName;
    private int dayOfWeek, course_id, locationId = -1;
    private int beginHour, beginMin, endHour, endMin;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);
        Intent intent = getIntent();
        course_id = intent.getIntExtra("course_id", 0);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitleTv.setText(R.string.add_attendance);
        mConfirmTv.setText(R.string.confirm);

        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item
                , getResources().getStringArray(R.array.week_day));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDaySpin.setAdapter(adapter);
        mDaySpin.setSelection(0);
        mDaySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.colorTextGray));
                dayOfWeek = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void AddAtt() {
        // 数据获取
        beginTime = mBeginTimeTv.getText().toString();
        endTime = mEndTimeTv.getText().toString();

        // 数据校验
        Boolean netStatus = netWorkUtils.checkNetWorkEx(AddAttendanceActivity.this);
        if (beginTime.equals(getString(R.string.click_to_set)) || endTime.equals(getString(R.string.click_to_set))
                || locationId < 0) {
            Toast.makeText(this, "信息填写不完整", Toast.LENGTH_SHORT).show();
        } else if (endHour < beginHour || (endHour == beginHour && endMin <= beginMin)) {
            Toast.makeText(this, "时间设置不符合要求，请重新设置", Toast.LENGTH_SHORT).show();
        } else if (!netStatus) {
            Toast.makeText(this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
        } else {
            // 服务器访问
            AddAttPost();
        }

    }

    private void AddAttPost() {
        showProgressDialog("添加中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            // 本地数据测试
            int id = 201;
            AttDetailDao attDetailDao = new AttDetailDao(this);
            attDetailDao.insert(id, beginTime, endTime, dayOfWeek, course_id, locationId, locationName);
            progressDialog.cancel();
            setResult(0);
            finish();
        } else {
            // 服务器端添加考勤
            String url = ConstParameter.SERVER_ADDRESS + "/addAttDetail.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(AddAttendanceActivity.this);

            Map<String, String> addAttendanceMap = new HashMap<>();
            addAttendanceMap.put("dayOfWeek", dayOfWeek + "");
            addAttendanceMap.put("beginTime", beginTime);
            addAttendanceMap.put("endTime", endTime);
            addAttendanceMap.put("courseId", course_id + "");
            addAttendanceMap.put("locationId", locationId + "");
            final JSONObject addAttendanceObject = new JSONObject(addAttendanceMap);
            JsonObjectRequest mAddAttendanceRequest = new JsonObjectRequest(Request.Method.POST, url, addAttendanceObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            progressDialog.cancel();
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    Toast.makeText(AddAttendanceActivity.this, ConstParameter.ADD_SUCCESS, Toast.LENGTH_SHORT).show();
                                    int detailId = jsonObject.getInt("detailId");
                                    AttDetailDao attDetailDao = new AttDetailDao(AddAttendanceActivity.this);
                                    attDetailDao.insert(detailId, beginTime, endTime, dayOfWeek, course_id, locationId, locationName);
                                    progressDialog.cancel();
                                    setResult(0);
                                    finish();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    Toast.makeText(AddAttendanceActivity.this, ConstParameter.ADD_FAILED, Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(AddAttendanceActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                } else {
                                    Toast.makeText(AddAttendanceActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.cancel();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.cancel();
                            Toast.makeText(AddAttendanceActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
            volleyUtil.addToRequestQueue(mAddAttendanceRequest);
        }
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(AddAttendanceActivity.this);
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
        finish();
    }

    @OnClick(R.id.begin_time_layout)
    public void setBeginTime() {
        Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(AddAttendanceActivity.this,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        String hour, min;
                        beginHour = hourOfDay;
                        beginMin = minute;
                        if (hourOfDay < 10)
                            hour = "0" + hourOfDay;
                        else
                            hour = "" + hourOfDay;
                        if (minute < 10)
                            min = "0" + minute;
                        else
                            min = "" + minute;
                        mBeginTimeTv.setText(hour + ":" + min);
                    }
                }
                // 设置初始时间
                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                // true表示采用24小时制
                true).show();
    }

    @OnClick(R.id.end_time_layout)
    public void setEndTime() {
        Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(AddAttendanceActivity.this,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        String hour, min;
                        endHour = hourOfDay;
                        endMin = minute;
                        if (hourOfDay < 10)
                            hour = "0" + hourOfDay;
                        else
                            hour = "" + hourOfDay;
                        if (minute < 10)
                            min = "0" + minute;
                        else
                            min = "" + minute;
                        mEndTimeTv.setText(hour + ":" + min);
                    }
                }
                // 设置初始时间
                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                // true表示采用24小时制
                true).show();
    }

    @OnClick(R.id.location_layout)
    public void startLocActivity() {
        Intent intent0 = new Intent(AddAttendanceActivity.this, LocationActivity.class);
        startActivityForResult(intent0, 0);
    }

    @OnClick(R.id.right_tv)
    public void confirm() {
        AddAtt();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    locationId = data.getIntExtra("locationId", 0);
                    locationName = data.getStringExtra("locationName");
                    mLocationTv.setText(locationName);
                } else {
                }
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
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