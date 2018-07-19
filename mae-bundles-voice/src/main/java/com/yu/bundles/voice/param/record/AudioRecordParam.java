package com.yu.bundles.voice.param.record;

import android.media.AudioFormat;

import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.record.audio_extend.BaseAudioRecordExtendUtils;

/**
 * Created by liyu on 2017/11/2.
 */

public class AudioRecordParam extends VoiceRecordParam {
    public int sampleRateInHz;
    public AudioInChannel audioInChannel;
    public boolean isSetOutputFile = true;
    public int bufferLen = 0;
    public BaseAudioRecordExtendUtils pcmFileConverter;

    public AudioRecordParam(int sampleRateInHz, AudioInChannel audioInChannel) {
        this.sampleRateInHz = sampleRateInHz;
        this.audioInChannel = audioInChannel;
    }

    public AudioRecordParam setPcmFileConverter(BaseAudioRecordExtendUtils pcmFileConverter) {
        this.pcmFileConverter = pcmFileConverter;
        return this;
    }

    /**
     * 是否设置输出文件
     * */
    public AudioRecordParam setIsOutputFile(boolean isSetOutputFile){
        this.isSetOutputFile = isSetOutputFile;
        return this;
    }

    public int getAudioFormat(VoiceType voiceType){
        switch (voiceType){
            case PCM_16BIT:
                return AudioFormat.ENCODING_PCM_16BIT;
            case PCM_8BIT:
                return AudioFormat.ENCODING_PCM_8BIT;
            case WAV:
                return AudioFormat.ENCODING_PCM_16BIT;
            default:
                return AudioFormat.ENCODING_PCM_16BIT;
        }
    }

    public int getBit(VoiceType voiceType) {
        switch (voiceType) {
            case PCM_8BIT:
                return 1;
            default:
                return 2;
        }
    }

    public int getAudioInChannel(){
        switch (audioInChannel){
            case CHANNEL_IN_LEFT:
                return AudioFormat.CHANNEL_IN_LEFT;
            case CHANNEL_IN_RIGHT:
                return AudioFormat.CHANNEL_IN_RIGHT;
            case CHANNEL_IN_MONO:
                return AudioFormat.CHANNEL_IN_MONO;
            case CHANNEL_IN_STEREO:
                return AudioFormat.CHANNEL_IN_STEREO;
            default:
                return AudioFormat.CHANNEL_IN_DEFAULT;
        }
    }

    public enum AudioInChannel {
        CHANNEL_IN_MONO, CHANNEL_IN_STEREO, CHANNEL_IN_LEFT, CHANNEL_IN_RIGHT, CHANNEL_IN_DEFAULT
    }

    @Override
    public String toString() {
        return "AudioInParam{" +
                "sampleRateInHz=" + sampleRateInHz +
                ", audioInChannel=" + audioInChannel +
                '}';
    }
}
