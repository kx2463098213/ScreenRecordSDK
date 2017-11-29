package com.orz.recorder.util;

import android.text.TextUtils;


import com.orz.recorder.ATest;

import java.io.File;

/**
 * Created by Administrator on 2017/11/16.
 * 文件工具
 */

public class FileUtil {

    private static String gVideoPath = null;
    private static String gLogFilePath = null;

    /**
     * 根据路径判断文件是否存在
     * @param path
     * @return
     */
    public static boolean fileExists(String path){
        if (TextUtils.isEmpty(path)){
            return false;
        }
        File file = new File(path);
        return fileExists(file);
    }

    /**
     * 判断文件是否存在
     * @param file
     * @return
     */
    public static boolean fileExists(File file){
        return file != null && file.exists();
    }

    /**
     * 获取视频文件的存放路径
     * @return
     */
    public static String getVideoRootPath(){
        if (!TextUtils.isEmpty(gVideoPath)){
            return gVideoPath;
        }
        try {
            File file = ATest.gContext.getExternalFilesDir(ATest.STORAGE_VIDEO_ROOT_PATH);
            if (!fileExists(file)){
                file.mkdirs();
            }
            gVideoPath = file.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("getVideoRootPath error:" + e.getLocalizedMessage());
        }
        return gVideoPath;
    }

    /**
     * 获取日志文件的存放路径
     * @return
     */
    public static String getLogRootPath(){
        if (!TextUtils.isEmpty(gLogFilePath)){
            return gLogFilePath;
        }
        try {
            File file = ATest.gContext.getExternalFilesDir(ATest.STOTAGE_LOG_ROOT_PATH);
            if (!fileExists(file)){
                file.mkdirs();
            }
            gLogFilePath = file.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("getLogRootPath error:" + e.getLocalizedMessage());
        }
        return gLogFilePath;
    }

}
