package tech.pixelw.dmr_core;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceSettings implements Parcelable {
    public final String name;
    public final String description;
    public final int versionCode;
    public final String modelUrl;
    public final String model;
    public final String manufacturer;

    public DeviceSettings(String name, String description, int versionCode, String modelUrl, String model,
                          String manufacturer) {
        this.name = name;
        this.description = description;
        this.versionCode = versionCode;
        this.modelUrl = modelUrl;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    protected DeviceSettings(Parcel in) {
        name = in.readString();
        description = in.readString();
        versionCode = in.readInt();
        modelUrl = in.readString();
        model = in.readString();
        manufacturer = in.readString();
    }

    public static final Creator<DeviceSettings> CREATOR = new Creator<DeviceSettings>() {
        @Override
        public DeviceSettings createFromParcel(Parcel in) {
            return new DeviceSettings(in);
        }

        @Override
        public DeviceSettings[] newArray(int size) {
            return new DeviceSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(versionCode);
        dest.writeString(modelUrl);
        dest.writeString(model);
        dest.writeString(manufacturer);
    }
}
