package tech.pixelw.dmr_core.service;

import android.content.Context;

import org.fourthline.cling.support.model.TransportState;

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
    public void prepare(String uri) {
        idlnaNewSession.newPlayer(context, uri);
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
        return null;
    }

    @Override
    public float getSpeed() {
        return 0;
    }
}
