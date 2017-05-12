package com.attendance.attendresults;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.attendance.R;
import com.attendance.data.AttendResult;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by peiqin on 2/24/2017.
 */
public class AttendResultsActivity extends AppCompatActivity implements AttendResultsContract.View {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.result_tv)
    SortableTableView mResultTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AttendResultsContract.Presenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_stat);
        initView();
        presenter = new AttendResultsPresenter(this);
        presenter.getAttendResultList();
    }

    private void initView() {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
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
    public void setPresenter(AttendResultsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showTip(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getAttendResultsSuccess(List list) {
        mResultTv.setDataAdapter(new AttendResultsAdapter(AttendResultsActivity.this, list));
    }

    @OnClick(R.id.back_iv)
    public void back() {
        finish();
    }

    private static class MyComparator {

        public static Comparator<AttendResult> getNameComparator() {
            return new NameComparator();
        }

        public static Comparator<AttendResult> getAttendComparator() {
            return new AttendComparator();
        }

        public static Comparator<AttendResult> getEarlyComparator() {
            return new EarlyComparator();
        }

        public static Comparator<AttendResult> getLateComparator() {
            return new LateComparator();
        }

        public static Comparator<AttendResult> getSumComparator() {
            return new SumComparator();
        }

        private static class NameComparator implements Comparator<AttendResult> {

            @Override
            public int compare(AttendResult r1, AttendResult r2) {
                return r1.getName().compareTo(r2.getName());
            }
        }

        private static class AttendComparator implements Comparator<AttendResult> {

            @Override
            public int compare(AttendResult r1, AttendResult r2) {
                return r1.getAttend().compareTo(r2.getAttend());
            }
        }

        private static class EarlyComparator implements Comparator<AttendResult> {

            @Override
            public int compare(AttendResult r1, AttendResult r2) {
                return r1.getEarly().compareTo(r2.getEarly());
            }
        }

        private static class LateComparator implements Comparator<AttendResult> {

            @Override
            public int compare(AttendResult r1, AttendResult r2) {
                return r1.getLate().compareTo(r2.getLate());
            }
        }

        private static class SumComparator implements Comparator<AttendResult> {

            @Override
            public int compare(AttendResult r1, AttendResult r2) {
                return r1.getSum().compareTo(r2.getSum());
            }
        }

    }

}