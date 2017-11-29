package com.orz.recorder.core;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.orz.recorder.util.FileUtil;
import com.orz.recorder.util.LogUtil;
import com.orz.recorder.util.NotificationUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
        final float density = dm.density;
        final int width = dm.widthPixels;
        final int height = dm.heightPixels;
        LogUtil.i("width:" + width +", height:" + height + ", density:" + density);

        String fileName = FileUtil.getVideoRootPath() + File.separator + "录制时间-" + sdf.format(new Date()) + ".mp4";
        LogUtil.i("fileName：" + fileName);
        File file = new File(fileName);
        mRecorder = new ScreenRecorder(width, height,  bitrate, (int) density, mediaProjection, file.getAbsolutePath());
        mRecorder.start();
        LogUtil.i("record start...");
    }


    private void capture(int width, int height, int dpi){
        ImageReader reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888,1);
        if (reader == null){
            return;
        }
        final String mFilePath = FileUtil.getVideoRootPath() + File.separator + "截图-" + sdf.format(new Date()) + ".png";
        mediaProjection.createVirtualDisplay("Capture", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, reader.getSurface(), null, null);
        reader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();

                int width = image.getWidth();
                int height = image.getHeight();
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer =  planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding/pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                image.close();

                File file = new File(mFilePath);
                if (!file.exists()){
                    try {
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        LogUtil.i("capture finish.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
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
