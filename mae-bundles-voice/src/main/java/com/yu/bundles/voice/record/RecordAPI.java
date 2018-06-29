package com.yu.bundles.voice.record;

import android.support.annotation.Keep;

/**
 * Created by liyu on 2017/10/27.
 */

@Keep
public interface RecordAPI {
    void startRecord(String outputFilePath, RecordListener recordListener);
    void stopRecord();
    boolean isRecording();
    void cancelRecord();
    void release();
}
