package com.yu.demos.voice;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.yu.bundles.voice.manager.VoiceManager;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.record.AudioRecordParam;
import com.yu.bundles.voice.record.RecordAPI;
import com.yu.bundles.voice.record.RecordListener;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by liyu on 2017/10/26.
 */

public class H5TestActivity extends AppCompatActivity {

    private RecordAPI recordAPI;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);

        webView = (WebView) findViewById(R.id.webview);
        webView.setBackgroundColor(Color.parseColor("#ffffff"));
        if (Build.VERSION.SDK_INT >= 19) {  // 去掉硬件加速
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        CookieManager.getInstance().removeAllCookie();

        setListener(webView);
        String url = getIntent().getStringExtra("url");
        boolean showHeader = getIntent().getBooleanExtra("showHeader", true);
        if(!showHeader) {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        } else {
            finish();
        }
    }

    /**
     * 设置加载监听
     */
    private void setListener(final WebView webView) {
        // 增加js支持
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        // 视频播放有声音无图像问题
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // 自动加载图片
        webView.getSettings().setLoadsImagesAutomatically(true);
        // 控件滚动条位置
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // 支持JavaScript调用
        webView.addJavascriptInterface(new RecordJavaScriptInterface(), "Android");
        webView.requestFocus();

        // 与网页配合，支持手势  <meta name="viewport" content="width=device-width,user-scalable=yes  initial-scale=1.0, maximum-scale=4.0">
        WebSettings settings = webView.getSettings();
        webView.setVerticalScrollbarOverlay(true);

        //settings.setUseWideViewPort(false);//设定支持viewport
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);//设定支持缩放

        webView.getSettings().setDomStorageEnabled(true);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getExternalCacheDir().getAbsolutePath();//Apps.getAppContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        //启用地理定位
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        settings.setGeolocationDatabasePath(appCachePath);

        // 允许 https 加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {    // 表示按返回键时的操作
                            webView.goBack();
                            return true; // 已处理
                        }
                    }
                    return false;
                }
            }
        });
    }

    /**
     * JavaScript调用Native
     */
    private class RecordJavaScriptInterface {
        String originPath;

        /**
         * 格式类型
         *
         * @param type
         * @return
         */
        private VoiceType getVoiceType(int type) {
            switch (type) {
                case 1:
                    return VoiceType.PCM_16BIT;
                case 2:
                    return VoiceType.PCM_8BIT;
                case 3:
                    return VoiceType.WAV;
                case 4:
                    return VoiceType.AMR;
            }
            return VoiceType.PCM_16BIT;
        }

        /**
         * 目前只支持 PCM 无损格式
         *
         * @param json
         * @return
         */
        private void initRecordAPI(String json) {
            try {
                JSONObject object = new JSONObject(json);
                // audioRate： 采样频率字段。目前使用：16000
                int rate = object.getInt("audioRate");
                // 文件格式 (1:PCM_16位，2：PCM_位，3：WAV格式，4：AMR格式，5：SPX格式)
                VoiceType voiceType = getVoiceType(object.getInt("audioFormat"));
                Object channel;
                if (object.getInt("channelConfig") == 16) { // 通道，目前：单声道：16
                    channel = AudioRecordParam.AudioInChannel.CHANNEL_IN_MONO;
                } else {
                    channel = AudioRecordParam.AudioInChannel.CHANNEL_IN_STEREO;
                }
                recordAPI = VoiceManager.with(H5TestActivity.this).getRecordAPI(voiceType, new AudioRecordParam(rate, (AudioRecordParam.AudioInChannel) channel));
            } catch (Exception e) {
                Log.e(H5TestActivity.class.getSimpleName(), e.toString());
            }
        }

        @JavascriptInterface
        public void startRecording(String json) {
            Log.d("hehe", "开始录音：" + json);

            // ===== 1.初始化录音组件
            initRecordAPI(json);
            if (recordAPI == null) {
                Toast.makeText(getApplicationContext(), "录音组件初始化失败，请检查参数设置！ " + json, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // 2. ==== 设置临时文件：开通输出流到指定的文件
                File fpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/better");
                fpath.mkdirs();// 创建文件夹
                File mAudioFile = new File(fpath, "recoding.wav");
                originPath = fpath.getAbsolutePath() + "/recoding.wav";

                // 3.==== 判断是否有需要uploadUrl
                JSONObject object = new JSONObject(json);
                final String uploadUrl = object.optString("uploadUrl");

                // 4.=== 开始录音
                recordAPI.startRecord(originPath, new RecordListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinishRecord(long duration, final String filePath) {
                        Toast.makeText(getApplicationContext(), "录音结束" + duration, Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(uploadUrl)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("hehe", "开始上传" + filePath);
                                    final String info = UploadUtils.getResponseString(uploadUrl, filePath);
                                    Log.d("hehe", info);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (H5TestActivity.this != null && !H5TestActivity.this.isFinishing()) {
                                                webView.loadUrl("javascript:parseRecordComplete('" + info + "')");
                                            }
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (H5TestActivity.this != null && !H5TestActivity.this.isFinishing()) {
                                        webView.loadUrl("javascript:parseRecordComplete('" + filePath + "')");
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(), "出错啦 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAmplitudeChanged(int volume) {
                    }

                    @Override
                    public void onRecordBytes(byte[] audioData, int len, int audioSeq) {

                    }
                });
            } catch (Exception e) {
                Log.e(H5TestActivity.class.getSimpleName(), e.toString());
            }
        }

        @JavascriptInterface
        public void finishRecording() {
            if (recordAPI != null) {
                recordAPI.stopRecord();
            }
        }

        @JavascriptInterface
        public void cancelRecording() {
            if(recordAPI != null) {
                recordAPI.cancelRecord();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager.with(H5TestActivity.this).onDestroy();
    }
}
