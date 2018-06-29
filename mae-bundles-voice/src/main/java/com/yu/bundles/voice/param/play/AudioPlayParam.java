package com.yu.bundles.voice.param.play;

import android.media.AudioFormat;

import com.yu.bundles.voice.param.VoiceType;

/**
 * Created by liyu on 2017/11/2.
 */

public class AudioPlayParam extends VoicePlayParam {
    public int sampleRateInHz;
    private AudioOutChannel audioOutChannel;

    public AudioPlayParam(int sampleRateInHz, AudioOutChannel audioOutChannel) {
        this.sampleRateInHz = sampleRateInHz;
        this.audioOutChannel = audioOutChannel;
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

    public int getAudioOutChannel(){
        switch (audioOutChannel){
            case CHANNEL_OUT_MONO:
                return AudioFormat.CHANNEL_OUT_MONO;
            case CHANNEL_OUT_STEREO:
                return AudioFormat.CHANNEL_OUT_STEREO;
            default:
                return AudioFormat.CHANNEL_OUT_DEFAULT;
        }
    }

    public enum AudioOutChannel {
        CHANNEL_OUT_MONO, CHANNEL_OUT_STEREO, CHANNEL_OUT_DEFAULT
    }

    @Override
    public String toString() {
        return "AudioOutParam{" +
                "sampleRateInHz=" + sampleRateInHz +
                ", audioOutChannel=" + audioOutChannel +
                '}';
    }
}
