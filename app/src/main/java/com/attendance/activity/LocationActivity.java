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
import com.attendance.adapter.LocationAdapter;
import com.attendance.customview.StatusBarCompat;
import com.attendance.entities.ConstParameter;
import com.attendance.entities.LocationBean;
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
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 9/28/2016.
 */
public class LocationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.right_tv)
    TextView mAddTv;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.location_lv)
    ListView mLocationLv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipeRefreshLayout_listView)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog progressDialog;
    private LocationAdapter adapter;
    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();

    private List<LocationBean> locationList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initView();
        initData();
    }

    private void initData() {
        locationList = new ArrayList<>();

        //自动下拉刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        //从服务端加载课程数据
        Boolean netStatus = netWorkUtils.checkNetWorkEx(LocationActivity.this);
        if (!netStatus) {
            Toast.makeText(LocationActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            getLocationList();
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        initToolBar();
        initTextView();
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
        mTitleTv.setText(R.string.location_list);
        mAddTv.setText(getString(R.string.add));
    }

    private void initSwipeLayout() {
        //设置下拉刷新界面
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT); // 设置圆圈的大小
    }

    private void initListView() {
        //ListView设置适配器
        adapter = new LocationAdapter(this);
        mLocationLv.setAdapter(adapter);

        //ListView初始化
        mEmptyTv.setText(getString(R.string.system_add_location));
        mLocationLv.setEmptyView(mEmptyTv);

        //ListView点击事件
        mLocationLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否选择该地点");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        int locationId = ((LocationBean) adapter.getItem(pos)).getId();
                        String locationName = ((LocationBean) adapter.getItem(pos)).getName();
                        Intent intent = new Intent(LocationActivity.this, AddAttendanceActivity.class);
                        intent.putExtra("locationId", locationId);
                        intent.putExtra("locationName", locationName);
                        setResult(0, intent);
                        finish();
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
        });

        //ListView长按事件
        mLocationLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否删除该地点");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        //向服务器发送删除请求
                        Boolean netStatus = netWorkUtils.checkNetWorkEx(LocationActivity.this);
                        if (!netStatus) {
                            Toast.makeText(LocationActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            delLocationPost(((LocationBean) adapter.getItem(position)).getId());
                        }

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg0) {
                        Toast.makeText(LocationActivity.this, "删除取消", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

    }

    public void addLocation() {
        Intent intent = new Intent(LocationActivity.this, AddLocationActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * 获取地点列表
     */
    private void getLocationList() {
        locationList.removeAll(locationList);

        boolean localMode = sharedFileUtil.getBoolean("localMode");

        if (localMode) {
            //本地数据测试
            for (int i = 0; i < 5; i++) {
                Random random = new Random();
                int num = random.nextInt(200);
                locationList.add(new LocationBean(num, "B" + num));
            }
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            adapter.setLocationList(locationList);
            adapter.notifyDataSetChanged();
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/getLocationList.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(LocationActivity.this);

            final Map<String, String> getLocationListMap = new HashMap<>();
            getLocationListMap.put("teacherId", sharedFileUtil.getString("username"));
            JSONObject getLocationListObject = new JSONObject(getLocationListMap);

            JsonObjectRequest mGetLocationListRequest = new JsonObjectRequest(Request.Method.POST, url, getLocationListObject,
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
                                    JSONArray getLocationListArray = jsonObject.getJSONArray("locationList");
                                    for (int i = 0; i < getLocationListArray.length(); i++) {
                                        JSONObject getLocationListObject = getLocationListArray.getJSONObject(i);
                                        locationList.add(new LocationBean(getLocationListObject.getInt("locationId"), getLocationListObject.getString("locationName")));
                                    }
                                    adapter.setLocationList(locationList);
                                    adapter.notifyDataSetChanged();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    adapter.setLocationList(locationList);
                                    adapter.notifyDataSetChanged();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(LocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
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
            volleyUtil.addToRequestQueue(mGetLocationListRequest);
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @OnClick(R.id.back_iv)
    public void back() {
        setResult(-1);
        finish();
    }

    @OnClick(R.id.right_tv)
    public void confirm() {
        addLocation();
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(LocationActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    initData();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 删除地点
     */
    public void delLocationPost(final int id) {

        showProgressDialog("删除中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            // 本地数据测试
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/delLocation.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(LocationActivity.this);
            StringRequest mDeleteLocationRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Toast.makeText(LocationActivity.this, ConstParameter.DEL_SUCCESS, Toast.LENGTH_SHORT).show();
                        initData();
                    } else if (response.equals("failed")) {
                        Toast.makeText(LocationActivity.this, ConstParameter.DEL_FAILED, Toast.LENGTH_SHORT).show();
                    } else if (response.equals("error")) {
                        Toast.makeText(LocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.cancel();
                    Toast.makeText(LocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> deleteLocationMap = new HashMap<>();
                    deleteLocationMap.put("locationId", id + "");
                    return deleteLocationMap;
                }
            };
            volleyUtil.addToRequestQueue(mDeleteLocationRequest);
        }
    }

}
