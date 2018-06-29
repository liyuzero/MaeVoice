package com.yu.bundles.voice.param.record;

import com.yu.bundles.voice.param.VoiceParam;

/**
 * Created by liyu on 2017/11/2.
 */

public abstract class VoiceRecordParam extends VoiceParam{
    private int maxRecordTime = 59 * 1000;
    private long volumeInterval = 50; // 间隔取样时间

    protected VoiceRecordParam() {}

    public VoiceRecordParam setMaxRecordTime(int maxRecordTime) {
        this.maxRecordTime = maxRecordTime;
        return this;
    }

    public int getMaxRecordTime() {
        return maxRecordTime;
    }

    public void setVolumeInterval(long volumeInterval) {
        this.volumeInterval = volumeInterval;
    }

    public long getVolumeInterval() {
        return volumeInterval;
    }
}
