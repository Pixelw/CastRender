package tech.pixelw.dmr_core.service;

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
        return mIRenderControlManager.getAvTransportCurrentInstanceIds();
    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerFourBytes instanceId) throws Exception {
        return mIRenderControlManager.getAvTransportControl(instanceId).getCurrentTransportActions();
    }

    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) {
        return mIRenderControlManager.getAvTransportControl(instanceId).getDeviceCapabilities();
    }

    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId) {
        return mIRenderControlManager.getAvTransportControl(instanceId).getMediaInfo();
    }

    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId) {
        return mIRenderControlManager.getAvTransportControl(instanceId).getPositionInfo();
    }

    public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId) {
        return mIRenderControlManager.getAvTransportControl(instanceId).getTransportInfo();
    }

    public TransportSettings getTransportSettings(UnsignedIntegerFourBytes instanceId) {
        return mIRenderControlManager.getAvTransportControl(instanceId).getTransportSettings();
    }

    @Override
    public void next(UnsignedIntegerFourBytes instanceId) {
        mIRenderControlManager.getAvTransportControl(instanceId).next();
    }

    @Override
    public void pause(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        mIRenderControlManager.getAvTransportControl(instanceId).pause();
    }

    @Override
    public void play(UnsignedIntegerFourBytes instanceId, String arg1) throws AVTransportException {
        mIRenderControlManager.getAvTransportControl(instanceId).play(arg1);
    }

    @Override
    public void previous(UnsignedIntegerFourBytes instanceId) {
        mIRenderControlManager.getAvTransportControl(instanceId).previous();
    }

    @Override
    public void record(UnsignedIntegerFourBytes instanceId) {
        mIRenderControlManager.getAvTransportControl(instanceId).record();
    }

    @Override
    public void seek(UnsignedIntegerFourBytes instanceId, String arg1, String arg2) throws AVTransportException {
        mIRenderControlManager.getAvTransportControl(instanceId).seek(arg1, arg2);
    }

    @Override
    public void setAVTransportURI(UnsignedIntegerFourBytes instanceId, String arg1, String arg2) throws AVTransportException {
        mIRenderControlManager.getAvTransportControl(instanceId).setAVTransportURI(arg1, arg2);
    }

    @Override
    public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId, String arg1, String arg2) {
        mIRenderControlManager.getAvTransportControl(instanceId).setNextAVTransportURI(arg1, arg2);
    }

    @Override
    public void setPlayMode(UnsignedIntegerFourBytes instanceId, String arg1) {
        mIRenderControlManager.getAvTransportControl(instanceId).setPlayMode(arg1);
    }

    @Override
    public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId, String arg1) {
        mIRenderControlManager.getAvTransportControl(instanceId).setRecordQualityMode(arg1);
    }

    @Override
    public void stop(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        mIRenderControlManager.getAvTransportControl(instanceId).stop();
    }

}
