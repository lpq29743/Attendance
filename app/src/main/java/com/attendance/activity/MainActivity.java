package com.attendance.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.attendance.adapter.CourseAdapter;
import com.attendance.adapter.CourseTeaAdapter;
import com.attendance.customview.CustomDialog;
import com.attendance.customview.StatusBarCompat;
import com.attendance.dao.CourseDao;
import com.attendance.entities.ConstParameter;
import com.attendance.entities.CourseBean;
import com.attendance.entities.CourseTeaBean;
import com.attendance.utils.NetWorkUtil;
import com.attendance.utils.SharedFileUtil;
import com.attendance.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peiqin on 7/28/2016.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.username_tv)
    TextView mUsernameTv;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.course_lv)
    ListView mCourseLv;
    @BindView(R.id.search_or_add)
    ImageView mSearchOrAddIv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.swipeRefreshLayout_listView)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Boolean isTeacher;
    private CourseAdapter teaAdapter;
    private CourseTeaAdapter stuAdapter;
    private NetWorkUtil netWorkUtils = new NetWorkUtil();
    private ProgressDialog progressDialog;
    private SharedFileUtil sharedFileUtil = new SharedFileUtil();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Boolean netStatus = netWorkUtils.checkNetWorkEx(MainActivity.this);
        if (!netStatus) {
            Toast.makeText(MainActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            getCourseList();
        }
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

        String name = sharedFileUtil.getString("name");
        String username = sharedFileUtil.getString("username");
        isTeacher = sharedFileUtil.getBoolean("isTeacher");
        if (isTeacher) {
            mNameTv.setText(name + "老师");
            initContentView();
            mSearchOrAddIv.setImageResource(android.R.drawable.ic_menu_add);
        } else {
            mNameTv.setText(name + "同学");
            initContentView();
            mSearchOrAddIv.setImageResource(android.R.drawable.ic_menu_search);
        }
        mUsernameTv.setText(username);
    }

    private void initContentView() {
        initSwipeLayout();
        initListView();
    }

    private void initListView() {

        //ListView初始化
        if (isTeacher) {
            //ListView设置适配器
            teaAdapter = new CourseAdapter(this);
            mCourseLv.setAdapter(teaAdapter);
            mEmptyTv.setText(getString(R.string.teacher_add_course));
            mCourseLv.setEmptyView(mEmptyTv);

            //ListView点击事件
            mCourseLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int course_id = ((CourseBean) teaAdapter.getItem(position)).getId();
                    String course_name = ((CourseBean) teaAdapter.getItem(position)).getName();
                    String teacher_name = sharedFileUtil.getString("name");
                    Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                    intent.putExtra("id", course_id);
                    intent.putExtra("name", course_name);
                    intent.putExtra("teacherName", teacher_name);
                    startActivityForResult(intent, 0);
                }
            });

            //ListView长按事件
            mCourseLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("是否删除该课程");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            //向服务器发送删除请求
                            Boolean netStatus = netWorkUtils.checkNetWorkEx(MainActivity.this);
                            if (!netStatus) {
                                Toast.makeText(MainActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                delCoursePost(((CourseBean) teaAdapter.getItem(position)).getId());
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg0) {
                            Toast.makeText(MainActivity.this, "删除取消", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        } else {

            //ListView设置适配器
            stuAdapter = new CourseTeaAdapter(this);
            mCourseLv.setAdapter(stuAdapter);
            mEmptyTv.setText(getString(R.string.student_search_course));
            mCourseLv.setEmptyView(mEmptyTv);

            //ListView点击事件
            mCourseLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int course_id = ((CourseTeaBean) stuAdapter.getItem(position)).getId();
                    String course_name = ((CourseTeaBean) stuAdapter.getItem(position)).getName();
                    String teacher_name = ((CourseTeaBean) stuAdapter.getItem(position)).getTeacherName();
                    Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                    intent.putExtra("id", course_id);
                    intent.putExtra("name", course_name);
                    intent.putExtra("teacherName", teacher_name);
                    startActivityForResult(intent, 0);
                }
            });

            //ListView长按事件
            mCourseLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("是否删除该课程");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            //向服务器发送删除请求
                            Boolean netStatus = netWorkUtils.checkNetWorkEx(MainActivity.this);
                            if (!netStatus) {
                                Toast.makeText(MainActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                delCoursePost(((CourseTeaBean) stuAdapter.getItem(position)).getId());
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg0) {
                            Toast.makeText(MainActivity.this, "删除取消", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

    }

    private void initSwipeLayout() {
        //设置下拉刷新界面
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT); // 设置圆圈的大小
    }

    public void addCourse() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(MainActivity.this);
        View view = mLayoutInflater.inflate(R.layout.dialog_custom, null);

        CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this, R.style.NoDialogTitle);
        builder.setTitle(getString(R.string.add_course));
        builder.setView(view);
        final EditText mCourseNameEt = (EditText) view.findViewById(R.id.text_et);

        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                //向服务器发送添加请求
                String courseName = mCourseNameEt.getText().toString();
                Boolean netStatus = netWorkUtils.checkNetWorkEx(MainActivity.this);
                if (courseName.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入课程名称！", Toast.LENGTH_LONG).show();
                    return;
                } else if ((!netStatus)) {
                    Toast.makeText(MainActivity.this, "网络状况不佳，请检查网络情况", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    addCoursePost(mCourseNameEt.getText().toString());
                }
            }
        });

        builder.setNegativeButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg0) {
                Toast.makeText(MainActivity.this, "取消创建", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        //显示对话框
        builder.create().show();

        //自动弹出软键盘
        mCourseNameEt.setFocusable(true);
        mCourseNameEt.setFocusableInTouchMode(true);
        mCourseNameEt.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) MainActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }

        }, 200);
    }

    /*
     * 监听器SwipeRefreshLayout.OnRefreshListener中的方法，当下拉刷新后触发
     */
    public void onRefresh() {
        initData();
    }

    /**
     * 获取课程
     */
    public void getCourseList() {

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            //本地数据测试
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/getCourseList.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(MainActivity.this);

            String username = sharedFileUtil.getString("username");

            final Map<String, String> getCourseListMap = new HashMap<>();
            getCourseListMap.put("username", username);
            getCourseListMap.put("isTeacher", isTeacher + "");
            JSONObject getCourseListObject = new JSONObject(getCourseListMap);

            JsonObjectRequest mGetCourseListRequest = new JsonObjectRequest(Request.Method.POST, url, getCourseListObject,
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
                                    JSONArray getCourseListArray = jsonObject.getJSONArray("courseList");
                                    CourseDao courseDao = new CourseDao(MainActivity.this);
                                    courseDao.delAll();
                                    if (isTeacher) {
                                        for (int i = 0; i < getCourseListArray.length(); i++) {
                                            JSONObject getCourseListObject = getCourseListArray.getJSONObject(i);
                                            courseDao.insert(getCourseListObject.getInt("courseId"), getCourseListObject.getString("courseName"), "");
                                        }
                                        teaAdapter.setCourseList();
                                        teaAdapter.notifyDataSetChanged();
                                    } else {
                                        for (int i = 0; i < getCourseListArray.length(); i++) {
                                            JSONObject getCourseListObject = getCourseListArray.getJSONObject(i);
                                            courseDao.insert(getCourseListObject.getInt("courseId"), getCourseListObject.getString("courseName"),
                                                    getCourseListObject.getString("teacherName"));
                                        }
                                        stuAdapter.setCourseList();
                                        stuAdapter.notifyDataSetChanged();
                                    }
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    CourseDao courseDao = new CourseDao(MainActivity.this);
                                    courseDao.delAll();
                                    if (isTeacher) {
                                        teaAdapter.setCourseList();
                                        teaAdapter.notifyDataSetChanged();
                                    } else {
                                        stuAdapter.setCourseList();
                                        stuAdapter.notifyDataSetChanged();
                                    }
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
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
            volleyUtil.addToRequestQueue(mGetCourseListRequest);
        }
    }

    /**
     * 添加课程
     */
    public void addCoursePost(final String name) {

        showProgressDialog("添加中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            // 本地数据测试
        } else {
            // 服务器端添加课程
            String url = ConstParameter.SERVER_ADDRESS + "/addCourse.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(MainActivity.this);

            String username = sharedFileUtil.getString("username");
            Map<String, String> addCourseMap = new HashMap<>();
            addCourseMap.put("username", username);
            addCourseMap.put("isTeacher", isTeacher + "");
            addCourseMap.put("courseName", name);
            final JSONObject addCourseObject = new JSONObject(addCourseMap);

            JsonObjectRequest mAddCourseRequest = new JsonObjectRequest(Request.Method.POST, url, addCourseObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            progressDialog.cancel();
                            try {
                                if (jsonObject.getString("result").equals("success")) {
                                    Toast.makeText(MainActivity.this, ConstParameter.ADD_SUCCESS, Toast.LENGTH_SHORT).show();
                                    CourseDao courseDao = new CourseDao(MainActivity.this);
                                    courseDao.insert(jsonObject.getInt("courseId"), name, "");
                                    teaAdapter.setCourseList();
                                    teaAdapter.notifyDataSetChanged();
                                } else if (jsonObject.getString("result").equals("failed")) {
                                    Toast.makeText(MainActivity.this, ConstParameter.ADD_FAILED, Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.getString("result").equals("error")) {
                                    Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
            volleyUtil.addToRequestQueue(mAddCourseRequest);
        }
    }

    /**
     * 删除课程
     */
    public void delCoursePost(final int id) {

        showProgressDialog("删除中");

        boolean localMode = sharedFileUtil.getBoolean("localMode");
        if (localMode) {
            // 本地数据测试
        } else {
            String url = ConstParameter.SERVER_ADDRESS + "/delCourse.php";
            VolleyUtil volleyUtil = VolleyUtil.getInstance(MainActivity.this);
            StringRequest mDeleteCourseRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.cancel();
                    if (response.equals("success")) {
                        Toast.makeText(MainActivity.this, ConstParameter.DEL_SUCCESS, Toast.LENGTH_SHORT).show();
                        CourseDao courseDao = new CourseDao(MainActivity.this);
                        courseDao.del(id);
                        if (isTeacher) {
                            teaAdapter.setCourseList();
                            teaAdapter.notifyDataSetChanged();
                        } else {
                            stuAdapter.setCourseList();
                            stuAdapter.notifyDataSetChanged();
                        }
                    } else if (response.equals("failed")) {
                        Toast.makeText(MainActivity.this, ConstParameter.DEL_FAILED, Toast.LENGTH_SHORT).show();
                    } else if (response.equals("error")) {
                        Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, ConstParameter.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String username = sharedFileUtil.getString("username");
                    Map<String, String> deleteCourseMap = new HashMap<>();
                    deleteCourseMap.put("username", username);
                    deleteCourseMap.put("courseId", id + "");
                    deleteCourseMap.put("isTeacher", isTeacher + "");
                    return deleteCourseMap;
                }
            };
            volleyUtil.addToRequestQueue(mDeleteCourseRequest);
        }
    }

    private void showProgressDialog(String message) {
        //创建ProgressDialog对象
        progressDialog = new ProgressDialog(MainActivity.this);
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

    @OnClick(R.id.search_or_add)
    public void searchOrAdd() {
        if (isTeacher) {
            addCourse();
        } else {
            Intent intent = new Intent(MainActivity.this, SearchCourseActivity.class);
            startActivityForResult(intent, 1);
        }
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

        if (id == R.id.about_software) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
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
        initData();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        if (isTeacher) {
            teaAdapter.setCourseList();
            teaAdapter.notifyDataSetChanged();
        } else {
            stuAdapter.setCourseList();
            stuAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

}