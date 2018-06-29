package com.yu.bundles.voice.manager;

import android.support.annotation.Keep;

import com.yu.bundles.voice.param.VoiceParam;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.play.AudioPlayParam;
import com.yu.bundles.voice.player.AudioPlayerUtils;
import com.yu.bundles.voice.player.MediaPlayerUtils;
import com.yu.bundles.voice.player.PlayListener;
import com.yu.bundles.voice.player.PlayerAPI;

/**
 * 修改权限，不对外暴露
 * Created by liyu on 2017/10/31.
 */

@Keep
class PlayerManager implements PlayerAPI{
    private PlayerAPI playerAPI;

    public PlayerManager(VoiceType voiceType, VoiceParam voiceParam) {
        switch (voiceType){
            case PCM_8BIT:
            case PCM_16BIT:
                if(!(voiceParam instanceof AudioPlayParam)){
                    throw new RuntimeException("The voiceParam must be AudioPlayParam");
                }
                playerAPI = new AudioPlayerUtils(voiceType, (AudioPlayParam) voiceParam);
                break;
            case AMR:
                playerAPI = new MediaPlayerUtils();
                break;
            case WAV:
                playerAPI = new MediaPlayerUtils();
                break;
        }
    }

    @Override
    public void startPlay(final String filePath, final PlayListener listener) {
        if(playerAPI!=null){
            playerAPI.startPlay(filePath, listener);
        }
    }

    @Override
    public void stopPlay() {
        if(playerAPI != null){
            playerAPI.stopPlay();
        }
    }

    @Override
    public boolean isPlaying() {
        return playerAPI.isPlaying();
    }

    @Override
    public void release() {
        if(playerAPI != null){
            playerAPI.stopPlay();
            playerAPI.release();
            playerAPI = null;
        }
    }
}
