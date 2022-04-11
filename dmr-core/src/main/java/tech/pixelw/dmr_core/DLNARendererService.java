package tech.pixelw.dmr_core;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

import tech.pixelw.cling_common.SharedUpnpService;
import tech.pixelw.cling_common.UpnpAttach;
import tech.pixelw.dmr_core.service.AVTransportServiceImpl;
import tech.pixelw.dmr_core.service.AudioRenderServiceImpl;
import tech.pixelw.dmr_core.service.ConnectionManagerServiceImpl;
import tech.pixelw.dmr_core.service.RenderControlManager;

/**
 * DMR
 */
public class DLNARendererService extends UpnpAttach {

    private static final String TAG = "DLNARendererService";

    private RenderControlManager mRenderControlManager;
    private LastChange mAvTransportLastChange;
    private LastChange mAudioControlLastChange;
    private LocalDevice mRendererDevice;
    private UpnpService upnpService;
    private final DeviceSettings deviceSettings;
    private final Notification notification;

    public DLNARendererService(DeviceSettings deviceSettings, Notification notification) {
        this.deviceSettings = deviceSettings;
        this.notification = notification;
    }

    @Override
    public void onBinderAttached(@NonNull SharedUpnpService.SharedBinder binder) {
        upnpService = binder.getService();
        mRenderControlManager = new RenderControlManager(getContext());
        try {
            mRendererDevice = createRendererDevice(deviceSettings, getContext());
            upnpService.getRegistry().addDevice(mRendererDevice);
            binder.serveForeground(notification);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(@NonNull Context context) {
        if (upnpService != null) {
            upnpService.getRegistry().removeDevice(mRendererDevice);
        }
        super.stop(context);
    }

    public LastChange getAudioControlLastChange() {
        return mAudioControlLastChange;
    }


    private static final String ID_SALT = "CastRender by Pixelw";

    protected LocalDevice createRendererDevice(DeviceSettings deviceSettings, Context context) throws ValidationException, IOException {
        DeviceIdentity deviceIdentity = new DeviceIdentity(
                createUniqueSystemIdentifier(ID_SALT, Utils.getWifiIpAddress(context)));
        UDADeviceType deviceType = new UDADeviceType("MediaRenderer", deviceSettings.versionCode);
        DeviceDetails details = new DeviceDetails(deviceSettings.name,
                new ManufacturerDetails(android.os.Build.MANUFACTURER),
                new ModelDetails(android.os.Build.MODEL, deviceSettings.description,
                        "v" + deviceSettings.versionCode, deviceSettings.modelUrl));
        Icon[] icons = null;
        BitmapDrawable drawable = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.tv));
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());
            icons = new Icon[]{new Icon("image/png", 48, 48, 8, "icon.png", byteArrayInputStream)};
        }
        return new LocalDevice(deviceIdentity, deviceType, details, icons, generateLocalServices(context));
    }

    @SuppressWarnings("unchecked")
    protected LocalService<?>[] generateLocalServices(final Context context) {
        // connection
        LocalService<ConnectionManagerServiceImpl> connectionManagerService
                = new AnnotationLocalServiceBinder().read(ConnectionManagerServiceImpl.class);
        connectionManagerService.setManager(new DefaultServiceManager<ConnectionManagerServiceImpl>(
                connectionManagerService, ConnectionManagerServiceImpl.class) {
            @Override
            protected ConnectionManagerServiceImpl createServiceInstance() {
                return new ConnectionManagerServiceImpl(context);
            }
        });

        // av transport service
        mAvTransportLastChange = new LastChange(new AVTransportLastChangeParser());
        LocalService<AVTransportServiceImpl> avTransportService =
                new AnnotationLocalServiceBinder().read(AVTransportServiceImpl.class);
        avTransportService.setManager(new LastChangeAwareServiceManager<AVTransportServiceImpl>
                (avTransportService, new AVTransportLastChangeParser()) {
            @Override
            protected AVTransportServiceImpl createServiceInstance() {
                return new AVTransportServiceImpl(mAvTransportLastChange, mRenderControlManager);
            }
        });

        // render service
        mAudioControlLastChange = new LastChange(new RenderingControlLastChangeParser());
        LocalService<AudioRenderServiceImpl> renderingControlService =
                new AnnotationLocalServiceBinder().read(AudioRenderServiceImpl.class);
        renderingControlService.setManager(new LastChangeAwareServiceManager<AudioRenderServiceImpl>(
                renderingControlService, new RenderingControlLastChangeParser()) {
            @Override
            protected AudioRenderServiceImpl createServiceInstance() {
                return new AudioRenderServiceImpl(mAudioControlLastChange, mRenderControlManager);
            }
        });

        return new LocalService[]{connectionManagerService, avTransportService, renderingControlService};
    }

    private static UDN createUniqueSystemIdentifier(@SuppressWarnings("SameParameterValue") String salt, String ipAddress) {
        StringBuilder builder = new StringBuilder();
        builder.append(ipAddress);
        builder.append(android.os.Build.MODEL);
        builder.append(android.os.Build.MANUFACTURER);
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(builder.toString().getBytes());
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception ex) {
            return new UDN(ex.getMessage() != null ? ex.getMessage() : "UNKNOWN");
        }
    }

    /**
     * view calls
     */

    public LastChange avTransportLastChange() {
        return mAvTransportLastChange;
    }

    public void notifyTransportStateChanged(TransportState state) {
        Log.d(TAG, "notifyTransportStateChanged() called with: state = [" + state + "]");
        mAvTransportLastChange.setEventedValue(
                getInstanceId(),
                new AVTransportVariable.TransportState(state)
        );
    }

    private void notifyRenderVolumeChanged(int volume) {
        Log.d(TAG, "notifyRenderVolumeChanged() called with: volume = [" + volume + "]");
        audioControlLastChange().
                setEventedValue(getInstanceId(),
                        new RenderingControlVariable.Volume(
                                new ChannelVolume(Channel.Master, volume)));
    }

    public void registerController(IDLNARenderControl control) {
        if (control == null) {
            return;
        }
        mRenderControlManager.attachPlayerControl(control);
    }

    public void unregisterController() {
        mRenderControlManager.detachPlayerControl();
    }

    public LastChange audioControlLastChange() {
        return getAudioControlLastChange();
    }

    public UnsignedIntegerFourBytes getInstanceId() {
        return new UnsignedIntegerFourBytes(0);
    }

    public void hello() {
        Log.w(TAG, "hello");
    }
}
