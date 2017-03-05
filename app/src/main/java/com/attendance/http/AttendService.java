package com.attendance.http;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by peiqin on 3/5/2017.
 */

public interface AttendService {

    @POST("login.php")
    Call<String> login(@Query("username") String username, @Query("password") String password, @Query("isTeacher") String isTeacher);

}
