package com.orz.recorder.core;

import android.content.Context;
import android.os.Process;

import com.orz.recorder.util.FileUtil;
import com.orz.recorder.util.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/28.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    //用于处理已有默认崩溃处理的情况
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private SimpleDateFormat format = new SimpleDateFormat("dd日HH:mm:ss");

    private CrashHandler(){}

    private static class CrashHandlerHolder{
        private static CrashHandler INSTANCE = new CrashHandler();
    }

    public static CrashHandler getInstance(){
        return CrashHandlerHolder.INSTANCE;
    }

    public void init(Context context){
        this.mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handleException(t, e);// 如果先调用 mDefaultHandler.uncaughtException()，将不会有日志文件
        if (mDefaultHandler != null){
            mDefaultHandler.uncaughtException(t, e);
        }else {
            Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        LogUtil.e("The fucking Error:" + e.getLocalizedMessage());
    }

    /**
     * 处理异常
     * @param t 异常线程
     * @param e 异常
     * @return
     */
    private boolean handleException(Thread t, Throwable e){
        if (e == null){
            return false;
        }
        LogUtil.e("Crash Thread Name:" + t.getName());
        saveCrashInfo2File(e);
        return true;
    }

    /**
     * 写本地文件
     * @param e
     */
    private void saveCrashInfo2File(Throwable e){
        /*Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();*/
        try {
            String time = format.format(new Date());
            String fileName = FileUtil.getLogRootPath() + File.separator + "crash-" + time + ".log";
            File logFile = new File(fileName);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
            e.printStackTrace(pw);
            pw.close();
        }catch (Exception ex){
            LogUtil.e("write crash to local error:" + ex.getLocalizedMessage());
        }
    }

}
