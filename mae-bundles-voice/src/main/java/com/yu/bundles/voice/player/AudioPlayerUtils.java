package com.yu.bundles.voice.player;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.play.AudioPlayParam;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * pcm 无损格式播放
 */
public final class AudioPlayerUtils implements PlayerAPI{
	private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;
	private boolean isPlaying = false;
	private int mMinBufferSize = 0;
	private AudioTrack mAudioTrack;
	private DataInputStream dis;
	private PlayListener playListener;
	private VoiceType voiceType;
	private AudioPlayParam param;
	private AudioPlayerExtraHandle audioPlayerExtraHandle;

	public AudioPlayerUtils(VoiceType voiceType, AudioPlayParam param) {
		this.voiceType = voiceType;
		this.param = param;
	}

	public void setParam(AudioPlayParam param) {
		this.param = param;
	}

	/**
	 * 设置 AudioTrack 参数
	 */
	private void initAudioTrack(int streamType, AudioPlayParam audioPlayParam) {
		if (mAudioTrack == null) {
			mMinBufferSize = AudioTrack.getMinBufferSize(audioPlayParam.sampleRateInHz, audioPlayParam.getAudioOutChannel(), audioPlayParam.getAudioFormat(voiceType));
			if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE) {
				if (playListener != null) {
					playListener.onError(new Exception("AudioTrack MinBufferSize is Bad Value"));
				}
				return;
			}
			mAudioTrack = new AudioTrack(streamType, audioPlayParam.sampleRateInHz, audioPlayParam.getAudioOutChannel(), audioPlayParam.getAudioFormat(voiceType), mMinBufferSize, DEFAULT_PLAY_MODE);
			if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
				if (playListener != null) {
					playListener.onError(new Exception("AudioTrack State is STATE_UNINITIALIZED"));
				}
			}
		}
	}

	@Override
	public void startPlay(String filePath, PlayListener listener) {
		if (isPlaying) {
			return;
		}
		this.playListener = listener;
		File audioFile = new File(filePath);
		if (audioFile.exists()) {
			this.playListener = listener;
			try {
				dis = new DataInputStream(new FileInputStream(audioFile));
				isPlaying = true;
				new Thread(playTask).start();
				playListener.onStart();
				if(playListener != null){
					playListener.onStart();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if(playListener != null){
					playListener.onError(e);
				}
				if(listener!=null){
					listener.onError(new Exception("File not found"));
				}
			}
		} else {
			if(listener!=null){
				listener.onError(new Exception("File not found"));
			}
		}
	}

	/**
	 * 停止播放
	 */
	@Override
	public void stopPlay() {
		stop();
		if(playListener != null){
			playListener.onStop();
		}
	}

	private void stop(){
		isPlaying = false;
		if (mAudioTrack != null) {
			if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
				mAudioTrack.stop();
			}
		}
		if (dis != null) {
			try {
				dis.close();
			} catch (IOException e) {
				if(playListener != null){
					playListener.onError(e);
				}
			}
			dis = null;
		}
	}

	@Override
	public boolean isPlaying() {
		return isPlaying;
	}

	@Override
	public void release() {
		isPlaying = false;
		if (mAudioTrack != null) {
			if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
				mAudioTrack.stop();
			}
		}
		if (dis != null) {
			try {
				dis.close();
			} catch (IOException e) {
				if(playListener != null){
					playListener.onError(e);
				}
			}
			dis = null;
		}
		if(mAudioTrack != null){
			mAudioTrack.release();
		}
		mAudioTrack = null;
	}

	/**
	 * 播放任务
	 */
	private Runnable playTask = new Runnable() {
		@Override
		public void run() {
			try {
				int streamType = DEFAULT_STREAM_TYPE;
				int bufferSize = 0;
				if(audioPlayerExtraHandle != null){
					streamType = audioPlayerExtraHandle.getStreamType();
					bufferSize = audioPlayerExtraHandle.resetParam(dis, param);
				}
				initAudioTrack(streamType, param);
				bufferSize = bufferSize != 0? bufferSize: mMinBufferSize;
				byte[] tempBuffer = new byte[bufferSize];
				int readCount;
				mAudioTrack.play();
				while (dis.available() > 0 && isPlaying) {
					readCount = dis.read(tempBuffer, 0, bufferSize);
					if (readCount > 0) {
						if(audioPlayerExtraHandle != null){
							audioPlayerExtraHandle.writeTrack(mAudioTrack, tempBuffer, bufferSize);
						} else {
							mAudioTrack.write(tempBuffer, 0, readCount);
						}
					}

					if(dis.available() <= 0){
						isPlaying = false;
						if (playListener != null) {
							playListener.onComplete();
						}
					}
				}
				stop();
			} catch (Exception e) {
				if (playListener != null) {
					playListener.onError(e);
				}
			}
		}
	};

	void setAudioPlayerExtraHandle(AudioPlayerExtraHandle audioPlayerExtraHandle) {
		this.audioPlayerExtraHandle = audioPlayerExtraHandle;
	}

	public interface AudioPlayerExtraHandle {
		int resetParam(InputStream in, AudioPlayParam audioPlayParam) throws IOException;
		int getStreamType();
		void writeTrack(AudioTrack audioTrack, byte[] tempBuffer, int bufferSize);
	}
}
