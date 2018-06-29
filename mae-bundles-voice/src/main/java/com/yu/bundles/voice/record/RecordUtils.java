package com.yu.bundles.voice.record;

import android.media.MediaRecorder;
import android.os.Environment;

import java.util.Arrays;

/**
 * Created by liyu on 2017/10/31.
 */

public class RecordUtils {
    // 基本的文件路径
    private static String BASE_DIR = "better";            // 录音目录，此目录可修改
    //临时文件目录
    private static String TMEP_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + RecordUtils.BASE_DIR;
    /**
     * 音频输入
     */
    public final static int DEFAULT_AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    public static String getTmepFileDir() {
        return TMEP_FILE_DIR;
    }

    //字节数组转换为short数组
    public static short[] byteArray2ShortArray(byte[] data) {
        int len = data.length >> 1; //除以2
        short[] out = new short[len];
        Arrays.fill(out, (short) 0);
        for (int i = 0; i < len; i++) {
            out[i] = (short) (data[i * 2] & 0xff | (data[i * 2 + 1] & 0xff) << 8);
        }
        return out;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
