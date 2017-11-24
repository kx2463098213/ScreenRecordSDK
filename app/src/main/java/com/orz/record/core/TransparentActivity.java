package com.orz.record.core;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.orz.record.ATest;
import com.orz.record.util.LogUtil;

/**
 * Created by Administrator on 2017/11/17.
 */

public class TransparentActivity extends AppCompatActivity {

    private static final int TYPE_REQUEST_FLOAT_PERMISSION = 0;
    private static final int TYPE_REQUEST_RECORD = 1;

    private static final String KEY_TYPE = "TYPE";

    private MediaProjectionManager mMediaProjectionManager;
    private static RecordListener mRecordListener;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null){
            int type = intent.getIntExtra(KEY_TYPE, -1);
            if (type == TYPE_REQUEST_FLOAT_PERMISSION){
                showMissPermissionDialog();
            }else if (type == TYPE_REQUEST_RECORD){
                requestRecord();
            }
        }
    }

    /**
     * 7.0 以上从外部通过该 activity 请求悬浮窗权限
     */
    public static void startPermissionActivity(){
        if (ATest.gContext == null){
            LogUtil.e("gContext is null, can't request permission");
            return;
        }
        Intent i = new Intent(ATest.gContext, TransparentActivity.class);
        i.putExtra(KEY_TYPE, TYPE_REQUEST_FLOAT_PERMISSION);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ATest.gContext.startActivity(i);
    }

    /**
     * 开始录屏
     */
    public static void startRecord(RecordListener listener){
        if (listener == null){
            LogUtil.e("RecordListener is null, startRecord failure.");
            return;
        }
        mRecordListener = listener;
        if (!ATest.isRecording){
            Intent intent = new Intent(ATest.gContext, TransparentActivity.class);
            intent.putExtra(KEY_TYPE, TransparentActivity.TYPE_REQUEST_RECORD);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ATest.gContext.startActivity(intent);
        }else {
            LogUtil.e("Record sdk is working, can't start again.");
        }
    }

    private void requestFloatPermission(){
        /*final AppOpsManager ops = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        ops.startWatchingMode(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, getPackageName(), new AppOpsManager.OnOpChangedListener(){

            @Override
            public void onOpChanged(String op, String packageName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    LogUtil.e("result:" + Settings.canDrawOverlays(TransparentActivity.this));
                }
            }
        });*/
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, ATest.REQUEST_PERMISSION_CODE);
    }

    private void requestRecord(){
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, ATest.REQUEST_RECORD_CODE);
    }

    private void showMissPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示:");
        builder.setMessage("悬浮窗权限缺失，录屏SDK无法正常使用，请开启相关权限.");
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(TransparentActivity.this, "权限被拒绝，录屏SDK初始化失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestFloatPermission();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

   /* private void startAppSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ATest.REQUEST_RECORD_CODE) {
            if (RESULT_OK == resultCode) {
                Intent intent = new Intent(this, RecordService.class);
                intent.putExtra("resultCode", resultCode);
                intent.putExtra("data", data);
                startService(intent);
                setResult(RESULT_OK);
                ATest.isRecording = true;
                if (mRecordListener != null){
                    mRecordListener.startRecord();
                }
            } else {
                Toast.makeText(this, "录屏操作已被拒绝.", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == ATest.REQUEST_PERMISSION_CODE){
            /*if (Settings.canDrawOverlays(ATest.gContext)){//获取授权成功,展示悬浮窗
                ATest.initFloatButton();
            }else {
                try { //暴力方式显示悬浮窗，如果崩溃的话，代表授权失败（8.0 系统，进入到设置页，不修改开关状态，canDrawOverlays进入返回true.）
                    ATest.initFloatButton();
                }catch (Exception e){
                    LogUtil.e(e.getLocalizedMessage());
                    Toast.makeText(this, "未授予悬浮窗需要的权限，录屏SDK无法正常使用。",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "未授予悬浮窗需要的权限，录屏SDK无法正常使用。",Toast.LENGTH_SHORT).show();

            }*/
            if (Settings.canDrawOverlays(ATest.gContext)){
                ATest.initFloatButton();
            }else {
                Toast.makeText(this, "未授予悬浮窗需要的权限，录屏SDK功能无法正常使用。", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 录屏是否开始的监听
     */
    public interface RecordListener {
        void startRecord();
    }

}
