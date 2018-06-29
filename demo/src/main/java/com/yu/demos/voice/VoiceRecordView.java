package com.yu.demos.voice;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * voice 录音浮层view
 */
public class VoiceRecordView extends FrameLayout {

    private ImageView imageView;
    private TextView textView;
    private int currentState = NORMAL;

    public static final int NORMAL = 0;
    public static final int TOO_SHORT = 1;
    public static final int CANCEL = -1;

    @IntDef({NORMAL, TOO_SHORT, CANCEL})
    public @interface VolumeState {
    }

    public VoiceRecordView(@NonNull Context context) {
        this(context, null);
    }

    public VoiceRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceRecordView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.popup_audio_wi_vo, this, true);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_volume_1);
        textView.setText(R.string.record__move_up_cancel);
    }

    /**
     * 设置音量
     * @param volume
     */
    public void setVolume(int volume) {
        if(currentState != NORMAL) {
            return;
        }

        if (volume <= 10) {
            imageView.setImageResource(R.drawable.ic_volume_1);
        } else {
            int current = volume / 10;
            switch (current) {
                case 1:
                    imageView.setImageResource(R.drawable.ic_volume_1);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_volume_2);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.ic_volume_3);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.ic_volume_4);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.ic_volume_5);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.ic_volume_6);
                    break;
                case 7:
                    imageView.setImageResource(R.drawable.ic_volume_7);
                    break;
                case 8:
                    imageView.setImageResource(R.drawable.ic_volume_8);
                    default:
                        break;
            }
        }
    }

    /**
     * 设置State
     * @param state
     */
    public void setVolumeState(@VolumeState int state) {
        currentState = state;
        textView.setPressed(false);
        switch (currentState) {
            case CANCEL:
                imageView.setImageResource(R.drawable.ic_volume_cancel);
                textView.setText(R.string.record__release_finger_cancel);
                textView.setPressed(true);  // 设置状态
                break;
            case TOO_SHORT:
                imageView.setImageResource(R.drawable.ic_volume_wraning);
                textView.setText(R.string.record__speak_short);
                break;
            case NORMAL:
            default:
                imageView.setImageResource(R.drawable.ic_volume_1);
                textView.setText(R.string.record__move_up_cancel);
                break;
        }
    }
}
