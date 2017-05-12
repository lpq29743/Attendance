package com.attendance.attendresults

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.ButterKnife

import com.attendance.R

import java.util.Comparator

import butterknife.bindView
import com.attendance.data.AttendResult
import de.codecrafters.tableview.SortableTableView
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter

/**
 * Created by peiqin on 2/24/2017.
 */
class AttendResultsActivity : AppCompatActivity(), AttendResultsContract.View {

    internal val mTitleTv: TextView by bindView(R.id.title_tv)
    internal val mAttendResultTv: SortableTableView<AttendResult> by bindView(R.id.result_tv)
    internal val mBackIv: ImageView by bindView(R.id.back_iv)
    internal val toolbar: Toolbar by bindView(R.id.toolbar)

    private var presenter: AttendResultsContract.Presenter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tea_stat)
        initView()
        presenter = AttendResultsPresenter(this)
        presenter!!.getAttendResultList()
    }

    private fun initView() {
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mTitleTv.setText(R.string.attend_result)

        val title = arrayOf("姓名", "出勤", "早退", "迟到", "总计")
        mAttendResultTv.headerAdapter = SimpleTableHeaderAdapter(this, *title)
        mAttendResultTv.setColumnComparator(0, MyComparator.nameComparator)
        mAttendResultTv.setColumnComparator(1, MyComparator.attendComparator)
        mAttendResultTv.setColumnComparator(2, MyComparator.earlyComparator)
        mAttendResultTv.setColumnComparator(3, MyComparator.lateComparator)
        mAttendResultTv.setColumnComparator(4, MyComparator.sumComparator)

        mBackIv.setOnClickListener {
            finish()
        }

    }

    override fun setPresenter(presenter: AttendResultsContract.Presenter) {
        this.presenter = presenter
    }

    override fun showTip(tip: String) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show()
    }

    override fun getAttendResultsSuccess(list: List<AttendResult>) {
        mAttendResultTv.setDataAdapter(AttendResultsAdapter(this@AttendResultsActivity, list))
    }

    private object MyComparator {

        val nameComparator: Comparator<AttendResult>
            get() = NameComparator()

        val attendComparator: Comparator<AttendResult>
            get() = AttendComparator()

        val earlyComparator: Comparator<AttendResult>
            get() = EarlyComparator()

        val lateComparator: Comparator<AttendResult>
            get() = LateComparator()

        val sumComparator: Comparator<AttendResult>
            get() = SumComparator()

        private class NameComparator : Comparator<AttendResult> {

            override fun compare(r1: AttendResult, r2: AttendResult): Int {
                return r1.name.compareTo(r2.name)
            }
        }

        private class AttendComparator : Comparator<AttendResult> {

            override fun compare(r1: AttendResult, r2: AttendResult): Int {
                return r1.attend.compareTo(r2.attend)
            }
        }

        private class EarlyComparator : Comparator<AttendResult> {

            override fun compare(r1: AttendResult, r2: AttendResult): Int {
                return r1.early.compareTo(r2.early)
            }
        }

        private class LateComparator : Comparator<AttendResult> {

            override fun compare(r1: AttendResult, r2: AttendResult): Int {
                return r1.late.compareTo(r2.late)
            }
        }

        private class SumComparator : Comparator<AttendResult> {

            override fun compare(r1: AttendResult, r2: AttendResult): Int {
                return r1.sum.compareTo(r2.sum)
            }
        }

    }

}