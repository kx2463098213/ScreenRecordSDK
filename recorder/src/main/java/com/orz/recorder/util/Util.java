package com.orz.recorder.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2017/11/20.
 * 常规工具类
 */

public class Util {

    /**
     * 获取应用名称
     * @param context
     * @param pkgName
     * @return
     */
    public static String getAppName(Context context, String pkgName){
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int dip2px(Context context, int value){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(value * scale + 0.5f);
    }

    public static int px2dip(Context context, int value){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(value / scale + 0.5f);
    }

}
