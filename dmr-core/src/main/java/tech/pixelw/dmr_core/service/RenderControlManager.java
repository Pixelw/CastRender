package tech.pixelw.dmr_core.service;

import android.content.Context;

import androidx.annotation.Nullable;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import tech.pixelw.dmr_core.IDLNARenderControl;

/**
 * @author Carl Su "Pixelw"
 * @date 2022/1/19
 */
public class RenderControlManager {
    private AVTransportController avControl;
    private AudioRenderController audioControl;

    private final UnsignedIntegerFourBytes[] nullIds = new UnsignedIntegerFourBytes[0];
    private final UnsignedIntegerFourBytes[] hasIds = new UnsignedIntegerFourBytes[]{new UnsignedIntegerFourBytes(0)};

    public RenderControlManager(Context context) {
        audioControl = new AudioRenderController(context);
        avControl = new AVTransportController(context);
    }

    public void attachPlayerControl(IDLNARenderControl control){
        avControl.setMediaControl(control);
    }

    public void detachPlayerControl(){
        avControl.setMediaControl(null);
    }

    @Nullable
    public IRendererInterface.IAVTransportControl getAvTransportControl(UnsignedIntegerFourBytes instanceId) {
        return avControl;
    }

    public UnsignedIntegerFourBytes[] getAvTransportCurrentInstanceIds() {
        if (avControl == null) {
            return nullIds;
        } else {
            return hasIds;
        }
    }

    @Nullable
    public IRendererInterface.IAudioControl getAudioControl(UnsignedIntegerFourBytes instanceId) {
        return audioControl;
    }

    public UnsignedIntegerFourBytes[] getAudioControlCurrentInstanceIds() {
        if (audioControl == null) {
            return nullIds;
        } else {
            return hasIds;
        }
    }
}
