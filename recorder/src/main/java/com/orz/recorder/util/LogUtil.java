package com.orz.recorder.util;

import android.util.Log;

import com.orz.recorder.ATest;

/**
 * Created by Administrator on 2017/11/16.
 * 日志工具
 */

public class LogUtil {

    private static final String TAG = "SCREEN_RECORD";

    public static void i(String msg){
        if (ATest.isDebugModel){
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg){
        if (ATest.isDebugModel){
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg){
        Log.e(TAG, msg);
    }

    public static void v(String msg){
        if (ATest.isDebugModel){
            Log.v(TAG, msg);
        }
    }

    public static void w(String msg){
        Log.w(TAG, msg)
        ;
    }

}
