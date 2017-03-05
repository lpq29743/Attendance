package com.attendance.http;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

/**
 * Created by peiqin on 3/5/2017.
 */

public class HttpMethods {

    public static final String BASE_URL = "http://139.199.202.214/Attendance/";

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private AttendService attendService;

    // 构造方法私有
    private HttpMethods() {
        // 手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        attendService = retrofit.create(AttendService.class);
    }

    // 在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    // 获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void login(Subscriber<String> subscriber, String username, String password, String isTeacher) {

        Observable observable = attendService.login(username, password, isTeacher)
                .map(new HttpResultFunc<String>());

        toSubscribe(observable, subscriber);
    }

    private <String> void toSubscribe(Observable<String> o, Subscriber<String> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    private class HttpResultFunc<String> implements Func1<String, String> {

        @Override
        public String call(String result) {
            return result;
        }
    }

}
