package com.yu.bundles.voice.param;

import com.yu.bundles.voice.param.play.AudioPlayParam;
import com.yu.bundles.voice.param.play.VoicePlayParam;
import com.yu.bundles.voice.param.record.AudioRecordParam;
import com.yu.bundles.voice.param.record.MediaRecordParam;
import com.yu.bundles.voice.param.record.VoiceRecordParam;

/**
 * Created by liyu on 2017/11/1.
 */

@SuppressWarnings("unchecked")
public class DefaultParam {

    private static int DEFAULT_SAMPLE_RATE = 8000;

    public static AudioRecordParam getDefaultAudioRecordParam(){
        return new AudioRecordParam(DEFAULT_SAMPLE_RATE, AudioRecordParam.AudioInChannel.CHANNEL_IN_MONO);
    }

    private static AudioPlayParam getDefaultAudioPlayParam(){
        return new AudioPlayParam(DEFAULT_SAMPLE_RATE, AudioPlayParam.AudioOutChannel.CHANNEL_OUT_MONO);
    }

    private static MediaRecordParam getDefaultMediaRecordParam(){
        return new MediaRecordParam(MediaRecordParam.MediaEncoder.DEFAULT);
    }

    public static VoicePlayParam getDefaultPlayParam(VoiceType voiceType){
        return getDefaultVoiceParam(false, voiceType);
    }

    public static VoiceRecordParam getDefaultRecordParam(VoiceType voiceType){
        return getDefaultVoiceParam(true, voiceType);
    }

    private static <T>T getDefaultVoiceParam(boolean isRecord, VoiceType voiceType){
        if(isRecord){
            switch (voiceType){
                case AMR:
                    return (T) DefaultParam.getDefaultMediaRecordParam();
                default:
                    return (T) DefaultParam.getDefaultAudioRecordParam();
            }
        } else {
            switch (voiceType){
                case AMR:
                    return null;
                default:
                    return (T) DefaultParam.getDefaultAudioPlayParam();
            }
        }
    }
}
