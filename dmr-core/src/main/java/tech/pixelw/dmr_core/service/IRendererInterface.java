package tech.pixelw.dmr_core.service;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;

/**
 *
 */
public interface IRendererInterface {

    public interface IControl {
        UnsignedIntegerFourBytes getInstanceId();
    }

    // -------------------------------------------------------------------------------------------
    // - AvTransport
    // -------------------------------------------------------------------------------------------
    public interface IAVTransportControl extends IControl {
        void setAVTransportURI(String currentURI, String currentURIMetaData) throws AVTransportException;

        void setNextAVTransportURI(String nextURI, String nextURIMetaData);

        void setPlayMode(String newPlayMode);

        void setRecordQualityMode(String newRecordQualityMode);

        void play(String speed) throws AVTransportException;

        void pause() throws AVTransportException;

        void seek(String unit, String target) throws AVTransportException;

        void previous();

        void next();

        void stop() throws AVTransportException;

        void record();

        TransportAction[] getCurrentTransportActions() throws Exception;

        DeviceCapabilities getDeviceCapabilities();

        MediaInfo getMediaInfo();

        PositionInfo getPositionInfo();

        TransportInfo getTransportInfo();

        TransportSettings getTransportSettings();
    }

    // -------------------------------------------------------------------------------------------
    // - Audio
    // -------------------------------------------------------------------------------------------
    public interface IAudioControl extends IControl {
        void setMute(String channelName, boolean desiredMute);

        boolean getMute(String channelName);

        void setVolume(String channelName, UnsignedIntegerTwoBytes desiredVolume);

        UnsignedIntegerTwoBytes getVolume(String channelName);
    }
}