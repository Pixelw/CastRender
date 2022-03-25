package tech.pixelw.dmr_core.service;


import android.content.Context;
import android.util.Log;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;

import java.net.URI;

import tech.pixelw.dmr_core.IDLNARenderControl;
import tech.pixelw.dmr_core.Utils;

/**
 * 实现传输到这里控制的各种行为
 */
public class AVTransportController implements IRendererInterface.IAVTransportControl {
    private static final TransportAction[] TRANSPORT_ACTION_STOPPED = new TransportAction[]{TransportAction.Play};
    private static final TransportAction[] TRANSPORT_ACTION_PLAYING = new TransportAction[]{TransportAction.Stop, TransportAction.Pause, TransportAction.Seek};
    private static final TransportAction[] TRANSPORT_ACTION_PAUSE_PLAYBACK = new TransportAction[]{TransportAction.Play, TransportAction.Seek, TransportAction.Stop};

    private final UnsignedIntegerFourBytes mInstanceId;
    private TransportInfo mTransportInfo;
    private final TransportSettings mTransportSettings = new TransportSettings(); // default no changed
    private PositionInfo mOriginPositionInfo;
    private MediaInfo mMediaInfo;
    private final IDLNARenderControl defaultMediaControl;
    private IDLNARenderControl mMediaControl;
    private static final String TAG = "AVTransportController";

    public AVTransportController(Context context) {
        this(context, new UnsignedIntegerFourBytes(0));
    }

    public AVTransportController(Context context, UnsignedIntegerFourBytes instanceId) {
        Context mApplicationContext = context.getApplicationContext();
        mInstanceId = instanceId;
        defaultMediaControl = new DefaultRenderControl(mApplicationContext);
        mMediaControl = defaultMediaControl;
        // 初始化默认值
        initStatusObjects();
    }

    private void initStatusObjects() {
        mTransportInfo = new TransportInfo();
        mOriginPositionInfo = new PositionInfo();
        mMediaInfo = new MediaInfo();
    }

    public void setMediaControl(IDLNARenderControl control) {
        if (control != null) {
            mMediaControl = control;
        } else {
            mMediaControl = defaultMediaControl;
            initStatusObjects();
        }
    }

    public UnsignedIntegerFourBytes getInstanceId() {
        return mInstanceId;
    }

    public synchronized TransportAction[] getCurrentTransportActions() {
        switch (mTransportInfo.getCurrentTransportState()) {
            case PLAYING:
                return TRANSPORT_ACTION_PLAYING;
            case PAUSED_PLAYBACK:
                return TRANSPORT_ACTION_PAUSE_PLAYBACK;
            default:
                return TRANSPORT_ACTION_STOPPED;
        }
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities() {
        return new DeviceCapabilities(new StorageMedium[]{StorageMedium.NETWORK});
    }

    // 三个主要的状态返回
    @Override
    public MediaInfo getMediaInfo() {
        return mMediaInfo;
    }

    @Override
    public PositionInfo getPositionInfo() {
        if (mMediaControl.type() > 0) {
            return new PositionInfo(mOriginPositionInfo.getTrack().getValue(),
                    ModelUtil.toTimeString(mMediaControl.getDuration() / 1000),
                    mOriginPositionInfo.getTrackURI(),
                    ModelUtil.toTimeString(mMediaControl.getPosition() / 1000),
                    ModelUtil.toTimeString(mMediaControl.getPosition() / 1000));
        } else {
            return mOriginPositionInfo;
        }
    }

    @Override
    public TransportInfo getTransportInfo() {
        if (mMediaControl.type() > 0) {
            mTransportInfo = new TransportInfo(mMediaControl.getTransportState(), "1");
        }
        return mTransportInfo;

    }

    @Override
    public TransportSettings getTransportSettings() {
        return mTransportSettings;
    }

    @Override
    public void setAVTransportURI(String currentURI, String currentURIMetaData) throws AVTransportException {
        try {
            new URI(currentURI);
        } catch (Exception ex) {
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "CurrentURI can not be null or malformed");
        }
        Log.i(TAG, "got uri: " + currentURI);
        Log.i(TAG, "got meta: " + currentURIMetaData);
        mMediaInfo = new MediaInfo(currentURI, currentURIMetaData, new UnsignedIntegerFourBytes(1), "00:00:00", StorageMedium.NETWORK);
        mOriginPositionInfo = new PositionInfo(1, currentURIMetaData, currentURI);
        mMediaControl.setMediaInfo(currentURIMetaData);
        mMediaControl.prepare(currentURI);
    }

    @Override
    public void setNextAVTransportURI(String nextURI, String nextURIMetaData) {
    }

    @Override
    public void play(String speed) {
        mMediaControl.play();
    }

    public void pause() {
        mMediaControl.pause();
    }

    @Override
    public void seek(String unit, String target) throws AVTransportException {
        SeekMode seekMode = SeekMode.valueOrExceptionOf(unit);
        if (!seekMode.equals(SeekMode.REL_TIME)) {
            throw new AVTransportException(AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: " + unit);
        }
        long position = Utils.getIntTime(target);
        mMediaControl.seek(position);
    }

    public void stop() {
        mMediaControl.stop();
    }

    @Override
    public void previous() {
    }

    @Override
    public void next() {
    }

    @Override
    public void record() {
    }

    @Override
    public void setPlayMode(String newPlayMode) {
    }

    @Override
    public void setRecordQualityMode(String newRecordQualityMode) {
    }

}
