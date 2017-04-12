package com.attendance.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.attendance.entities.ResultBean

import de.codecrafters.tableview.TableDataAdapter

/**
 * Created by peiqin on 2/26/2017.
 */
class ResultDataAdapter(context: Context, data: List<ResultBean>) : TableDataAdapter<ResultBean>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View? {
        val resultBean = getRowData(rowIndex)
        var renderedView: View? = null

        when (columnIndex) {
            0 -> renderedView = renderString(resultBean.name)
            1 -> renderedView = renderString(resultBean.attend)
            2 -> renderedView = renderString(resultBean.early)
            3 -> renderedView = renderString(resultBean.late)
            4 -> renderedView = renderString(resultBean.sum)
        }

        return renderedView
    }

    private fun renderString(value: String): View {
        val textView = TextView(context)
        textView.text = value
        textView.setPadding(20, 10, 20, 10)
        textView.textSize = TEXT_SIZE.toFloat()
        return textView
    }

    companion object {

        private val TEXT_SIZE = 14
    }

}
