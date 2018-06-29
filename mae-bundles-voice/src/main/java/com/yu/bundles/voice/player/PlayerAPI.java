package com.yu.bundles.voice.player;

/**
 * Created by liyu on 2017/10/31.
 */

public interface PlayerAPI {
    void startPlay(String filePath, PlayListener listener);
    void stopPlay();
    boolean isPlaying();
    void release();
}
