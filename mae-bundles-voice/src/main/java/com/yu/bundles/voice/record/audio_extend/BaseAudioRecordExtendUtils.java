package com.yu.bundles.voice.record.audio_extend;

import android.os.Environment;
import android.text.TextUtils;

import com.yu.bundles.voice.manager.VoiceManager;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.AudioRecordParam;
import com.yu.bundles.voice.record.AudioRecordUtils;
import com.yu.bundles.voice.record.RecordAPI;
import com.yu.bundles.voice.record.RecordListener;
import com.yu.bundles.voice.record.TransformFormatOperator;

import java.io.File;

/**
 * Created by liyu on 2017/11/1.
 */

/*
* 该类功能为：对原始数据格式进行格式转换，目前支持wav和spx转换
* */
public abstract class BaseAudioRecordExtendUtils implements RecordAPI, TransformFormatOperator {
    AudioRecordUtils audioRecordUtils;
    AudioRecordParam audioParam;
    private File tempFile;
    private String outputFilePath;

    public BaseAudioRecordExtendUtils(VoiceType voiceType, AudioRecordParam audioParam) {
        this.audioParam = audioParam;
        audioRecordUtils = new AudioRecordUtils(voiceType, audioParam, this);
    }

    protected abstract String getTempFileName();

    @Override
    public void startRecord(String outputFilePath, final RecordListener recordListener) {
        this.outputFilePath = outputFilePath;
        this.tempFile = new File(TextUtils.isEmpty(VoiceManager.cacheDir)? Environment.getExternalStorageDirectory()
                .getAbsolutePath(): VoiceManager.cacheDir, getTempFileName());
        if(tempFile.exists()){
            tempFile.delete();
        }
        audioRecordUtils.startRecord(tempFile.getAbsolutePath(), recordListener);
    }

    @Override
    public void stopRecord() {
        audioRecordUtils.stopRecord();
    }

    @Override
    public final boolean isRecording() {
        return audioRecordUtils.isRecording();
    }

    @Override
    public final void cancelRecord() {
        audioRecordUtils.cancelRecord();
    }

    @Override
    public final String transform(String originFilePath) {
        File outputFile = new File(outputFilePath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        String resultPath = transform(originFilePath, outputFilePath);
        if(tempFile.exists()){
            tempFile.delete();
        }
        return resultPath;
    }

    @Override
    public void release() {
        audioRecordUtils.release();
    }

    protected abstract String transform(String originFilePath, String outputFilePath);
}
