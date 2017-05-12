package com.attendance.attendresults;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.attendance.data.AttendResult;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by peiqin on 2/26/2017.
 */
public class AttendResultsAdapter extends TableDataAdapter<AttendResult> {

    private static final int TEXT_SIZE = 14;

    public AttendResultsAdapter(Context context, List<AttendResult> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        AttendResult resultBean = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderString(resultBean.getName());
                break;
            case 1:
                renderedView = renderString(resultBean.getAttend());
                break;
            case 2:
                renderedView = renderString(resultBean.getEarly());
                break;
            case 3:
                renderedView = renderString(resultBean.getLate());
                break;
            case 4:
                renderedView = renderString(resultBean.getSum());
                break;
        }

        return renderedView;
    }

    private View renderString(String value) {
        TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }

}
