package com.yu.bundles.voice.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.yu.bundles.voice.BuildConfig;

import java.io.IOException;

/**
 * mediaPlayer 工具类封装
 * <pre>
 * 参考：
 * 1.http://blog.csdn.net/u014365133/article/details/53330776
 * 2.http://www.cnblogs.com/MMLoveMeMM/articles/3444718.html
 * </pre>
 */
public final class MediaPlayerUtils implements PlayerAPI{
	private MediaPlayer mediaPlayer;
	private PlayListener playListener;

	public MediaPlayerUtils() {
		initMediaPlayer();
	}

	private void initMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		// 设置流媒体类型
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	private void setListeners(){
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mediaPlayer.start();
				if(playListener != null){
					playListener.onStart();
				}
			}
		});
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				stop();
				if(playListener != null){
					playListener.onComplete();
				}
			}
		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if(playListener!=null){
					playListener.onError(new RuntimeException("MediaPlayer Error!"));
				}
				return false;
			}
		});
	}

	@Override
	public void startPlay(String filePath, PlayListener listener) {
		this.playListener = listener;
		setListeners();
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepareAsync();        // 异步状态流媒体文件
		} catch (IOException e){
			if (BuildConfig.DEBUG) {
				Log.e(getClass().getName(), ">>> " + "run: ", e);
			}
		}
	}

	@Override
	public void stopPlay() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			if(playListener != null){
				playListener.onStop();
			}
		}
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public void release() {
		stop();
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	// ------------ about player -----------------------------------------------
	public void play(String soundFilePath) throws IOException {
		mediaPlayer.reset();
		mediaPlayer.setDataSource(soundFilePath);
		mediaPlayer.prepareAsync();        // 异步状态流媒体文件
	}

	public void pause() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	private void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

}
