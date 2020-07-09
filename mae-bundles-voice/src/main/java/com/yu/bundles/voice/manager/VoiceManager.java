package com.yu.bundles.voice.manager;

import android.content.Context;

import com.yu.bundles.voice.param.DefaultParam;
import com.yu.bundles.voice.param.VoiceParam;
import com.yu.bundles.voice.param.VoiceType;
import com.yu.bundles.voice.param.play.VoicePlayParam;
import com.yu.bundles.voice.param.record.VoiceRecordParam;
import com.yu.bundles.voice.player.PlayerAPI;
import com.yu.bundles.voice.record.RecordAPI;

import java.lang.reflect.Constructor;

/**
 * Created by liyu on 2017/10/31.
 */
@SuppressWarnings("unchecked")
public class VoiceManager {
    private static volatile VoiceManager voiceManager;
    private RecordManager recordManager;
    private VoiceType recordType;
    private VoiceParam recordParam;

    private PlayerManager playerManager;
    private VoiceType playerType;
    private VoiceParam playerParam;

    public static String cacheDir;

    public static VoiceManager with(Context context){
        cacheDir = context.getCacheDir().getAbsolutePath();
        if(voiceManager == null){
            synchronized (VoiceManager.class){
                if(voiceManager == null){
                    voiceManager = new VoiceManager();
                }
            }
        }
        return voiceManager;
    }

    //不需要生成临时文件
    public static VoiceManager with(){
        cacheDir = "";
        if(voiceManager == null){
            synchronized (VoiceManager.class){
                if(voiceManager == null){
                    voiceManager = new VoiceManager();
                }
            }
        }
        return voiceManager;
    }

    private VoiceManager() {
    }

    /*
        * 获取播放API接口, voiceParam传值为空表示使用默认参数配置
        * */
    public PlayerAPI getPlayerAPI(VoiceType voiceType){
        return getPlayerAPI(voiceType, DefaultParam.getDefaultPlayParam(voiceType));
    }

    public PlayerAPI getPlayerAPI(VoiceType voiceType, VoicePlayParam voicePlayParam){
        return getManager(PlayerManager.class, voiceType, voicePlayParam);
    }

    /*
    * 获取录音API接口, voiceParam传值为空表示使用默认参数配置
    * */
    public RecordAPI getRecordAPI(VoiceType voiceType){
        return getRecordAPI(voiceType, DefaultParam.getDefaultRecordParam(voiceType));
    }

    public RecordAPI getRecordAPI(VoiceType voiceType, VoiceRecordParam voiceRecordParam){
        return getManager(RecordManager.class, voiceType, voiceRecordParam);
    }

    private <T>T getManager(Class clazz, VoiceType voiceType, VoiceParam voiceParam){
        if(voiceType == null){
            throw new NullPointerException("recordType should not be null!");
        }
        boolean isRecord = clazz.getName().equals(RecordManager.class.getName());
        T manager = (T) (isRecord? recordManager: playerManager);
        VoiceType preVoiceType = isRecord? recordType: playerType;
        VoiceParam preVoiceParam = isRecord? recordParam: playerParam;
        if(manager == null){
            synchronized (clazz){
                if(manager == null){
                    manager = getManager(isRecord, voiceType, voiceParam);
                }
            }
        } else {
            synchronized (clazz){
                if(isNeedCreate(preVoiceType, preVoiceParam, voiceType, voiceParam)){
                    manager = getManager(isRecord, voiceType, voiceParam);
                }
            }
        }
        return manager;
    }

    private <T>T getManager(boolean isRecord, VoiceType voiceType, VoiceParam voiceParam){
        if(isRecord){
            this.recordType = voiceType;
            this.recordParam = voiceParam;
            recordManager =  createManager(true, voiceType, voiceParam);
            return (T) recordManager;
        } else {
            this.playerType = voiceType;
            this.playerParam = voiceParam;
            playerManager = createManager(false, voiceType, voiceParam);
            return (T) playerManager;
        }
    }

    private <T>T createManager(boolean isRecord, VoiceType voiceType, VoiceParam voiceParam){
        try {
            Constructor constructor = isRecord? RecordManager.class.getConstructor(VoiceType.class, VoiceParam.class):
                    PlayerManager.class.getConstructor(VoiceType.class, VoiceParam.class);
            return (T) constructor.newInstance(voiceType, voiceParam);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //比对两组参数是否一致
    private boolean isNeedCreate(VoiceType preVoiceType, VoiceParam preVoiceParam, VoiceType voiceType, VoiceParam voiceParam){
        //录音类型不一样
        if(preVoiceType != voiceType){
            return true;
        }
        //录音参数不一致
        if(voiceParam == null && preVoiceParam != null){
            return true;
        }
        if(voiceParam != null && preVoiceParam == null){
            return true;
        }
        //两个录音参数不为空，且参数具体内容不一致
        return voiceParam != null && !voiceParam.equals(preVoiceParam);
    }

    public void onDestroy(){
        if(playerManager != null){
            playerManager.release();
        }
        if(recordManager != null){
            recordManager.release();
        }
        playerManager = null;
        recordManager = null;
        voiceManager = null;
    }

}
