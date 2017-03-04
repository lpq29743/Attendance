package com.attendance.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
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
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
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
public class AddLocationActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.right_tv)
    TextView mConfirmTv;
    @BindView(R.id.location_name_et)
    EditText mLocNameEt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private NetWorkUtil netWorkUtils = new NetWorkUtil();

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private String locationName;

    //经纬度
    private double mLatitude;
    private double mLongitude;
    private boolean isSelectLoc = false;

    //定位
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;

    //覆盖物
    private BitmapDescriptor mMarker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_add_location);
        initView();
        initMap();
    }

    private void initMap() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        //设置初始化比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        initLocation();
        //地图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isSelectLoc = true;
                //设置图标
                mMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
                //清理图层
                mBaiduMap.clear();
                //经纬度
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                LatLng latLng0 = new LatLng(mLatitude, mLongitude);
                //图标
                OverlayOptions options = new MarkerOptions().position(latLng0).icon(mMarker).zIndex(5);
                mBaiduMap.addOverlay(options);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng0);
                mBaiduMap.setMapStatus(msu);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 定位初始化
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        //坐标类型
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        //可选，默认高精度，设置定位模式，高精度/低功耗/仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //GPS
        option.setOpenGps(true);
        //定位间隔
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitleTv.setText(R.string.add_location);
        mConfirmTv.setText(R.string.confirm);

    }

    private void AddLoc() {
        // 数据获取
        locationName = mLocNameEt.getText().toString();

        // 数据校验
        Boolean netStatus = netWorkUtils.checkNetWorkEx(AddLocationActivity.this);
        if (locationName.equals("")) {
            Toast.makeText(this, "请填写地点名", Toast.LENGTH_SHORT).show();
        } else if (!isSelectLoc) {
            Toast.makeText(this, "请选择地点", Toast.LENGTH_SHORT).show();
        } else if (!netStatus) {
            Toast.makeText(this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
        } else {
            // 服务器访问
            AddLocPost();
        }

    }

    private void AddLocPost() {
        showProgressDialog("添加中");
        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            progressDialog.cancel();
        } else {
            // 服务器端添加考勤
            String url = ConstParameter.SERVER_ADDRESS + "/addLocation.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(AddLocationActivity.this);

            Map<String, String> AddLocMap = new HashMap<>();
            AddLocMap.put("teacherId", sharedFileUtil.getString("username"));
            AddLocMap.put("locationName", locationName);
            AddLocMap.put("latitude", mLatitude + "");
            AddLocMap.put("longitude", mLongitude + "");
            JSONObject AddLocObject = new JSONObject(AddLocMap);

            JsonObjectRequest mAddLocRequest = new JsonObjectRequest(Request.Method.POST, url, AddLocObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            progressDialog.cancel();
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    Toast.makeText(AddLocationActivity.this, ConstParameter.ADD_SUCCESS, Toast.LENGTH_SHORT).show();
                                    setResult(0);
                                    finish();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    Toast.makeText(AddLocationActivity.this, ConstParameter.ADD_FAILED, Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(AddLocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddLocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddLocationActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
            volleyUtil.addToRequestQueue(mAddLocRequest);
        }
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(AddLocationActivity.this);
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
    public void confirm() {
        AddLoc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //判断首次定位
            if (isFirstIn) {
                ToMyLocation(location.getLatitude(), location.getLongitude());
                isFirstIn = false;
            }
        }
    }

}