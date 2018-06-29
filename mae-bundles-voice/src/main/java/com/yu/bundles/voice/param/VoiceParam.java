package com.yu.bundles.voice.param;

/**
 * Created by liyu on 2017/10/31.
 */

public abstract class VoiceParam {

    protected VoiceParam() {
    }

    @Override
    public boolean equals(Object obj) {
        return obj == null? false: toString().equals(obj.toString());
    }
}
