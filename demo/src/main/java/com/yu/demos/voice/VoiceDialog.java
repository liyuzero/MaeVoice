package com.yu.demos.voice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Window;
import android.view.WindowManager;

public class VoiceDialog extends Dialog {

    private VoiceRecordView voiceView;

    public VoiceDialog(@NonNull Context context) {
        super(context);
    }

    public VoiceDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected VoiceDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.setCanceledOnTouchOutside(false);
        this.setContentView(R.layout.record__dialog_volume_show);
        voiceView = (VoiceRecordView) findViewById(R.id.voiceView);
    }

    public VoiceRecordView getVoiceRecordView() {
        return voiceView;
    }

    public void setVolume(int volume) {
        if(isShowing()) {
            voiceView.setVolume(volume);
        }
    }

    public void setVolumeState(@VoiceRecordView.VolumeState int state) {
        if(isShowing()) {
            voiceView.setVolumeState(state);
        }
    }
}
