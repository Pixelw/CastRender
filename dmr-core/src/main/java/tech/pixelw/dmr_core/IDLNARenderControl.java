package tech.pixelw.dmr_core;

import org.fourthline.cling.support.model.TransportState;


/**
 *
 */
public interface IDLNARenderControl {
    int type();

    void prepare(String uri);

    void play();

    void pause();

    void seek(long position);

    void stop();

    long getPosition();

    long getDuration();

    TransportState getTransportState();

    void setMediaInfo(String metadata);

    float getSpeed();
}

