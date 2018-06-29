package com.yu.demos.voice;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yu.bundles.voice.manager.VoiceManager;
import com.yu.bundles.voice.param.DefaultParam;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.player.PlayListener;
import com.yu.bundles.voice.player.PlayerAPI;
import com.yu.bundles.voice.record.RecordAPI;
import com.yu.bundles.voice.record.RecordListener;

import java.io.File;

/**
 * PCM 格式
 */
public class AudioBaseUseActivity extends AppCompatActivity {
    String originPath;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_base_use);

        try {
            // 开通输出流到指定的文件
            File fpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/better");
            fpath.mkdirs();// 创建文件夹
            originPath = fpath.getAbsolutePath() + "/recoding.pcm";
        } catch (Exception e) {
            Log.e(AudioBaseUseActivity.class.getSimpleName(), e.toString());
        }

        initView();
    }

    int total;

    private void initView() {
        final Button recordBtn = findViewById(R.id.one);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAPI recordAPI;
                recordAPI = VoiceManager.with(AudioBaseUseActivity.this).getRecordAPI(VoiceType.PCM_16BIT,
                        DefaultParam.getDefaultAudioRecordParam());//.setIsOutputFile(true);//.setMaxRecordTime(5*1000));
                if (v.getTag() == null) {
                    recordAPI.startRecord(originPath, new RecordListener() {
                        @Override
                        public void onStart() {
                            total = 0;
                            recordBtn.setText("结束");
                        }

                        @Override
                        public void onFinishRecord(long duration, String filePath) {
                            Toast.makeText(getApplicationContext(), "录音结束,时长： " + duration, Toast.LENGTH_SHORT).show();
                            recordBtn.setText("开始");
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getApplicationContext(), "出错啦 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            recordBtn.setText("开始");
                        }

                        @Override
                        public void onAmplitudeChanged(int volume) {

                        }

                        @Override
                        public void onRecordBytes(byte[] audioData, int len, int audioSeq) {
                            total += len;
                            //Log.d("hehe", "语音序列："+audioSeq + "==" + (total));
                        }
                    });
                    v.setTag("stop");
                    ((Button) v).setText("結束");
                } else {
                    v.setTag(null);
                    ((Button) v).setText("開始");
                    recordAPI.stopRecord();
                }
            }
        });

        // 播放
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    Log.d("hehe", "播放"+new File(originPath).length());
                    PlayerAPI playerAPI = VoiceManager.with(AudioBaseUseActivity.this).getPlayerAPI(VoiceType.PCM_16BIT);
                    playerAPI.startPlay(originPath, new PlayListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            isPlaying = false;
                        }
                    });
                } else {
                    PlayerAPI playerAPI = VoiceManager.with(AudioBaseUseActivity.this).getPlayerAPI(VoiceType.PCM_16BIT);
                    playerAPI.stopPlay();
                }
                isPlaying = !isPlaying;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager.with(AudioBaseUseActivity.this).onDestroy();
    }
}
