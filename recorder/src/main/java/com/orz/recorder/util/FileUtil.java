package com.orz.recorder.util;

import android.text.TextUtils;


import com.orz.recorder.ATest;

import java.io.File;

/**
 * Created by Administrator on 2017/11/16.
 * 文件工具
 */

public class FileUtil {

    private static String gStoragePath = null;


    public static boolean fileExists(String path){
        if (TextUtils.isEmpty(path)){
            return false;
        }
        File file = new File(path);
        return fileExists(file);
    }

    public static boolean fileExists(File file){
        return file != null && file.exists();
    }

    public static String getStoreRootPath(){
        if (!TextUtils.isEmpty(gStoragePath)){
            return gStoragePath;
        }
        String path;
        try {
            File file = ATest.gContext.getExternalFilesDir(ATest.STORAGE_VIDEO_ROOT_PATH);
            if (!fileExists(file)){
                file.mkdirs();
            }
            path = file.getAbsolutePath();
            gStoragePath = path;
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("getStoreRootPath error:" + e.getLocalizedMessage());
        }
        return gStoragePath;
    }

}
