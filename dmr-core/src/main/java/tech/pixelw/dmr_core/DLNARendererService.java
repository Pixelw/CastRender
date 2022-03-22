package tech.pixelw.dmr_core;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.core.content.ContextCompat;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
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
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

import tech.pixelw.dmr_core.service.AVTransportServiceImpl;
import tech.pixelw.dmr_core.service.AudioRenderServiceImpl;
import tech.pixelw.dmr_core.service.ConnectionManagerServiceImpl;
import tech.pixelw.dmr_core.service.RenderControlManager;

/**
 * DMR
 */
public class DLNARendererService extends AndroidUpnpServiceImpl {

    public static final String KEY_DEVICE_SETTINGS = "dmr.service.device.settings";
    public static final String KEY_NOTIFICATION = "dmr.service.notification";
    public static final int NOTIFIID = 220322;

    private boolean serviceRunning = false;

    private RenderControlManager mRenderControlManager;

    private LastChange mAvTransportLastChange;
    private LastChange mAudioControlLastChange;
    private final RendererServiceBinder mBinder = new RendererServiceBinder();
    private LocalDevice mRendererDevice;

    public static void startService(Context context, DeviceSettings deviceSettings, Notification notification) {
        Intent intent = new Intent(context, DLNARendererService.class);
        intent.putExtra(KEY_DEVICE_SETTINGS, deviceSettings);
        intent.putExtra(KEY_NOTIFICATION, notification);
        context.startService(intent);
    }

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            @Override
            public int getAliveIntervalMillis() {
                return 5 * 1000;
            }
        };
    }

    @Override
    public void onCreate() {
        org.seamless.util.logging.LoggingUtil.resetRootHandler(new FixedAndroidLogHandler());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !serviceRunning) {
            DeviceSettings deviceSettings = intent.getExtras().getParcelable(KEY_DEVICE_SETTINGS);
            if (deviceSettings != null) {
                try {
                    mRenderControlManager = new RenderControlManager(getApplicationContext());
                    mRendererDevice = createRendererDevice(deviceSettings);
                    upnpService.getRegistry().addDevice(mRendererDevice);
                    serviceRunning = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }
            }
            Parcelable parcelable = intent.getExtras().getParcelable(KEY_NOTIFICATION);
            if (parcelable instanceof Notification) {
                startForeground(NOTIFIID, (Notification) parcelable);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        if (mRendererDevice != null && upnpService != null && upnpService.getRegistry() != null) {
            upnpService.getRegistry().removeDevice(mRendererDevice);
        }
        super.onDestroy();
    }

    public LastChange getAvTransportLastChange() {
        return mAvTransportLastChange;
    }

    public LastChange getAudioControlLastChange() {
        return mAudioControlLastChange;
    }


    // TODO: 2022/1/25 custom model desc outside
    private static final String ID_SALT = "CastRender by Pixelw";

    protected LocalDevice createRendererDevice(DeviceSettings deviceSettings) throws ValidationException, IOException {
        DeviceIdentity deviceIdentity = new DeviceIdentity(
                createUniqueSystemIdentifier(ID_SALT, Utils.getWifiIpAddress(this)));
        UDADeviceType deviceType = new UDADeviceType("MediaRenderer", deviceSettings.versionCode);
        DeviceDetails details = new DeviceDetails(deviceSettings.name,
                new ManufacturerDetails(android.os.Build.MANUFACTURER),
                new ModelDetails(android.os.Build.MODEL, deviceSettings.description,
                        "v" + deviceSettings.versionCode, deviceSettings.modelUrl));
        Icon[] icons = null;
        BitmapDrawable drawable = ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.tv));
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());
            icons = new Icon[]{new Icon("image/png", 48, 48, 8, "icon.png", byteArrayInputStream)};
        }
        return new LocalDevice(deviceIdentity, deviceType, details, icons, generateLocalServices(this));
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

    // -------------------------------------------------------------------------------------------
    // - Binder
    // -------------------------------------------------------------------------------------------
    public class RendererServiceBinder extends Binder {
        @Deprecated
        public DLNARendererService getRendererService() {
            return DLNARendererService.this;
        }

        public LastChange avTransportLastChange() {
            return getAvTransportLastChange();
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
    }
}
