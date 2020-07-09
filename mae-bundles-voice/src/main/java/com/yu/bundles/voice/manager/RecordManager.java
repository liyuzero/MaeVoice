package com.yu.bundles.voice.manager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;

import com.yu.bundles.voice.param.VoiceParam;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.AudioRecordParam;
import com.yu.bundles.voice.param.record.MediaRecordParam;
import com.yu.bundles.voice.param.record.VoiceRecordParam;
import com.yu.bundles.voice.record.AudioRecordUtils;
import com.yu.bundles.voice.record.MediaRecordUtils;
import com.yu.bundles.voice.record.RecordAPI;
import com.yu.bundles.voice.record.RecordListener;
import com.yu.bundles.voice.record.audio_extend.WavRecordUtils;

/**
 * 修改权限，不对外暴露
 * Created by liyu on 2017/10/27.
 */

@Keep
public class RecordManager implements RecordAPI {
    private RecordAPI recordAPI;
    private RecordingDurationRunnable mRunnable = new RecordingDurationRunnable();
    private long MAX_RECORD_TIME;
    private Handler handler = new Handler(Looper.getMainLooper());

    public RecordManager(VoiceType voiceType, VoiceParam voiceParam) {
        cancelRecord();
        release();
        MAX_RECORD_TIME = ((VoiceRecordParam)voiceParam).getMaxRecordTime();
        switch (voiceType) {
            case PCM_8BIT:
            case PCM_16BIT:
                if (!(voiceParam instanceof AudioRecordParam)) {
                    throw new RuntimeException("The voiceParam must be AudioRecordParam");
                }
                recordAPI = new AudioRecordUtils(voiceType, (AudioRecordParam) voiceParam);
                break;
            case AMR:
                if (!(voiceParam instanceof MediaRecordParam)) {
                    throw new RuntimeException("The voiceParam must be MediaRecordParam");
                }
                recordAPI = new MediaRecordUtils(voiceType, (MediaRecordParam) voiceParam);
                break;
            case WAV:
                if (!(voiceParam instanceof AudioRecordParam)) {
                    throw new RuntimeException("The voiceParam must be AudioRecordParam");
                }
                recordAPI = new WavRecordUtils(voiceType, (AudioRecordParam) voiceParam);
                break;
            case OTHER_EXTEND:
                if (!(voiceParam instanceof AudioRecordParam)) {
                    throw new RuntimeException("The voiceParam must be AudioRecordParam");
                }
                if(((AudioRecordParam) voiceParam).pcmFileConverter == null){
                    throw new RuntimeException("You must set pcm file converter!!");
                }
                recordAPI = ((AudioRecordParam)voiceParam).pcmFileConverter;
                break;
        }
    }

    @Override
    public void startRecord(final String outputFilePath, final RecordListener recordListener) {
        recordAPI.startRecord(outputFilePath, recordListener);
        handler.postDelayed(mRunnable, MAX_RECORD_TIME);     // 开始计时
    }

    @Override
    public void stopRecord() {
        recordAPI.stopRecord();
        handler.removeCallbacks(mRunnable);
    }

    @Override
    public boolean isRecording() {
        return recordAPI.isRecording();
    }

    @Override
    public void cancelRecord() {
        if (recordAPI != null) {
            recordAPI.cancelRecord();
        }
        handler.removeCallbacks(mRunnable);
    }

    @Override
    public void release() {
        if (recordAPI != null) {
            recordAPI.release();
        }
    }

    private class RecordingDurationRunnable implements Runnable {
        @Override
        public void run() {
            stopRecord();
        }
    }
}
