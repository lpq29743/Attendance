package com.attendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.attendance.R;
import com.attendance.adapter.CourseAdapter;
import com.attendance.contract.MainContract;
import com.attendance.customview.StatusBarCompat;
import com.attendance.entities.CourseBean;
import com.attendance.presenter.MainPresenter;
import com.attendance.utils.SharedFileUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by peiqin on 7/28/2016.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, MainContract.View {

    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.course_lv)
    ListView mCourseLv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private CourseAdapter courseAdapter;
    private SharedFileUtil sharedFileUtil;
    private MainContract.Presenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        presenter = new MainPresenter(this);
        presenter.getCourseList();
    }

    private void initView() {
        ButterKnife.bind(this);

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitle(R.string.course_list);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        sharedFileUtil = new SharedFileUtil();
        String username = sharedFileUtil.getString("username");
        ((TextView) headerLayout.findViewById(R.id.name_tv)).setText(R.string.fake_teacher);;
        ((TextView) headerLayout.findViewById(R.id.username_tv)).setText(username);
        initContentView();
    }

    private void initContentView() {
        initSwipeLayout();
        initListView();
    }

    private void initSwipeLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setDistanceToTriggerSync(200);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    private void initListView() {

        courseAdapter = new CourseAdapter(this);
        mCourseLv.setAdapter(courseAdapter);
        mEmptyTv.setText(getString(R.string.teacher_add_course));
        mCourseLv.setEmptyView(mEmptyTv);

        //ListView点击事件
        mCourseLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseName = ((CourseBean) courseAdapter.getItem(position)).getName();
                Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                intent.putExtra("name", courseName);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    public void onRefresh() {
        presenter.getCourseList();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showTip(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void stopRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void getCourseSuccess() {
        courseAdapter.setCourseList();
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            sharedFileUtil.putBoolean("hasLogin", false);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.getCourseList();
    }

}