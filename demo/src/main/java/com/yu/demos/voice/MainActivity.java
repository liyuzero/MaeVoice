package com.yu.demos.voice;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.yu.bundles.monitorfragment.MAEMonitorFragment;
import com.yu.bundles.monitorfragment.MAEPermissionCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText urlEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Toolbar_Test");

        // 使用MediaPlayer
        findViewById(R.id.media_player).setOnClickListener(this);
        findViewById(R.id.audio_player).setOnClickListener(this);
        findViewById(R.id.native_test).setOnClickListener(this);
        findViewById(R.id.h5_test_use).setOnClickListener(this);
        urlEdit = findViewById(R.id.url);

        Switch swith = findViewById(R.id.test_switch);
        swith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.container).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        MAEMonitorFragment.getInstance(MainActivity.this).requestPermission(new String[]{Manifest.permission.GET_ACCOUNTS},
                new MAEPermissionCallback() {
                    @Override
                    public void onPermissionApplySuccess() {
                        switch (v.getId()) {
                            case R.id.media_player:
                                startActivity(new Intent(getApplicationContext(), MediaRecordTestActivity.class));
                                break;
                            case R.id.audio_player:
                                startActivity(new Intent(getApplicationContext(), AudioBaseUseActivity.class));
                                break;
                            case R.id.native_test:
                                startActivity(new Intent(getApplicationContext(), RecordNativeTestActivity.class));
                                break;
                            case R.id.h5_test_use:
                                if (TextUtils.isEmpty(urlEdit.getText())) {
                                    Toast.makeText(getApplicationContext(), "输入URL地址", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                CheckBox showHeader = findViewById(R.id.showHeader);
                                Intent intent = new Intent(getApplicationContext(), H5TestActivity.class);
                                intent.putExtra("url", urlEdit.getText().toString());
                                intent.putExtra("showHeader",showHeader.isChecked());
                                startActivity(intent);
                                break;
                        }
                    }

                    @Override
                    public void onPermissionApplyFailure(List<String> list, List<Boolean> list1) {
                        Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
