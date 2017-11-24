package com.orz.record.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.orz.record.ATest;
import com.orz.record.R;
import com.orz.record.core.TransparentActivity;
import com.orz.record.util.LogUtil;
import com.orz.record.util.Util;

/**
 * Created by Administrator on 2017/11/20.
 */

public class FloatLayout extends FrameLayout implements TransparentActivity.RecordListener {

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private ImageView mFloatView;

    private long startTime, endTime;
    private float mLastX, mLastY;

    public FloatLayout(@NonNull Context context) {
        this(context, null);
    }

    public FloatLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init(){
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mFloatView = new ImageView(mContext);
        mFloatView.setImageResource(R.drawable.icon_record);
        int size = Util.dip2px(mContext, 50);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        addView(mFloatView, params);
        setBackgroundResource(R.drawable.selector_float_button);
        setClickable(true);
    }

    public void setParams(WindowManager.LayoutParams params){
        this.mParams = params;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //相对屏幕的坐标
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) (x - mLastX);
                int offsetY = (int) (y - mLastY);
                if (Math.abs(offsetX) > 5 && Math.abs(offsetY) > 5){
                    mParams.x = (mParams.x - offsetX);
                    mParams.y = (mParams.y - offsetY);
                    //这里必须要重置 mLastX 和 mLastY的值，否则会出现漂移和不受控制的情况
                    mLastX = x;
                    mLastY = y;
                    mWindowManager.updateViewLayout(this, mParams);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) <= 0.1 * 1000L){
                    startOrStopRecord();
                }
                break;
        }
        return true;
    }

    private void startOrStopRecord(){
        if (ATest.isRecording){
            ATest.stopRecord();
            mFloatView.setImageResource(R.drawable.icon_record);
            Toast.makeText(mContext,"录制已经停止", Toast.LENGTH_SHORT).show();
        }else {
            ATest.startRecord(this);
        }
    }


    @Override
    public void startRecord() {
        LogUtil.i("startRecord callback");
        mFloatView.setImageResource(R.drawable.icon_stop);
    }

}
