package tech.pixelw.dmr_core.service;

import android.content.Context;

import androidx.annotation.Nullable;

import org.fourthline.cling.support.model.TransportState;

import tech.pixelw.cling_common.entity.MediaEntity;
import tech.pixelw.dmr_core.IDLNARenderControl;

/**
 * @author Carl Su "Pixelw"
 * @date 2022/1/22
 */
public class DefaultRenderControl implements IDLNARenderControl {
    public static IDLNANewSession idlnaNewSession;
    private final Context context;

    public DefaultRenderControl(Context context) {
        this.context = context;
    }

    @Override
    public int type() {
        return 0;
    }

    @Override
    public void prepare(@Nullable String uri, @Nullable MediaEntity entity) {
        idlnaNewSession.newPlayer(context, uri, entity);
    }

    @Override
    public void setRawMetadata(@Nullable String rawMetadata) {
    }

    @Override
    public void play() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void seek(long position) {
    }

    @Override
    public void stop() {
    }

    @Override
    public long getPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public TransportState getTransportState() {
        return TransportState.NO_MEDIA_PRESENT;
    }

    @Override
    public float getSpeed() {
        return 1.0f;
    }
}
