package com.yu.bundles.voice.record.audio_extend;

import android.util.Log;

import com.yu.bundles.voice.BuildConfig;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.AudioRecordParam;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * AudioRecord 工具类
 * <pre>
 *     参考：
 *     1. http://www.cnblogs.com/MMLoveMeMM/articles/3444718.html
 * </pre>
 */
public final class WavRecordUtils extends BaseAudioRecordExtendUtils {

	public WavRecordUtils(VoiceType voiceType, AudioRecordParam audioParam) {
		super(voiceType, audioParam);
	}

	@Override
	protected String getTempFileName() {
		return "maeTempWav.pcm";
	}

	@Override
	protected String transform(String originFilePath, String outputFilePath) {
		copyWaveFile(originFilePath, outputFilePath);
		return outputFilePath;
	}

	// 这里得到可播放的音频文件
	private void copyWaveFile(String originFilePath, String outputFilePath) {
		FileInputStream in;
		FileOutputStream out;
		long totalAudioLen;
		long totalDataLen;
		long longSampleRate = audioParam.sampleRateInHz;
		int channels = 1;
		long byteRate = 16 * audioParam.sampleRateInHz * channels / 8;
		byte[] data = new byte[audioRecordUtils.getBufferSizeInBytes()];
		try {
			in = new FileInputStream(originFilePath);
			out = new FileOutputStream(outputFilePath);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			if (BuildConfig.DEBUG) {
				Log.e(getClass().getName(), ">>> " + "run: ", e);
			}
			e.printStackTrace();
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(getClass().getName(), ">>> " + "run: ", e);
			}
			e.printStackTrace();
		}
	}

	/*
    任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
    FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的，
     */
	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
									 int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);//数据大小
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';//WAVE
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		//FMT Chunk
		header[12] = 'f'; // 'fmt '
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';//过渡字节
		//数据大小
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		//编码方式 10H为PCM编码格式
		header[20] = 1; // format = 1
		header[21] = 0;
		//通道数
		header[22] = (byte) channels;
		header[23] = 0;
		//采样率，每个通道的播放速度
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		//音频数据传送速率,采样率*通道数*采样深度/8
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		// 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
		header[32] = (byte) (1 * 16 / 8);
		header[33] = 0;
		//每个样本的数据位数
		header[34] = 16;
		header[35] = 0;
		//Data chunk
		header[36] = 'd';//data
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}
}

