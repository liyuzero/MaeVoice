package com.yu.bundles.voice.record;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;

import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.AudioRecordParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * AudioRecord 工具类
 * <pre>
 *     参考：
 *     1. http://www.cnblogs.com/MMLoveMeMM/articles/3444718.html
 * </pre>
 */
public final class AudioRecordUtils implements RecordAPI{
	private Handler handler = new Handler(Looper.getMainLooper());
	// 缓冲区字节大小
	private int bufferSizeInBytes = 0;
	// AudioName裸音频数据文件 ，麦克风
	private String outputFilePath = "";
	private AudioRecord audioRecord;
	private boolean isRecording = false;// 设置正在录制的状态
	private RecordListener recordListener;
	private long startTime = 0;
	private TransformFormatOperator transformFormatOperator; //格式转换器\
	private VoiceType voiceType;
	private AudioRecordParam audioParam;
	private Queue<Integer> eventList = new LinkedList<>();
	private int BUFFER_LEN = 0;

	private static final int STOP = 0;
	private static final int CANCEL = 1;
	private static final int NORMAL = 2;
	private static final int RELEASE = 3;

	private int audioSeq = 0; //录音的字节数组的序列号

	public AudioRecordUtils(VoiceType voiceType, AudioRecordParam audioParam){
		this(voiceType, audioParam, null);
	}

	public AudioRecordUtils(VoiceType voiceType, AudioRecordParam audioParam, TransformFormatOperator transformFormatOperator){
		this.transformFormatOperator = transformFormatOperator;
		this.voiceType = voiceType;
		this.audioParam = audioParam;
	}

	private void initRecord(){
		// 获得缓冲区字节大小
		bufferSizeInBytes = AudioRecord.getMinBufferSize(audioParam.sampleRateInHz,
				audioParam.getAudioInChannel(), audioParam.getAudioFormat(voiceType));
		BUFFER_LEN = audioParam.bufferLen > 0? audioParam.bufferLen: bufferSizeInBytes;
		// 创建AudioRecord对象
		audioRecord = new AudioRecord(RecordUtils.DEFAULT_AUDIO_INPUT, audioParam.sampleRateInHz,
				audioParam.getAudioInChannel(), audioParam.getAudioFormat(voiceType), bufferSizeInBytes);
	}

	/**
	 * 是否正在录音
	 */
	@Override
	public boolean isRecording() {
		return isRecording;
	}

	/**
	 * 开始录音
	 *
	 * @param outputFilePath 输出语音文件路径
	 * @param listener 录音监听
	 */
	@Override
	public void startRecord(String outputFilePath, RecordListener listener) {
		if (isRecording) {
			return;
		}
		if (listener == null) {
			throw new RuntimeException("RecordListener can not be null!");
		}
		if(audioRecord == null){
			initRecord();
		}
		if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED){
			audioRecord = null;
			listener.onError(new RuntimeException("Recorder init fail..."));
			return;
		}
		if (RecordUtils.isSdcardExit() && audioParam.isSetOutputFile || !audioParam.isSetOutputFile) {
			this.outputFilePath = audioParam.isSetOutputFile? outputFilePath: this.outputFilePath;
			this.recordListener = listener;
			audioRecord.startRecording();
			int sum = 0;
			for (int i = 0; i < 10;i++){
				byte[] buf = new byte[32];
				int x = audioRecord.read(buf,0,buf.length);
				sum += x;
                if( x > 0) {
                	break;
				}
			}
			if(sum <= 0){
				if(recordListener!=null){
					recordListener.onError(new RuntimeException("bad recorder can not read audio data"));
				}
				return;
			}
			isRecording = true;
			startTime = System.currentTimeMillis();
			listener.onStart();
			//录音字节数组序列号清零
			audioSeq = 0;
			// 开启线程收集原始音频数据
			new Thread(new AudioRecordThread()).start();
		} else {
			if (recordListener != null) {
				recordListener.onError(new Exception("not found SD!"));
			}
		}
	}

	@Override
	public void stopRecord() {
		if(isRecording){
			eventList.offer(STOP);
			isRecording = false;//停止文件写入
		} else if(audioRecord != null){
			onRecordStop(STOP);
		}
	}

	/**
	 * 去掉录制
	 */
	@Override
	public void cancelRecord() {
		if(isRecording){
			eventList.offer(CANCEL);
			isRecording = false;//停止文件写入
		} else if(audioRecord != null){
			onRecordStop(CANCEL);
		}
	}

	@Override
	public void release() {
		if(isRecording){
			eventList.offer(RELEASE);
			isRecording = false;//停止文件写入
		} else if(audioRecord != null){
			onRecordStop(RELEASE);
		}
	}

	class AudioRecordThread implements Runnable {
		@Override
		public void run() {
			try {
				writeDateTOFile(); //往文件中写入裸数据
			} catch (final Exception e){
				e.printStackTrace();
				eventList.offer(STOP);
				if(recordListener != null){
					handler.post(new Runnable() {
						@Override
						public void run() {
							recordListener.onError(e);
						}
					});
				}
			}

			Integer stopState = eventList.poll();
            if (recordListener != null && transformFormatOperator == null && stopState == STOP) {
            	handler.post(new Runnable() {
					@Override
					public void run() {
						recordListener.onFinishRecord(System.currentTimeMillis() - startTime, outputFilePath);
					}
				});
            }
			//如果含有转换器，则对原始音频数据文件进行格式转换，目前支持spx和wav
			if(transformFormatOperator != null && recordListener != null && stopState == STOP){
				final String targetPath = transformFormatOperator.transform(outputFilePath);
				handler.post(new Runnable() {
					@Override
					public void run() {
						recordListener.onFinishRecord(System.currentTimeMillis() - startTime, targetPath);
					}
				});
			}
			onRecordStop(stopState);
			while((stopState = eventList.poll()) != null){
				onRecordStop(stopState); //依据停止录音动作做出相应操作
			}
		}
	}

	/**
	 * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
	 * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
	 * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
	 */
	private synchronized void writeDateTOFile() throws Exception{
		// new一个byte数组用来存一些字节数据，大小为缓冲区大小
		final byte[] audioData = new byte[BUFFER_LEN];
		FileOutputStream fos = getFileOutputStream(outputFilePath);
		while (isRecording) {
			int count = 0;
			while (count < BUFFER_LEN && audioRecord != null) {
                int readSize = audioRecord.read(audioData, count, BUFFER_LEN - count);
				if (readSize < 0) {
					break ;
				}
				count += readSize;
			}
			if(count > 0){
				if(recordListener != null){
					handler.post(new VolumeBytesRun(computePower(audioData, count), audioSeq++, count, Arrays.copyOf(audioData, audioData.length)));
				}
				if (fos != null) {
					try {
						fos.write(audioData);
					} catch (final IOException e) {
						if (recordListener != null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									recordListener.onError(e);
								}
							});
						}
					}
				}
			}
		}
		if (fos != null)
			fos.close();// 关闭写入流
	}

	private class VolumeBytesRun implements Runnable {
		private long volume;
		private int id;
		private int count;
		private byte[] data;

		private VolumeBytesRun(long volume, int id, int count, byte[] data) {
			this.volume = volume;
			this.id = id;
			this.count = count;
			this.data = data;
		}

		@Override
		public void run() {
			if(volume > 0){
				recordListener.onAmplitudeChanged((int)volume);
			}
			recordListener.onRecordBytes(data, count, id);
		}
	}

	private synchronized void onRecordStop(int stopState){
		switch (stopState){
			case RELEASE:
				if(audioRecord != null){
					if(audioRecord.getState() != AudioRecord.RECORDSTATE_STOPPED){
						audioRecord.stop();
					}
					audioRecord.release();//释放资源
					audioRecord = null;
				}
				break;
			case CANCEL:
				File file = new File(outputFilePath);
				if (file.exists()) {
					file.delete();
				}
				if (recordListener != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							recordListener.onCancel();
						}
					});
				}
			case STOP:
				if(audioRecord != null && audioRecord.getState() != AudioRecord.RECORDSTATE_STOPPED){
					audioRecord.stop();
				}
				break;

			default:
				break;
		}
	}

	private FileOutputStream getFileOutputStream(String outputFilePath) throws Exception{
		FileOutputStream fos = null;
		if(!audioParam.isSetOutputFile){
			return fos;
		}
		File file = new File(outputFilePath);
		if (file.exists()) {
			file.delete();
		}
		file.getParentFile().mkdirs();
		fos = new FileOutputStream(file);// 建立一个可存取字节的文件
		return fos;
	}

	private long lastComputePowerTime;

	private long computePower(byte[] buffer, int length) {
		if(System.currentTimeMillis() - lastComputePowerTime > audioParam.getVolumeInterval()){
			final short[] data = new short[length / 2];
			for (int i = 0; i < data.length; i++) {
				data[i] = (short) ((buffer[2 * i + 1] << 8) | (buffer[2 * i] & 0xff));
			}
			lastComputePowerTime = System.currentTimeMillis();
			return (long) Math.sqrt(computePower(data, data.length)); // 0 - 100
		} else {
			return -1;
		}
	}

	/**
	 * 计算语音功率
	 *
	 * @param voiceBuf    原始语音 short数组
	 * @param sizeInShort 长度
	 * @return 功率 平方均值再开方
	 */
	private static double computePower(short[] voiceBuf, int sizeInShort) {
		if (voiceBuf == null) {
			return 0;
		}
		int sampleRate = 2; //采样频率
		//最多只计算512个点
		int sampleSize = Math.min(sizeInShort / sampleRate, 512); //SUPPRESS CHECKSTYLE
		if (sampleSize <= 0) {
			return 0;
		}
		long sum = 0;
		for (int i = 0; i < sampleSize; i++) {
			sum += voiceBuf[i * sampleRate] * voiceBuf[i * sampleRate];
		}
		return Math.sqrt((sum / sampleSize));
	}


	public int getBufferSizeInBytes() {
		return bufferSizeInBytes;
	}
}

