package com.orz.recorder.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

import com.orz.recorder.ATest;
import com.orz.recorder.widget.FloatLayout;

/**
 * Created by Administrator on 2017/11/20.
 * 用于显示悬浮按钮
 */

public class FloatWindowManager {

    private static FloatLayout mFloatLayout;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams mParams;
    private static boolean hasShown = false;

    static {
        getWindowManager();
    }

    private static void getWindowManager(){
        if (mWindowManager == null){
            mWindowManager = (WindowManager) ATest.gContext.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    /**
     * 创建悬浮窗
     */
    public static void createFloatWindow(){
        mParams = new WindowManager.LayoutParams();
        mFloatLayout = new FloatLayout(ATest.gContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }else {
            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }

        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;

//        DisplayMetrics dm = new DisplayMetrics();
//        mWindowManager.getDefaultDisplay().getMetrics(dm);
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;

        //悬浮窗默认出现的位置
        mParams.x = 100;
        mParams.y = 100;

        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatLayout.setParams(mParams);
        mWindowManager.addView(mFloatLayout, mParams);
        hasShown = true;
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindow() {
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatLayout.isAttachedToWindow();
        }

        if (hasShown && isAttach && mWindowManager != null){
            mWindowManager.removeView(mFloatLayout);
        }
    }

}
