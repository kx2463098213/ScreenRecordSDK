package com.orz.recorder.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.orz.recorder.ATest;
import com.orz.recorder.R;


/**
 * Created by Administrator on 2017/11/20.
 * 用于显示通知或者取消通知
 */

public class NotificationUtil {

    private static NotificationManager nm;

    private NotificationUtil(){}

    private static class Holder {
        private static NotificationUtil INSTANCE = new NotificationUtil();
    }

    public static NotificationUtil getInstance(){
        if (nm == null){
            nm = (NotificationManager) ATest.gContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return Holder.INSTANCE;
    }

    public Notification showNotification(int id){
        Notification.Builder builder = new Notification.Builder(ATest.gContext);
        builder.setSmallIcon(R.drawable.icon_notification_record)
                .setContentTitle("눈_눈")
                .setContentText("ATest正在录制当前屏幕...(　･ิω･ิ)ノิ ")
                .setAutoCancel(false);
        Notification notification = builder.build();
        if (nm != null){
            nm.notify(id, notification);
        }
        return notification;
    }


    public void cancelNotification(int id){
        if (nm != null){
            nm.cancel(id);
        }
    }

    public void cancelAll(){
        if (nm != null){
            nm.cancelAll();
        }
    }

}
