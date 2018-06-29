==================
语音录制、播放模块
支持基于Media和Audio的语音录制和播放。

## 功能类别
- 语音录制
    1. 支持Android直接录制格式：PCM16，PCM8，AMR等
    2. 支持基于原始音频格式的包装扩展格式，目前支持SPX和WAV
    3. Audio录制支持采样频率、通道等参数的自定义
    4. Media录制支持自定义编码格式
- 语音播放
    1. 支持PCM，AMR，SPX，WAV等格式的播放

## 使用方法
### 新增依赖
1. 在项目的根目录gradle新增仓库如下：

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

2. 使用module依赖，新增依赖：

```
compile 'com.github.liyuzero:MaeBundlesVoice:1.0.0'
```

### 具体调用（详情见demo）
1. 录音使用方法

```
    RecordAPI recordAPI = VoiceManager.with(activity).getRecordAPI(VoiceType.PCM_16BIT);
    /* 添加录制参数
    RecordAPI recordAPI = VoiceManager.with(activity).getRecordAPI(VoiceType.PCM_16BIT,
            new AudioRecordParam(16000, AudioRecordParam.AudioInChannel.CHANNEL_IN_MONO));*/
    // 开始录制：outputPath：String 音频录制输出文件路径
    recordAPI.startRecord(outputPath, new RecordListener() {
    						@Override
    						public void onStart() {
    						}
    						@Override
    						public void onFinishRecord(long duration, String filePath) {
    							Toast.makeText(getApplicationContext(), "录音结束,时长： " + duration, Toast.LENGTH_SHORT).show();
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
    					});

    //停止并完成录制
    recordAPI.stopRecord();
    //检查是否正在录制
    //recordAPI.isRecording();
    //取消录制
    //recordAPI.cancelRecord();
    //释放资源
    //recordAPI.release();

```

2. 播放音频使用方法

```
	PlayerAPI playerAPI = VoiceManager.with(activity).getPlayerAPI(VoiceType.PCM_16BIT);
	//自定义播放参数【只有Audio需要且可以自定义参数】
	/*playerAPI = VoiceManager.with(activity).getPlayerAPI(VoiceType.PCM_16BIT,
	    new AudioPlayParam(16000, AudioRecordParam.AudioInChannel.CHANNEL_IN_MONO));*/
	//originPath：String 音频文件路径
	playerAPI.startPlay(originPath, new PlayListener() {
		@Override
		public void onStart() {
		}
		@Override
		public void onStop() {
		}
		@Override
		public void onError(Exception e) {
		}
	});

	//停止播放
	playerAPI.stopPlay();
	//是否正在播放
    playerAPI.isPlaying();
    //释放资源
    playerAPI.release();
```

3. 整体释放资源方法[自动执行]（整体释放播放和录制资源，目前跟随Activity生命周期的onDestroy()自动销毁，也可自己手动调用）

```
   VoiceManager.getInstance().onDestroy();
```