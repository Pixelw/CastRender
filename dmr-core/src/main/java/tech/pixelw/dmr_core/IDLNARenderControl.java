package tech.pixelw.dmr_core;

import org.fourthline.cling.support.model.TransportState;

import tech.pixelw.cling_common.entity.MediaEntity;


/**
 *
 */
public interface IDLNARenderControl {
    int type();

    void prepare(String uri, MediaEntity entity);

    void setRawMetadata(String rawMetadata);

    void play();

    void pause();

    void seek(long position);

    void stop();

    long getPosition();

    long getDuration();

    TransportState getTransportState();

    float getSpeed();
}

