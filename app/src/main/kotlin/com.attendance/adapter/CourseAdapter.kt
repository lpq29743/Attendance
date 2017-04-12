package com.attendance.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.attendance.R
import com.attendance.dao.CourseDao
import com.attendance.entities.CourseBean

import java.util.ArrayList

/**
 * Created by Administrator on 2016/8/30.
 */
class CourseAdapter(private val context: Context) : BaseAdapter() {
    private val courseList: MutableList<CourseBean>
    private val courseDao: CourseDao

    init {
        courseList = ArrayList<CourseBean>()
        courseDao = CourseDao(context)
    }

    override fun getCount(): Int {
        return courseList.size
    }

    override fun getItem(position: Int): Any {
        return courseList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val vh: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course, null)
            vh = ViewHolder()
            vh.name = convertView!!.findViewById(R.id.course_name_tv) as TextView
            convertView.tag = vh
        } else {
            vh = convertView.tag as ViewHolder
        }
        val item = courseList[position]
        val name = item.name
        vh.name!!.text = name
        return convertView
    }

    // 读取本地数据库设置数据
    fun setCourseList() {
        courseList.clear()
        // 查询所有数据
        val cursor = courseDao.findAll()
        while (cursor.moveToNext()) {
            val course = CourseBean()
            course.id = cursor.getInt(cursor.getColumnIndex("id"))
            course.name = cursor.getString(cursor.getColumnIndex("name"))
            courseList.add(course)
        }
    }

    private inner class ViewHolder {
        var name: TextView? = null
    }
}
