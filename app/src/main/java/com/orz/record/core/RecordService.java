package com.orz.record.core;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.orz.record.util.FileUtil;
import com.orz.record.util.LogUtil;
import com.orz.record.util.NotificationUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/17.
 */

public class RecordService extends Service {

    private static final int NOTIFICATION_ID = 0x502;

    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private MediaProjection mediaProjection;
    private final int bitrate = 8 * 1024 * 1024;
    private SimpleDateFormat sdf;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initVar(intent);
        initNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initVar(Intent intent){
        int resultCode = intent.getIntExtra("resultCode", -1);
        Intent data = intent.getParcelableExtra("data");
        if (resultCode != -1 || data == null){
            stopSelf();
        }

        sdf = new SimpleDateFormat("dd日HH:mm:ss");
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null){
            LogUtil.e("media projection is null");
            return;
        }
        if (mediaProjection == null){
            LogUtil.e("media projection is null");
            return;
        }
        startRecordThread();
    }

    private void initNotification(){
        Notification notification = NotificationUtil.getInstance().showNotification(NOTIFICATION_ID);
        startForeground(NOTIFICATION_ID, notification);
    }

    private void startRecordThread(){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        float density = dm.density;
        final int width = dm.widthPixels;
        final int height = dm.heightPixels;
        LogUtil.i("width:" + width +", height:" + height + ", density:" + density);

        String fileName = FileUtil.getStoreRootPath() + File.separator + "录制时间-" + sdf.format(new Date()) + ".mp4";
        LogUtil.i("fileName：" + fileName);
        File file = new File(fileName);
        mRecorder = new ScreenRecorder(width, height,  bitrate, (int) density, mediaProjection, file.getAbsolutePath());
        mRecorder.start();
        LogUtil.i("record start...");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null){
            mRecorder.quit();
        }
        cleanNotification();
        super.onDestroy();
    }

    private void cleanNotification(){
        NotificationUtil.getInstance().cancelNotification(NOTIFICATION_ID);
    }

}
