package com.orz.record.core;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.view.Surface;

import com.orz.record.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/11/16.
 */

public class ScreenRecorder extends Thread {

    private int mWidth;
    private int mHeight;
    private int mBitrate;
    private int mDpi;
    private String mFilePath;
    private MediaProjection mMediaProjection;

    private static final String MINE_TYPE = "video/avc";
    private static final int FRAME_RATE = 30; //帧率
    private static final int I_FRAME_INTERVAL = 1;//设置关键帧的间隔
    private static final int TIMEOUT_USEC = 10000;

    private MediaCodec mEncoder;
    private Surface mSurface;
    private MediaMuxer mMuxer;
    private VirtualDisplay mVirtualDisplay;
    private AtomicBoolean mEndStream = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private boolean mMuxerStarted = false;
    private int mVideoTrackIndex = -1;

    public ScreenRecorder(int width, int height, int bitrate, int dpi, MediaProjection mp, String filePath){
        super("ScreenRecorder Thread");
        mWidth = width;
        mHeight = height;
        mBitrate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mFilePath = filePath;
    }

    public void quit(){
        mEndStream.set(true);
    }

    @Override
    public void run() {
        try {
            prepareEncoder();
            mMuxer = new MediaMuxer(mFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("RECORD", mWidth, mHeight, mDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mSurface, null, null);
            LogUtil.d("created VirtualDisplay:" + mVirtualDisplay);
            recordVirtualDisplay();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("error:" + e.getLocalizedMessage());
        }finally {
            release();
        }
        
    }

    private void recordVirtualDisplay(){
        if (mEndStream.get()){
            mEncoder.signalEndOfInputStream();
        }
        while (true){
            int index = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            LogUtil.d("dequeue output buffer index = " + index );
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                resetOutputFormat();
            }else if (index == MediaCodec.INFO_TRY_AGAIN_LATER){
                if (mEndStream.get()){
                    break;
                }else {
                    LogUtil.d("retrieving buffers time out !");
                }
            }else  if (index >= 0){
                if (!mMuxerStarted){
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format)");
                }
                encodeToVideoTrack(index);
                mEncoder.releaseOutputBuffer(index, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                    if (mEndStream.get()){
                        LogUtil.w("reached end of stream unexpectedly");
                    }else {
                        LogUtil.i("end of stream reached");
                    }
                    break;
                }
            }
        }
    }

    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedDate = mEncoder.getOutputBuffer(index);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0){
            LogUtil.d("ignore BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0){
            LogUtil.d("info.size == 0, drop it");
            encodedDate = null;
        }else {
            LogUtil.d("got buffer, info:size=" + mBufferInfo.size +
                    ", presentationTimeUS = "+ mBufferInfo.presentationTimeUs +
                    ", offset = " + mBufferInfo.offset);
        }
        if (encodedDate != null){
            encodedDate.position(mBufferInfo.offset);
            encodedDate.limit(mBufferInfo.offset + mBufferInfo.size);
            mMuxer.writeSampleData(mVideoTrackIndex, encodedDate, mBufferInfo);
            LogUtil.d("sent " + mBufferInfo.size + " bytes to muxer.");
        }
    }

    private void resetOutputFormat() {
        if (mMuxerStarted){
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();
        LogUtil.d("output format changed.\n new format:" + newFormat.toString());
        mVideoTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
        LogUtil.d("start media muxer, videoIndex=" + mVideoTrackIndex);
    }

    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MINE_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);

        LogUtil.d("video format:" + format);
        mEncoder = MediaCodec.createEncoderByType(MINE_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
        LogUtil.d("created input surface:" + mSurface);
        mEncoder.start();
    }

    private void release() {
        if (mEncoder != null){
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mVirtualDisplay != null){
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null){
            mMediaProjection.stop();
        }
        if (mMuxer != null){
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }
}
