package com.attendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.attendance.R;
import com.attendance.adapter.ResultDataAdapter;
import com.attendance.contract.TeaStatContract;
import com.attendance.customview.StatusBarCompat;
import com.attendance.entities.ConstParameter;
import com.attendance.entities.ResultBean;
import com.attendance.presenter.TeaStatPresenter;
import com.attendance.utils.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by peiqin on 2/24/2017.
 */
public class TeaStatActivity extends AppCompatActivity implements TeaStatContract.View {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.result_tv)
    SortableTableView mResultTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TeaStatContract.Presenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_stat);
        initView();
        presenter = new TeaStatPresenter(this);
        presenter.getStatList();
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        //隐藏Toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitleTv.setText(R.string.attend_result);

        String[] title = {"姓名", "出勤", "早退", "迟到", "总计"};
        mResultTv.setHeaderAdapter(new SimpleTableHeaderAdapter(this, title));
        mResultTv.setColumnComparator(0, MyComparator.getNameComparator());
        mResultTv.setColumnComparator(1, MyComparator.getAttendComparator());
        mResultTv.setColumnComparator(2, MyComparator.getEarlyComparator());
        mResultTv.setColumnComparator(3, MyComparator.getLateComparator());
        mResultTv.setColumnComparator(4, MyComparator.getSumComparator());
    }

    @Override
    public void setPresenter(TeaStatContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showTip(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getStatSuccess(List list) {
        mResultTv.setDataAdapter(new ResultDataAdapter(TeaStatActivity.this, list));
    }

    @OnClick(R.id.back_iv)
    public void back() {
        finish();
    }

    private static class MyComparator {

        public static Comparator<ResultBean> getNameComparator() {
            return new NameComparator();
        }

        public static Comparator<ResultBean> getAttendComparator() {
            return new AttendComparator();
        }

        public static Comparator<ResultBean> getEarlyComparator() {
            return new EarlyComparator();
        }

        public static Comparator<ResultBean> getLateComparator() {
            return new LateComparator();
        }

        public static Comparator<ResultBean> getSumComparator() {
            return new SumComparator();
        }

        private static class NameComparator implements Comparator<ResultBean> {

            @Override
            public int compare(ResultBean r1, ResultBean r2) {
                return r1.getName().compareTo(r2.getName());
            }
        }

        private static class AttendComparator implements Comparator<ResultBean> {

            @Override
            public int compare(ResultBean r1, ResultBean r2) {
                return r1.getAttend().compareTo(r2.getAttend());
            }
        }

        private static class EarlyComparator implements Comparator<ResultBean> {

            @Override
            public int compare(ResultBean r1, ResultBean r2) {
                return r1.getEarly().compareTo(r2.getEarly());
            }
        }

        private static class LateComparator implements Comparator<ResultBean> {

            @Override
            public int compare(ResultBean r1, ResultBean r2) {
                return r1.getLate().compareTo(r2.getLate());
            }
        }

        private static class SumComparator implements Comparator<ResultBean> {

            @Override
            public int compare(ResultBean r1, ResultBean r2) {
                return r1.getSum().compareTo(r2.getSum());
            }
        }

    }

}