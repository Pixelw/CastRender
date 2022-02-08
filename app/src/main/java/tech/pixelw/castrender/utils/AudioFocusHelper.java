package tech.pixelw.castrender.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/9/26
 */
public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "AudioFocusHelper";
    private final AudioManager audioManager;
    private Play play;
    private Pause pause;
    private Duck duck;

    private boolean resumeOnGain = true;

    public boolean isResumeOnGain() {
        return resumeOnGain;
    }
    public AudioFocusHelper(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public void setPause(Pause pause) {
        this.pause = pause;
    }

    public void setDuck(Duck duck) {
        this.duck = duck;
    }

    public boolean gainAudioFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Handler handler = new Handler(Looper.getMainLooper());
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build();
            AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this, handler)
                    .build();
            int result = audioManager.requestAudioFocus(focusRequest);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
    }

    public void abandonAudioFocus(){
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        LogUtil.i(TAG, "focus change = " + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (play != null && resumeOnGain) {
                    play.play();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                resumeOnGain = false;
                if (pause != null) {
                    pause.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                resumeOnGain = true;
                if (pause != null) {
                    pause.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                resumeOnGain = true;
                if (duck != null) {
                    duck.duck();
                }
                break;
        }
    }

    public interface Play {
        void play();
    }

    public interface Pause {
        void pause();
    }

    public interface Duck {
        void duck();
    }
}
