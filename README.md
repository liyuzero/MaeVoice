==================
相册组件
====
语音录制、播放模块
支持基于Media和Audio的语音录制和播放。
支持基于PCM的格式扩展操作

## 功能类别
- 语音录制
    1. 支持Android直接录制格式：PCM16，PCM8，AMR等
    2. 支持基于原始音频格式的包装扩展格式，目前支持WAV
    3. Audio录制支持采样频率、通道等参数的自定义
    4. Media录制支持自定义编码格式
- 语音播放
    1. 支持PCM，AMR，WAV等格式的播放

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
compile 'com.github.liyuzero:mae-bundles-voice:1.0.0'
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

3. 自定义PCM文件扩展，即提供基于PCM原始文件的文件转换能力
```
        AudioRecordParam param = DefaultParam.getDefaultAudioRecordParam());
        param.setPcmFileConverter(new WavRecordUtils()); //改成你自己的基于BaseAudioRecordExtendUtils的实现类
        
        /*
         * 文件转换类自行实现文件转换方法,具体实现参考WavRecordUtils
            public class TestFileConvater extends BaseAudioRecordExtendUtils {
                
                public TestFileConvater(VoiceType voiceType, AudioRecordParam audioParam) {
                    super(voiceType, audioParam);
                }
            
                @Override
                //设置临时文件的名字，对应于originFilePath
                protected String getTempFileName() {
                    return null;
                }
            
                //将生成的PCM文件originFilePath：自行转换为outputFilePath所对应的文件，outputFilePath对应于外界指定的文件录音地址
                @Override
                protected String transform(String originFilePath, String outputFilePath) {
                    return null;
                }
            }
         *
         */
        
        recordAPI = VoiceManager.with(RecordNativeTestActivity.this).getRecordAPI(VoiceType.WAV, param);

```

4、支持不输出文件
    而只进行录音的PCM原始字节数组的实时传出（字节数组内部进行了一次复制传出，避免了多线程下数据错乱的问题）
    即：只调用 onRecordBytes(byte[] audioData, int len, int audioSeq)，而不进行文件写入
    该功能能有效支持以下应用场景：
    例如我们需要做一个语音转文字功能，或者聊天时将语音发给对方，这时候如果采用录制完后，再将所有语音数据传输给后台，
    将会出现较大的响应延迟，客户端需要经过一个较长的时间才能取得结果。这时候如果不录制文件，而直接通过onRecordBytes
    接收每一帧音频数据直接发送到服务端，实现实时传输，这时候效果会好很多
```
    AudioRecordParam param = DefaultParam.getDefaultAudioRecordParam());
    //byte[] audioData【每次通过麦克风采集的录音数据】的数组大小可以通过以下配置自行控制，因为一些语音数据压缩库可能对源字节数组的大小有限制，
    //例如opus，会有：
    //频率 * 位数 * 通道数 / 位 = B/s /(200ms / 1000ms) = B/200ms
    //bufferLen = getSampleRate() * 16 * 1 / 8 / 5;
    param.bufferLen = 14000;
    //当配置以下参数时，库内部将不再执行文件写入操作，而只会调用
    //onRecordBytes(byte[] audioData, int len, int audioSeq) 
    //和 onFinishRecord(long duration, String filePath)，其中filePath无意义
    param.setIsOutputFile(false); //不输出录音文件
    
```

5. 释放麦克风等资源的方法

```
   VoiceManager.getInstance().onDestroy();
```