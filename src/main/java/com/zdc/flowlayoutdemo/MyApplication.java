package com.zdc.flowlayoutdemo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;


/**
 * description:自定义Application,进行全局初始化
 *
 * @author zhaodecang
 * @date 2016-10-4下午12:32:13
 */
public class MyApplication extends Application {
    private static Handler mHandler;
    private static Context mContext;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        mHandler = new Handler();
        mainThreadId = Process.myTid();
        mContext = getApplicationContext();
    }

    public static Handler getmHandler() {
        return mHandler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static Context getmContext() {
        return mContext;
    }
}
