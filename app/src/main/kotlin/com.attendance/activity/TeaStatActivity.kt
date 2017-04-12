package com.attendance.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import android.widget.Toast

import com.attendance.R
import com.attendance.adapter.ResultDataAdapter
import com.attendance.contract.TeaStatContract
import com.attendance.customview.StatusBarCompat
import com.attendance.entities.ResultBean
import com.attendance.presenter.TeaStatPresenter

import java.util.Comparator

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import de.codecrafters.tableview.SortableTableView
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter

/**
 * Created by peiqin on 2/24/2017.
 */
class TeaStatActivity : AppCompatActivity(), TeaStatContract.View {

    @BindView(R.id.title_tv)
    internal var mTitleTv: TextView? = null
    @BindView(R.id.result_tv)
    internal var mResultTv: SortableTableView<ResultBean>? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null

    private var presenter: TeaStatContract.Presenter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tea_stat)
        initView()
        presenter = TeaStatPresenter(this)
        presenter!!.getStatList()
    }

    private fun initView() {
        ButterKnife.bind(this)

        //设置状态栏颜色，默认是主题色colorPrimary
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary))
        setSupportActionBar(toolbar)
        //隐藏Toolbar的标题
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mTitleTv!!.setText(R.string.attend_result)

        val title = arrayOf("姓名", "出勤", "早退", "迟到", "总计")
        mResultTv!!.headerAdapter = SimpleTableHeaderAdapter(this, *title)
        mResultTv!!.setColumnComparator(0, MyComparator.nameComparator)
        mResultTv!!.setColumnComparator(1, MyComparator.attendComparator)
        mResultTv!!.setColumnComparator(2, MyComparator.earlyComparator)
        mResultTv!!.setColumnComparator(3, MyComparator.lateComparator)
        mResultTv!!.setColumnComparator(4, MyComparator.sumComparator)
    }

    override fun setPresenter(presenter: TeaStatContract.Presenter) {
        this.presenter = presenter
    }

    override fun showTip(tip: String) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show()
    }

    override fun getStatSuccess(list: List<ResultBean>) {
        mResultTv!!.setDataAdapter(ResultDataAdapter(this@TeaStatActivity, list))
    }

    @OnClick(R.id.back_iv)
    fun back() {
        finish()
    }

    private object MyComparator {

        val nameComparator: Comparator<ResultBean>
            get() = NameComparator()

        val attendComparator: Comparator<ResultBean>
            get() = AttendComparator()

        val earlyComparator: Comparator<ResultBean>
            get() = EarlyComparator()

        val lateComparator: Comparator<ResultBean>
            get() = LateComparator()

        val sumComparator: Comparator<ResultBean>
            get() = SumComparator()

        private class NameComparator : Comparator<ResultBean> {

            override fun compare(r1: ResultBean, r2: ResultBean): Int {
                return r1.name.compareTo(r2.name)
            }
        }

        private class AttendComparator : Comparator<ResultBean> {

            override fun compare(r1: ResultBean, r2: ResultBean): Int {
                return r1.attend.compareTo(r2.attend)
            }
        }

        private class EarlyComparator : Comparator<ResultBean> {

            override fun compare(r1: ResultBean, r2: ResultBean): Int {
                return r1.early.compareTo(r2.early)
            }
        }

        private class LateComparator : Comparator<ResultBean> {

            override fun compare(r1: ResultBean, r2: ResultBean): Int {
                return r1.late.compareTo(r2.late)
            }
        }

        private class SumComparator : Comparator<ResultBean> {

            override fun compare(r1: ResultBean, r2: ResultBean): Int {
                return r1.sum.compareTo(r2.sum)
            }
        }

    }

}