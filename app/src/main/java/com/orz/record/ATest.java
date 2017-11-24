package com.orz.record;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.orz.record.core.RecordService;
import com.orz.record.core.TransparentActivity;
import com.orz.record.util.FloatWindowManager;
import com.orz.record.util.LogUtil;
import com.orz.record.util.PermissionsUtil;


/**
 * Created by Administrator on 2017/11/16.
 */

public class ATest {

    public static boolean isDebugModel = true;
    public static Context gContext = null;

    public static final String STORAGE_VIDEO_ROOT_PATH = "record/video";
    public static final String STOTAGE_LOG_ROOT_PATH = "record/log";
    public static final int REQUEST_RECORD_CODE = 1001;
    public static final int REQUEST_PERMISSION_CODE = 10001;

    //是否正在录制之中
    public static boolean isRecording = false;

    private ATest(){}

    //是否需要单例模式？
    /*private static class ConstantHolder{
        private static final ATest INSTANCE = new ATest();
    }

    public static ATest getInstance(){
        return ConstantHolder.INSTANCE;
    }*/

    /**
     * 初始化SDK
     * @param context
     */
    public static void init(Context context){
        if (context == null){
            LogUtil.e("Context in null, ATest SDK 初始化失败.");
            return;
        }
        gContext = context;
        // 6.0 以上进行动态请求悬浮窗权限.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(gContext)) {
            TransparentActivity.startPermissionActivity();
        }else {
            initFloatButton();
        }
    }

    public void destroy(){
        FloatWindowManager.removeFloatWindow();
    }

    /**
     * 请求录制
     * @param listener
     */
    public static void startRecord(TransparentActivity.RecordListener listener){
        TransparentActivity.startRecord(listener);
    }

    /**
     * 停止录制
     */
    public static void stopRecord(){
        Intent intent = new Intent(ATest.gContext, RecordService.class);
        ATest.gContext.stopService(intent);
        isRecording = false;
    }

    /**
     * 展示悬浮窗
     */
    public static void initFloatButton(){
        FloatWindowManager.createFloatWindow();
    }

}
