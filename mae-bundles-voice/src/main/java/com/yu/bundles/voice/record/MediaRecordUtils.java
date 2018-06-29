package com.yu.bundles.voice.record;

import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.yu.bundles.voice.BuildConfig;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.MediaRecordParam;

import java.io.File;

import static java.lang.System.currentTimeMillis;

/**
 * MediaRecord 录音工具封装类
 * <pre>
 *     参考：
 *     1.from http://www.cnblogs.com/MMLoveMeMM/articles/3444718.html
 * </pre>
 */
public final class MediaRecordUtils implements RecordAPI{
	// 默认录音格式 amr
	private boolean isRecording = false;
	private MediaRecorder mediaRecorder;
	private long startTime;
	private String filePath;
	private RecordListener recordListener;
	private VoiceType voiceType;
	private MediaRecordParam mediaParam;
	private Handler handler = new Handler();

	public MediaRecordUtils(VoiceType voiceType, MediaRecordParam mediaParam) {
		this.voiceType = voiceType;
		this.mediaParam = mediaParam;
	}

	private void initRecord(){
		if(mediaRecorder == null){
			mediaRecorder = new MediaRecorder();
		} else {
			mediaRecorder.reset();		// 还原，解决 tart stop 切换过快，会有异常抛出
		}

			/* setAudioSource/setVedioSource*/
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风
			/* 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
			* THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			*/
		mediaRecorder.setOutputFormat(mediaParam.getMediaFormat());
			/* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
		mediaRecorder.setAudioEncoder(mediaParam.getMediaEncoder());
	}

	// ------------- about record ---------------------------------------------
	@Override
	public void startRecord(String filePath, final RecordListener listener) {
		if (isRecording)
			return;

		// 接口检查
		this.recordListener = listener;
		if (recordListener == null) {
			throw new RuntimeException("RecordListener can not be null!");
		}
		initRecord();
		// 文件路径检查
		this.filePath = filePath;
		if (!TextUtils.isEmpty(filePath)) {
			// 输出文件路径
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			mediaRecorder.setOutputFile(filePath);
		}

		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			isRecording = true;
			startTime = currentTimeMillis();
			if (recordListener != null) {
				recordListener.onStart();
			}
			updateMicStatus();
		} catch (Exception e) {
			if (recordListener != null) {
				recordListener.onError(e);
			}
		}
	}

	/**
	 * 停止录音，生成文件
	 */
	@Override
	public void stopRecord() {
		if (isRecording && mediaRecorder != null) {
			isRecording = false;
			try {
				mediaRecorder.stop();		// start stop 切换过快，会有异常抛出
				if (recordListener != null) {
					recordListener.onFinishRecord(System.currentTimeMillis() - startTime, filePath);
				}
			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.e(getClass().getName(), ">>> " + "run: ", e);
				}
			}
		}
		isRecording = false;
	}

	/**
	 * 取消语音，删除文件
	 */
	@Override
	public synchronized void cancelRecord() {
		isRecording = false;
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			File file = new File(filePath);
			file.delete();
			if (recordListener != null) {
				recordListener.onCancel();
			}
		}
	}

	@Override
	public boolean isRecording() {
		return isRecording;
	}

	@Override
	public void release() {
		isRecording = false;
		try {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		} catch (Exception e) {
			//stop错误忽略，release为native方法无法捕捉错误，所以此处不处理
		}
	}

	/* --------------------------------------  新增 更新话筒状态  Start ----------------------------------  */

	/**
	 * 录音声音大小变化
	 */
	private void updateMicStatus() {
		if (mediaRecorder != null && isRecording) {
			double ratio = (double)mediaRecorder.getMaxAmplitude();
			double db = 0;// 分贝
			if (ratio > 1)
				db = 20 * Math.log10(ratio);
			if(recordListener != null) {
				recordListener.onAmplitudeChanged((int)db);
			}
			handler.postDelayed(mUpdateMicStatusTimer, mediaParam.getVolumeInterval());
		}
	}

	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};

	/* -------------------------------------- 新增  更新话筒状态  End ----------------------------------  */
}
