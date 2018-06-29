package com.yu.bundles.voice.param.record;

import android.media.MediaRecorder;

import com.yu.bundles.voice.param.VoiceType;

/**
 * Created by liyu on 2017/10/31.
 */

public class MediaRecordParam extends VoiceRecordParam {
    public MediaEncoder mediaEncoder;
    public MediaFormat mediaFormat;

    public MediaRecordParam(MediaEncoder mediaEncoder) {
        this.mediaEncoder = mediaEncoder;
    }

    public MediaRecordParam(MediaEncoder mediaEncoder, MediaFormat format) {
        this.mediaEncoder = mediaEncoder;
        this.mediaFormat = format;
    }

    public int getMediaFormat(VoiceType voiceType) {
        switch (voiceType) {
            case AMR:
                return MediaRecorder.OutputFormat.AMR_WB;       // 修改成 WB格式
            default:
                return MediaRecorder.OutputFormat.DEFAULT;
        }
    }

    public int getMediaEncoder() {
        switch (mediaEncoder) {
            case AAC:
                return MediaRecorder.AudioEncoder.AAC;
            case AAC_ELD:
                return MediaRecorder.AudioEncoder.AAC_ELD;
            case AMR_NB:
                return MediaRecorder.AudioEncoder.AMR_NB;
            case AMR_WB:
                return MediaRecorder.AudioEncoder.AMR_WB;
            case HE_AAC:
                return MediaRecorder.AudioEncoder.HE_AAC;
            default:
                return MediaRecorder.AudioEncoder.DEFAULT;
        }
    }

    public int getMediaFormat() {
        if (mediaFormat == null) {
            return MediaRecorder.OutputFormat.DEFAULT;
        }
        switch (mediaFormat) {
            case AAC_ADTS:
                return MediaRecorder.OutputFormat.AAC_ADTS;
            case AMR_WB:
                return MediaRecorder.OutputFormat.AMR_WB;
            case AMR_NB:
                return MediaRecorder.OutputFormat.AMR_NB;
            case MPEG_2_TS:
                return MediaRecorder.OutputFormat.MPEG_2_TS;
            case MPEG_4:
                return MediaRecorder.OutputFormat.MPEG_4;
            default:
                return MediaRecorder.OutputFormat.DEFAULT;
        }
    }

    @Override
    public String toString() {
        return "MediaParam{" +
                "mediaEncoder=" + mediaEncoder +
                '}';
    }

    public enum MediaEncoder {
        //AAC低复杂度（aac-lc）音频编解码器
        AAC,
        //增强低延迟AAC（aac-eld）音频编解码器
        AAC_ELD,
        //AMR（窄带）音频编解码器
        AMR_NB,
        //AMR（宽带）语音编解码
        AMR_WB,
        //高效率AAC音频编码（AAC）
        HE_AAC,
        //默认编解码器
        DEFAULT
    }

    /**
     * 格式
     */
    public enum MediaFormat {
        AAC_ADTS,
        AMR_WB,
        AMR_NB,
        MPEG_2_TS,
        MPEG_4,
        DEFAULT
    }
}
