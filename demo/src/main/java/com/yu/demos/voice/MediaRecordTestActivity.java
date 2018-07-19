package com.yu.demos.voice;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yu.bundles.voice.manager.VoiceManager;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.player.PlayListener;
import com.yu.bundles.voice.record.RecordListener;

import java.io.File;

public class MediaRecordTestActivity extends AppCompatActivity {
	private Button startBtn;
	private String filePath;
	private boolean isPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_record_test);
		startBtn = (Button) findViewById(R.id.start);

		File parent = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/better/");
		parent.mkdirs();
		filePath = parent.getAbsolutePath() + "/test.amr";

		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (v.getTag() == null) {
					VoiceManager.with(MediaRecordTestActivity.this).getRecordAPI(VoiceType.AMR).startRecord(filePath, new RecordListener() {
						@Override
						public void onStart() {
							v.setTag("1111");
							startBtn.setText("结束");
						}

						@Override
						public void onFinishRecord(long duration, String filePath) {
							Toast.makeText(getApplicationContext(), "录音结束,时长： " + duration, Toast.LENGTH_SHORT).show();
							v.setTag(null);
							startBtn.setText("开始");
						}

						@Override
						public void onCancel() {
							v.setTag(null);
							startBtn.setText("开始");
						}

						@Override
						public void onError(Exception e) {
							Toast.makeText(getApplicationContext(), "出错啦 " + e.toString(), Toast.LENGTH_SHORT).show();
							v.setTag(null);
							startBtn.setText("开始");
						}

						@Override
						public void onAmplitudeChanged(int volume) {

						}

						@Override
						public void onRecordBytes(byte[] audioData, int len, int audioSeq) {

						}
					});
				} else {
					VoiceManager.with(MediaRecordTestActivity.this).getRecordAPI(VoiceType.AMR).stopRecord();
					v.setTag(null);
					startBtn.setText("开始");
				}
			}
		});

		// 播放
		findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isPlaying){
					VoiceManager.with(MediaRecordTestActivity.this).getPlayerAPI(VoiceType.AMR).startPlay(filePath, new PlayListener() {
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
							isPlaying = false;
						}
					});
				} else {
					VoiceManager.with(MediaRecordTestActivity.this).getPlayerAPI(VoiceType.AMR).stopPlay();
				}
				isPlaying = !isPlaying;

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VoiceManager.with(MediaRecordTestActivity.this).onDestroy();
	}
}
