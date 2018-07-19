package com.yu.demos.voice;

import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.AudioRecordParam;
import com.yu.bundles.voice.record.audio_extend.BaseAudioRecordExtendUtils;

public class TestFileConvater extends BaseAudioRecordExtendUtils {

    public TestFileConvater(VoiceType voiceType, AudioRecordParam audioParam) {
        super(voiceType, audioParam);
    }

    @Override
    //设置临时文件的名字，对应于originFilePath
    protected String getTempFileName() {
        return null;
    }

    //将生成的PCM文件originFilePath：自行转换为outputFilePath所对应的文件，outputFilePath对应于外界指定的文件录音地址
    @Override
    protected String transform(String originFilePath, String outputFilePath) {
        return null;
    }
}
