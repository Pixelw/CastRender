package tech.pixelw.dmr_core.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RenderControlManager {

    // TODO: 2021/12/7 what this var for?
//    private UnsignedIntegerFourBytes[] avControlInstanceIds = null;
    private final Map<UnsignedIntegerFourBytes, IRendererInterface.IAVTransportControl> avControlMap = new LinkedHashMap<>();
    //    private UnsignedIntegerFourBytes[] audioControlInstanceIds = null;
    private final Map<UnsignedIntegerFourBytes, IRendererInterface.IAudioControl> audioControlMap = new LinkedHashMap<>();

    // TODO: 2021/12/6 need remove?
    public void addControl(@NonNull IRendererInterface.IControl control) {
        if (control instanceof IRendererInterface.IAVTransportControl) {
            avControlMap.put(control.getInstanceId(), (IRendererInterface.IAVTransportControl) control);
        } else if (control instanceof IRendererInterface.IAudioControl) {
            audioControlMap.put(control.getInstanceId(), (IRendererInterface.IAudioControl) control);
        }
    }

    public void removeControl(@NonNull IRendererInterface.IControl control){
        if (control instanceof IRendererInterface.IAVTransportControl) {
            avControlMap.remove(control.getInstanceId());
        } else if (control instanceof IRendererInterface.IAudioControl) {
            audioControlMap.remove(control.getInstanceId());
        }
    }

    @Nullable
    public IRendererInterface.IAVTransportControl getAvTransportControl(UnsignedIntegerFourBytes instanceId) {
        return avControlMap.get(instanceId);
    }

    public UnsignedIntegerFourBytes[] getAvTransportCurrentInstanceIds() {
        return avControlMap.keySet().toArray(new UnsignedIntegerFourBytes[0]);
    }

    @Nullable
    public IRendererInterface.IAudioControl getAudioControl(UnsignedIntegerFourBytes instanceId) {
        return audioControlMap.get(instanceId);
    }

    public UnsignedIntegerFourBytes[] getAudioControlCurrentInstanceIds() {
        return audioControlMap.keySet().toArray(new UnsignedIntegerFourBytes[0]);
    }
}
