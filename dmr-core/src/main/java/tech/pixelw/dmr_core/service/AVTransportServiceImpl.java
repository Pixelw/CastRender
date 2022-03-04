package tech.pixelw.dmr_core.service;

import android.util.Log;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;

public class AVTransportServiceImpl extends AbstractAVTransportService {
    private final RenderControlManager mIRenderControlManager;
    private static final String TAG = "AVTransportServiceImpl";

    public AVTransportServiceImpl(LastChange lastChange, RenderControlManager IRenderControlManager) {
        super(lastChange);
        mIRenderControlManager = IRenderControlManager;
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        Log.d(TAG, "getCurrentInstanceIds: ");
        return mIRenderControlManager.getAvTransportCurrentInstanceIds();
    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerFourBytes instanceId) throws Exception {
        Log.d(TAG, "getCurrentTransportActions: ");
        return mIRenderControlManager.getAvTransportControl(instanceId).getCurrentTransportActions();
    }
    @Override
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "getDeviceCapabilities: ");
        return mIRenderControlManager.getAvTransportControl(instanceId).getDeviceCapabilities();
    }
    @Override
    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "getMediaInfo: ");
        return mIRenderControlManager.getAvTransportControl(instanceId).getMediaInfo();
    }
    @Override
    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "getPositionInfo: ");
        return mIRenderControlManager.getAvTransportControl(instanceId).getPositionInfo();
    }
    @Override
    public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "getTransportInfo: ");
        return mIRenderControlManager.getAvTransportControl(instanceId).getTransportInfo();
    }
    @Override
    public TransportSettings getTransportSettings(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "getTransportSettings: ");
        return mIRenderControlManager.getAvTransportControl(instanceId).getTransportSettings();
    }

    @Override
    public void next(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "next: ");
        mIRenderControlManager.getAvTransportControl(instanceId).next();
    }
    @Override
    public void pause(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        Log.d(TAG, "pause: ");
        mIRenderControlManager.getAvTransportControl(instanceId).pause();
    }
    @Override
    public void play(UnsignedIntegerFourBytes instanceId, String arg1) throws AVTransportException {
        Log.d(TAG, "play: ");
        mIRenderControlManager.getAvTransportControl(instanceId).play(arg1);
    }
    @Override
    public void previous(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "previous: ");
        mIRenderControlManager.getAvTransportControl(instanceId).previous();
    }
    @Override
    public void record(UnsignedIntegerFourBytes instanceId) {
        Log.d(TAG, "record: ");
        mIRenderControlManager.getAvTransportControl(instanceId).record();
    }

    @Override
    public void seek(UnsignedIntegerFourBytes instanceId, String arg1, String arg2) throws AVTransportException {
        Log.d(TAG, "seek: ");
        mIRenderControlManager.getAvTransportControl(instanceId).seek(arg1, arg2);
    }

    @Override
    public void setAVTransportURI(UnsignedIntegerFourBytes instanceId, String arg1, String arg2) throws AVTransportException {
        Log.d(TAG, "setAVTransportURI: ");
        mIRenderControlManager.getAvTransportControl(instanceId).setAVTransportURI(arg1, arg2);
    }

    @Override
    public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId, String arg1, String arg2) {
        Log.d(TAG, "setNextAVTransportURI: ");
        mIRenderControlManager.getAvTransportControl(instanceId).setNextAVTransportURI(arg1, arg2);
    }

    @Override
    public void setPlayMode(UnsignedIntegerFourBytes instanceId, String arg1) {
        Log.d(TAG, "setPlayMode: ");
        mIRenderControlManager.getAvTransportControl(instanceId).setPlayMode(arg1);
    }

    @Override
    public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId, String arg1) {
        Log.d(TAG, "setRecordQualityMode: ");
        mIRenderControlManager.getAvTransportControl(instanceId).setRecordQualityMode(arg1);
    }

    @Override
    public void stop(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        Log.d(TAG, "stop: ");
        mIRenderControlManager.getAvTransportControl(instanceId).stop();
    }

}
