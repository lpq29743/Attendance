package com.attendance.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.attendance.R;
import com.attendance.adapter.AttDetailAdapter;
import com.attendance.customview.StatusBarCompat;
import com.attendance.dao.AttDetailDao;
import com.attendance.entities.AttDetailBean;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 9/28/2016.
 */
public class AttDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.right_tv)
    TextView mAddTv;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.time_tv)
    TextView mTimeTv;
    @BindView(R.id.day_tv)
    TextView mDayTv;
    @BindView(R.id.att_detail_lv)
    ListView mAttDetailLv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipeRefreshLayout_listView)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog progressDialog;
    private AttDetailAdapter adapter;
    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private int course_id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att_detail);
        initView();
        initData();
    }

    private void initData() {

        //自动下拉刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        //从服务端加载课程数据
        Boolean netStatus = netWorkUtils.checkNetWorkEx(AttDetailActivity.this);
        if (!netStatus) {
            Toast.makeText(AttDetailActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            getAttDetailList();
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        initToolBar();
        initTextView();
        initClickListener();
        initSwipeLayout();
        initListView();
    }

    private void initToolBar() {
        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initTextView() {
        mTitleTv.setText(R.string.attendance_list);
        if (sharedFileUtil.getBoolean("isTeacher"))
            mAddTv.setText(R.string.add);
    }

    private void initClickListener() {
        if (!sharedFileUtil.getBoolean("isTeacher")) {
            mAddTv.setClickable(false);
        }
    }

    private void initSwipeLayout() {
        //设置下拉刷新界面
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT); // 设置圆圈的大小
    }

    private void initListView() {
        Intent intent = getIntent();
        course_id = intent.getIntExtra("course_id", 0);

        //ListView设置适配器
        adapter = new AttDetailAdapter(this);
        mAttDetailLv.setAdapter(adapter);

        //ListView初始化
        if (sharedFileUtil.getBoolean("isTeacher"))
            mEmptyTv.setText(getString(R.string.tea_empty_att));
        else
            mEmptyTv.setText(getString(R.string.stu_empty_att));
        mAttDetailLv.setEmptyView(mEmptyTv);

        //ListView点击事件
        mAttDetailLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int locId = ((AttDetailBean) adapter.getItem(position)).getLocationId();
                Intent intent = new Intent(AttDetailActivity.this, LocDetailActivity.class);
                intent.putExtra("time", mDayTv.getText().toString() + "     " + mTimeTv.getText().toString());
                intent.putExtra("locId", locId);
                startActivity(intent);
            }
        });

        //ListView长按事件
        mAttDetailLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AttDetailActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否删除该考勤");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        //向服务器发送删除请求
                        Boolean netStatus = netWorkUtils.checkNetWorkEx(AttDetailActivity.this);
                        if (!netStatus) {
                            Toast.makeText(AttDetailActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            delAttDetailPost(((AttDetailBean) adapter.getItem(position)).getId());
                        }

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg0) {
                        Toast.makeText(AttDetailActivity.this, "删除取消", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

    }

    /**
     * 获取考勤列表
     */
    private void getAttDetailList() {

        boolean localMode = sharedFileUtil.getBoolean("localMode");

        if (localMode) {
            //本地数据测试
            AttDetailDao attDetailDao = new AttDetailDao(this);
            attDetailDao.delAll();
            for (int i = 0; i < 5; i++) {
                Random random = new Random();
                int num = random.nextInt(200);
                attDetailDao.insert(num, "9:00", "12:00", 1, course_id, num, "B" + num);
            }
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            adapter.setAttDetailList(course_id);
            adapter.notifyDataSetChanged();
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/getAttDetailList.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(AttDetailActivity.this);

            final Map<String, String> getDetailListMap = new HashMap<>();
            getDetailListMap.put("courseId", course_id + "");
            final JSONObject getDetailListObject = new JSONObject(getDetailListMap);

            JsonObjectRequest mGetDetailListRequest = new JsonObjectRequest(Request.Method.POST, url, getDetailListObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            mSwipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    JSONArray getDetailListArray = jsonObject.getJSONArray("attDetailList");
                                    AttDetailDao attDetailDao = new AttDetailDao(AttDetailActivity.this);
                                    attDetailDao.delAll();
                                    for (int i = 0; i < getDetailListArray.length(); i++) {
                                        JSONObject getDetailObject = getDetailListArray.getJSONObject(i);
                                        int detailId = getDetailObject.getInt("detailId");
                                        String beginTime = getDetailObject.getString("beginTime");
                                        String endTime = getDetailObject.getString("endTime");
                                        int dayOfWeek = getDetailObject.getInt("dayOfWeek");
                                        int locationId = getDetailObject.getInt("locationId");
                                        String locationName = getDetailObject.getString("locationName");
                                        attDetailDao.insert(detailId, beginTime, endTime, dayOfWeek, course_id, locationId, locationName);
                                    }
                                    adapter.setAttDetailList(course_id);
                                    adapter.notifyDataSetChanged();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    AttDetailDao locationDao = new AttDetailDao(AttDetailActivity.this);
                                    locationDao.delAll();
                                    adapter.setAttDetailList(course_id);
                                    adapter.notifyDataSetChanged();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(AttDetailActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AttDetailActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
            volleyUtil.addToRequestQueue(mGetDetailListRequest);
        }
    }

    /**
     * 删除考勤
     */
    public void delAttDetailPost(final int id) {

        showProgressDialog("删除中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            // 本地删除课程
            AttDetailDao attDetailDao = new AttDetailDao(this);
            attDetailDao.del(id);
            progressDialog.cancel();
            adapter.setAttDetailList(course_id);
            adapter.notifyDataSetChanged();
            Toast.makeText(AttDetailActivity.this, ConstParameter.DEL_SUCCESS, Toast.LENGTH_SHORT).show();
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/delAttDetail.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(AttDetailActivity.this);
            StringRequest mDeleteCourseRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.cancel();
                    if (response.equals("success")) {
                        Toast.makeText(AttDetailActivity.this, ConstParameter.DEL_SUCCESS, Toast.LENGTH_SHORT).show();
                        AttDetailDao attDetailDao = new AttDetailDao(AttDetailActivity.this);
                        attDetailDao.del(id);
                        adapter.setAttDetailList(course_id);
                        adapter.notifyDataSetChanged();
                    } else if (response.equals("failed")) {
                        Toast.makeText(AttDetailActivity.this, ConstParameter.DEL_FAILED, Toast.LENGTH_SHORT).show();
                    } else if (response.equals("error")) {
                        Toast.makeText(AttDetailActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AttDetailActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.cancel();
                    Toast.makeText(AttDetailActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> deleteCourseMap = new HashMap<>();
                    deleteCourseMap.put("detailId", id + "");
                    return deleteCourseMap;
                }
            };
            volleyUtil.addToRequestQueue(mDeleteCourseRequest);
        }
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(AttDetailActivity.this);
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

    @Override
    public void onRefresh() {
        initData();
    }

    @OnClick(R.id.back_iv)
    public void back() {
        finish();
    }

    @OnClick(R.id.right_tv)
    public void add() {
        Intent intent = new Intent(AttDetailActivity.this, AddAttendanceActivity.class);
        intent.putExtra("course_id", course_id);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    adapter.setAttDetailList(course_id);
                    adapter.notifyDataSetChanged();
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

}