package com.attendance.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.attendance.R;
import com.attendance.adapter.CourseTeaAdapter;
import com.attendance.customview.StatusBarCompat;
import com.attendance.entities.ConstParameter;
import com.attendance.entities.CourseTeaBean;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 10/16/2016.
 */
public class SearchCourseActivity extends AppCompatActivity {

    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.search_et)
    EditText mSearchEt;
    @BindView(R.id.search_lv)
    ListView mSearchLv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private CourseTeaAdapter adapter;
    private ProgressDialog progressDialog;
    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private MyEditorActionListener myEditorActionListener = new MyEditorActionListener();
    private int i = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSearchEt.setOnEditorActionListener(myEditorActionListener);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) mSearchEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mSearchEt, 0);
            }

        }, 300);

        //ListView设置适配器
        adapter = new CourseTeaAdapter(this);
        mSearchLv.setAdapter(adapter);

        //ListView初始化
        mEmptyTv.setText(getString(R.string.search_hint));
        mSearchLv.setEmptyView(mEmptyTv);

        //ListView点击事件
        mSearchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                Boolean netStatus = netWorkUtils.checkNetWorkEx(SearchCourseActivity.this);
                if (!netStatus) {
                    Toast.makeText(SearchCourseActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchCourseActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("是否选择该课程");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            int courseId = ((CourseTeaBean) adapter.getItem(pos)).getId();
                            addCourse(courseId);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg0) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    /**
     * 搜索课程
     */
    public void searchCourse() {

        showProgressDialog("搜索中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            //本地数据测试
            List<Map<String, String>> data = new ArrayList<>();
            List<CourseTeaBean> result = new ArrayList<>();
            String[] cname = {"编译原理", "计算机程序设计", "职业规划"};
            String[] tname = {"唐程", "米原峰", "欧阳业规"};
            for (int i = 0; i < cname.length; i++) {
                Map<String, String> map = new HashMap<>();
                map.put("courseId", i + "");
                map.put("courseName", cname[i]);
                map.put("teacherName", tname[i]);
                data.add(map);
            }

            String keyword = mSearchEt.getText().toString();
            for (int i = 0; i < data.size(); i++) {
                Map<String, String> map = data.get(i);
                if (map.get("courseName").contains(keyword)) {
                    result.add(new CourseTeaBean(Integer.valueOf(map.get("courseId")), map.get("courseName"), map.get("teacherName")));
                    continue;
                } else if (map.get("teacherName").contains(keyword)) {
                    result.add(new CourseTeaBean(Integer.valueOf(map.get("courseId")), map.get("courseName"), map.get("teacherName")));
                }
            }

            progressDialog.cancel();

            adapter.setSearchList(result);
            adapter.notifyDataSetChanged();

            if (result.isEmpty()) {
                mEmptyTv.setText(R.string.search_empty);
            }

        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/searchCourse.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(SearchCourseActivity.this);

            String keyword = mSearchEt.getText().toString();

            final Map<String, String> getSearchListMap = new HashMap<>();
            getSearchListMap.put("keyword", keyword);
            JSONObject getSearchListObject = new JSONObject(getSearchListMap);

            JsonObjectRequest mGetSearchListRequest = new JsonObjectRequest(Request.Method.POST, url, getSearchListObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    List<CourseTeaBean> result = new ArrayList<>();
                                    JSONArray getSearchListArray = jsonObject.getJSONArray("searchList");
                                    for (int i = 0; i < getSearchListArray.length(); i++) {
                                        JSONObject getSearchListObject = getSearchListArray.getJSONObject(i);
                                        result.add(new CourseTeaBean(Integer.valueOf(
                                                getSearchListObject.getString("courseId")),
                                                getSearchListObject.getString("courseName"),
                                                getSearchListObject.getString("teacherName")));
                                    }
                                    progressDialog.cancel();
                                    adapter.setSearchList(result);
                                    adapter.notifyDataSetChanged();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    progressDialog.cancel();
                                    Toast.makeText(SearchCourseActivity.this, "FAILED", Toast.LENGTH_LONG).show();
                                    mEmptyTv.setText(R.string.search_empty);
                                } else if (jsonObject.getString("result").equals("error")) {
                                    progressDialog.cancel();
                                    Toast.makeText(SearchCourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    mEmptyTv.setText(R.string.search_empty);
                                } else {
                                    progressDialog.cancel();
                                    Toast.makeText(SearchCourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    mEmptyTv.setText(R.string.search_empty);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.cancel();
                    mEmptyTv.setText(R.string.search_empty);
                }
            });
            volleyUtil.addToRequestQueue(mGetSearchListRequest);
        }
    }

    /**
     * 添加课程
     */
    public void addCourse(int courseId) {

        showProgressDialog("添加中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            progressDialog.cancel();
        } else {
            // 服务器端添加课程
            String url = ConstParameter.SERVER_ADDRESS + "/addCourse.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(SearchCourseActivity.this);

            String username = sharedFileUtil.getString("username");
            Boolean isTeacher = sharedFileUtil.getBoolean("isTeacher");
            Map<String, String> addCourseMap = new HashMap<>();
            addCourseMap.put("username", username);
            addCourseMap.put("isTeacher", isTeacher + "");
            addCourseMap.put("courseId", courseId + "");
            final JSONObject addCourseObject = new JSONObject(addCourseMap);

            JsonObjectRequest mAddCourseRequest = new JsonObjectRequest(Request.Method.POST, url, addCourseObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            progressDialog.cancel();
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    Toast.makeText(SearchCourseActivity.this, ConstParameter.ADD_SUCCESS, Toast.LENGTH_SHORT).show();
                                    setResult(0);
                                    finish();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    Toast.makeText(SearchCourseActivity.this, ConstParameter.ADD_FAILED, Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(SearchCourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SearchCourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.cancel();
                            Toast.makeText(SearchCourseActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
            volleyUtil.addToRequestQueue(mAddCourseRequest);
        }
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(SearchCourseActivity.this);
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

    @OnClick(R.id.search_iv)
    public void search() {
        if (!mSearchEt.getText().toString().equals("")) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
            Boolean netStatus = netWorkUtils.checkNetWorkEx(SearchCourseActivity.this);
            if (!netStatus) {
                Toast.makeText(SearchCourseActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
            } else {
                searchCourse();
            }
        }

    }

    private class MyEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.search_et:
                    if (!mSearchEt.getText().toString().equals("")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
                        Boolean netStatus = netWorkUtils.checkNetWorkEx(SearchCourseActivity.this);
                        if (!netStatus) {
                            Toast.makeText(SearchCourseActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                        } else {
                            searchCourse();
                        }
                    }
                    break;
            }
            return true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setResult(-1);
        finish();
        super.onBackPressed();
    }

}