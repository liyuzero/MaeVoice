package com.yu.demos.voice;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.bundles.voice.manager.VoiceManager;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.player.PlayListener;
import com.yu.bundles.voice.record.RecordAPI;
import com.yu.bundles.voice.record.RecordListener;

import java.io.File;

import static com.yu.demos.voice.VoiceRecordView.NORMAL;

/**
 * 原生页面测试
 */
public class RecordNativeTestActivity extends AppCompatActivity {

    private TextView bottom;
    private boolean isCancel = false;
    private VoiceDialog soundVolumeDialog;
    private RecordAPI recordAPI;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_native_test);
        bottom = (TextView) findViewById(R.id.bottom);
        initSoundVolumeDialog();
        recordAPI = VoiceManager.with(RecordNativeTestActivity.this).getRecordAPI(VoiceType.WAV);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    VoiceManager.with(RecordNativeTestActivity.this).getPlayerAPI(VoiceType.WAV).startPlay(filePath, new PlayListener() {
                        @Override
                        public void onStart() {
                        }
                        @Override
                        public void onStop() {
                        }
                        @Override
                        public void onError(Exception e) {
                        }
                        @Override
                        public void onComplete() {
                        }
                    });
                }
            }
        });

        // 监听事件
        bottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.drawable.record__selector_record_btn_pressed);
                        bottom.setText(R.string.record__release_stop);
                        startRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCancelled(v, event)) {
                            isCancel = true;
                            bottom.setText(R.string.record__release_cancel);
                            soundVolumeDialog.setVolumeState(VoiceRecordView.CANCEL);
                        } else {
                            if (isCancel) {  // 取消状态下，还原操作
                                soundVolumeDialog.setVolumeState(NORMAL);
                            }
                            isCancel = false;
                            bottom.setText(R.string.record__release_stop);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundResource(R.drawable.record__selector_record_btn_normal);
                        bottom.setText(R.string.record__press_speak);
                        stopRecord();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setBackgroundResource(R.drawable.record__selector_record_btn_normal);
                        bottom.setText(R.string.record__press_speak);
                        cancelRecord();
                        break;
                }
                return false;
            }
        });
    }

    private void startRecord() {
        File parent = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/better/");
        parent.mkdirs();
        filePath = parent.getAbsolutePath() + "/myTest.pcm";
        recordAPI.startRecord(filePath, new RecordListener() {
            @Override
            public void onStart() {
                soundVolumeDialog.show();
                soundVolumeDialog.setVolumeState(NORMAL);
            }

            @Override
            public void onFinishRecord(long duration, String filePath) {
                soundVolumeDialog.hide();
                Toast.makeText(RecordNativeTestActivity.this, "记录完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                soundVolumeDialog.hide();
            }

            @Override
            public void onError(Exception e) {
                soundVolumeDialog.hide();
            }

            @Override
            public void onAmplitudeChanged(int volume) {
                Log.e("better", "volume: " + volume);
                soundVolumeDialog.setVolume(volume - 10);
            }

            @Override
            public void onRecordBytes(byte[] audioData, int len, int audioSeq) {

            }
        });
    }

    private void stopRecord() {
        if (isCancel) {
            cancelRecord();
        } else {
            recordAPI.stopRecord();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundVolumeDialog.isShowing()) {
            soundVolumeDialog.hide();
        }
    }

    private void cancelRecord() {
        recordAPI.cancelRecord();
    }

    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        // 左右 or 向上滑
        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth() || event.getRawY() < location[1] - 96) {
            return true;
        }
        return false;
    }

    private void initSoundVolumeDialog() {
        soundVolumeDialog = new VoiceDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager.with(RecordNativeTestActivity.this).onDestroy();
    }
}
