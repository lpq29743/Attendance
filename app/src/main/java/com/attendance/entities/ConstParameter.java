package com.attendance.entities;

public class ConstParameter {

    //服务器
    //public static final String SERVER_ADDRESS = "http://192.168.191.1:8080/Attendance";
    //public static final String SERVER_ADDRESS = "http://192.168.1.104:80/Attendance";
    //public static final String SERVER_ADDRESS = "http://192.168.155.1:8080/Attendance";
    //腾讯云服务器
    public static final String SERVER_ADDRESS = "http://139.199.202.214/Attendance/";
    public static final String SERVER_ERROR = "服务器错误";

    //登录信息
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String LOGIN_FAILED = "登录失败！请检查用户名、密码以及身份信息是否填写错误";

    //注册信息
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String REGISTER_FAILED = "用户名已存在";

    //添加提示
    public static final String ADD_SUCCESS = "添加成功";
    public static final String ADD_FAILED = "添加失败";

    //删除提示
    public static final String DEL_SUCCESS = "删除成功";
    public static final String DEL_FAILED = "删除失败";

    //修改提示
    public static final String MOD_SUCCESS = "修改成功";
    public static final String MOD_FAILED = "修改失败";

    //获取信息提示
    public static final String GET_SUCCESS = "获取信息成功";
    public static final String GET_FAILED = "获取信息失败";

    //签到提示
    public static final String SIGN_SUCCESS = "签到成功";
    public static final String SIGN_FAILED = "签到失败";

}
