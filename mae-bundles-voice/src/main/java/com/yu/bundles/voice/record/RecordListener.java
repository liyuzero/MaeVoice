package com.yu.bundles.voice.record;

import android.support.annotation.Keep;

@Keep
public interface RecordListener {

    /**
     * 录音开始
     */
    void onStart();

    /**
     * 录音字节数组的实时监听，仅对AudioRecord有效
     *
     * @param audioData 录音的字节数组
     * @param len 字节数组有效长度
     * @param audioSeq 当前字节数组的序列号，从0开始
     * */
    void onRecordBytes(byte[] audioData, int len, int audioSeq);

    /**
     * 录音结束
     *
     * @param duration 录音时长 ms
     */
    void onFinishRecord(long duration, String filePath);

    /**
     * 录音取消
     */
    void onCancel();

    /**
     * 录音出错
     */
    void onError(Exception e);

    /**
     * 音频频率改变
     * @param volume
     */
    void onAmplitudeChanged(int volume);
}