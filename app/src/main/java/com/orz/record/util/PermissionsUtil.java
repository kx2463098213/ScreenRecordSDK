package com.orz.record.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/11/22.
 */

public class PermissionsUtil {


    /**
     * 检查是否有权限
     * @param permission 权限名称
     * @return
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String permission){
        if (TextUtils.isEmpty(permission))
            return false;
        int result = PermissionChecker.checkSelfPermission(context, permission);
        if (result == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    /**
     * 判断是否所有权限都已经授权
     * @param grantResults
     * @return
     */
    public static boolean isAllPermissionGranted(@NonNull int [] grantResults){
        for (int grantResult : grantResults){
            if (grantResult == PermissionChecker.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

}
