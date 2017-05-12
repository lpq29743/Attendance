package com.attendance.attendresults;

import com.attendance.BasePresenter;
import com.attendance.BaseView;
import com.attendance.data.AttendResult;

import java.util.List;

/**
 * Created by peiqin on 3/4/2017.
 */

public interface AttendResultsContract {

    interface View extends BaseView<Presenter> {

        void showTip(String tip);

        void getAttendResultsSuccess(List<AttendResult> list);

    }

    interface Presenter extends BasePresenter {

        void getAttendResultList();

    }

}
