package com.yu.bundles.voice.player;

/**
 * 播放监听
 */
public interface PlayListener {
	void onStart();
	void onStop();
	void onError(Exception e);
	void onComplete();
}
