package com.attendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 2/24/2017.
 */
public class StuStatActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.result_tv)
    TextView mResultTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();
    private int course_id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_stat);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitleTv.setText(R.string.attend_result);
        mResultTv.setText(R.string.empty_result);

    }

    private void initData() {

        Intent intent = getIntent();
        course_id = intent.getIntExtra("course_id", 0);
        Boolean netStatus = netWorkUtils.checkNetWorkEx(StuStatActivity.this);
        if (!netStatus) {
            Toast.makeText(StuStatActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/getStuStat.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(StuStatActivity.this);

            final Map<String, String> getStatListMap = new HashMap<>();
            getStatListMap.put("username", sharedFileUtil.getString("username"));
            getStatListMap.put("courseId", course_id + "");
            final JSONObject getStatListObject = new JSONObject(getStatListMap);

            JsonObjectRequest mGetStatListRequest = new JsonObjectRequest(Request.Method.POST, url, getStatListObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    String result = "您出勤过" + jsonObject.getString("attend") + "次，早退过"
                                            + jsonObject.getString("early") + "次，迟到过"
                                            + jsonObject.getString("late") + "次，总计"
                                            + jsonObject.getString("sum") + "次";
                                    mResultTv.setText(result);
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    Toast.makeText(StuStatActivity.this, ConstParameter.GET_FAILED, Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(StuStatActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(StuStatActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(StuStatActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                }
            });
            volleyUtil.addToRequestQueue(mGetStatListRequest);
        }
    }

    @OnClick(R.id.back_iv)
    public void back() {
        finish();
    }

}