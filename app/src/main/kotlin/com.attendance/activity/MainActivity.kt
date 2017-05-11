package com.attendance.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import butterknife.ButterKnife

import com.attendance.R
import com.attendance.adapter.CourseAdapter
import com.attendance.contract.MainContract
import com.attendance.customview.StatusBarCompat
import com.attendance.entities.CourseBean
import com.attendance.presenter.MainPresenter
import com.attendance.utils.SharedFileUtil

import butterknife.bindView

/**
 * Created by peiqin on 7/28/2016.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, MainContract.View {

    internal val mEmptyTv: TextView by bindView(R.id.empty_tv)
    internal val mCourseLv: ListView by bindView(R.id.course_lv)
    internal val toolbar: Toolbar by bindView(R.id.toolbar)
    internal val drawer: DrawerLayout by bindView(R.id.drawer_layout)
    internal val navigationView: NavigationView by bindView(R.id.nav_view)
    internal val mSwipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_layout)

    private var courseAdapter: CourseAdapter? = null
    private var sharedFileUtil: SharedFileUtil? = null
    private var presenter: MainContract.Presenter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        presenter = MainPresenter(this)
        presenter!!.getCourseList()
    }

    private fun initView() {
        ButterKnife.bind(this)

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary))
        toolbar.setTitle(R.string.course_list)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        if (drawer != null)
            drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        val headerLayout = navigationView.getHeaderView(0)
        sharedFileUtil = SharedFileUtil()
        val username = sharedFileUtil!!.getString("username")
        (headerLayout.findViewById(R.id.name_tv) as TextView).setText(R.string.fake_teacher)
        (headerLayout.findViewById(R.id.username_tv) as TextView).text = username
        initContentView()
    }

    private fun initContentView() {
        initSwipeLayout()
        initListView()
    }

    private fun initSwipeLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        mSwipeRefreshLayout.setDistanceToTriggerSync(200)
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT)
    }

    private fun initListView() {

        courseAdapter = CourseAdapter(this)
        mCourseLv.adapter = courseAdapter
        mEmptyTv.text = getString(R.string.teacher_add_course)
        mCourseLv.emptyView = mEmptyTv

        //ListView点击事件
        mCourseLv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val courseName = (courseAdapter!!.getItem(position) as CourseBean).name
            val intent = Intent(this@MainActivity, CourseActivity::class.java)
            intent.putExtra("name", courseName)
            startActivityForResult(intent, 0)
        }

    }

    override fun onRefresh() {
        presenter!!.getCourseList()
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun showTip(tip: String) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show()
    }

    override fun startRefresh() {
        mSwipeRefreshLayout.post { mSwipeRefreshLayout.isRefreshing = true }
    }

    override fun stopRefresh() {
        mSwipeRefreshLayout.post { mSwipeRefreshLayout.isRefreshing = false }
    }

    override fun getCourseSuccess() {
        courseAdapter!!.setCourseList()
        courseAdapter!!.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.logout) {
            sharedFileUtil!!.putBoolean("hasLogin", false)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter!!.getCourseList()
    }

}