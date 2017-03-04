package com.attendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.attendance.R;
import com.attendance.customview.StatusBarCompat;
import com.attendance.entities.ConstParameter;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 12/20/2016.
 */
public class LocDetailActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.time_tv)
    TextView mTimeTv;
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BaiduMap mBaiduMap;
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private int locId;
    private String time;
    //经纬度
    private double mLatitude;
    private double mLongitude;
    //定位
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_loc_detail);
        initView();
        initData();
        initMap();
    }

    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //设置初始化比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        initLocation();
    }

    /**
     * 定位初始化
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
    }

    private void initData() {
        Intent intent = getIntent();
        locId = intent.getIntExtra("locId", 0);
        time = intent.getStringExtra("time");
        mTimeTv.setText(time);
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitleTv.setText(R.string.attendance_detail);
    }

    @OnClick(R.id.back_iv)
    public void back() {
        setResult(-1);
        finish();
    }

    @OnClick(R.id.request_btn)
    public void request() {
        requestLocation();
    }

    @Override
    public void onBackPressed() {
        setResult(-1);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //定位停止
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 定位到我的位置
     */
    private void ToMyLocation(double mLatitude, double mLongitude) {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
        mBaiduMap.setMyLocationEnabled(true);
    }

    /**
     * 手动请求定位的方法
     */
    public void requestLocation() {
        if (mLocationClient.isStarted()) {
            ToMyLocation(mLatitude, mLongitude);
        } else {
            Log.d("log", "locClient is null or not started");
        }
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取经纬度
            boolean localMode = sharedFileUtil.getBoolean("localMode");
            if (localMode) {
                //本地数据测试
                mLongitude = 113.035561;
                mLatitude = 23.155114;
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);

                //位置信息
                MyLocationData data = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .latitude(mLatitude)
                        .longitude(mLongitude)
                        .build();
                mBaiduMap.setMyLocationData(data);

                //判断首次定位
                if (isFirstIn) {
                    ToMyLocation(mLatitude, mLongitude);
                    isFirstIn = false;
                }

            } else {
                String url = ConstParameter.SERVER_ADDRESS + "/getTude.php";
                VolleyUtil volleyUtil = VolleyUtil.getInstance(LocDetailActivity.this);

                final Map<String, String> getTudeMap = new HashMap<>();
                getTudeMap.put("locationId", locId + "");
                final JSONObject getTudeObject = new JSONObject(getTudeMap);

                JsonObjectRequest mGetTudeRequest = new JsonObjectRequest(Request.Method.POST, url, getTudeObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if (jsonObject.getString("result").equals("success")) {
                                        mLongitude = jsonObject.getDouble("longitude");
                                        mLatitude = jsonObject.getDouble("latitude");
                                        LatLng latLng = new LatLng(mLatitude, mLongitude);
                                        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                                        mBaiduMap.animateMapStatus(msu);

                                        //位置信息
                                        MyLocationData data = new MyLocationData.Builder()
                                                .latitude(mLatitude)
                                                .longitude(mLongitude)
                                                .build();
                                        mBaiduMap.setMyLocationData(data);

                                        //判断首次定位
                                        if (isFirstIn) {
                                            ToMyLocation(mLatitude, mLongitude);
                                            isFirstIn = false;
                                        }
                                    } else {
                                        Toast.makeText(LocDetailActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                            }
                        });
                volleyUtil.addToRequestQueue(mGetTudeRequest);
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
            }

        }
    }
}