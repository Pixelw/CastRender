package tech.pixelw.castrender.feature.main;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/4/28
 */
public class AppVersion {
    public static final String PLACEHOLDER_ABI = "_abi_";
    public static final String ABI_UNIVERSAL = "all";
    public static final String ABI_ARMV7 = "armeabi-v7a";
    public static final String ABI_ARMV8 = "arm64-v8a";
    public static final String ABI_X86 = "x86";

    String[] abis;

    String url;
    String[] alternativeApkUrl;

    public String[] getAlternativeApkUrl() {
        return alternativeApkUrl;
    }

    String[] storeUrls;

    public String[] getStoreUrls() {
        return storeUrls;
    }

    String versionName;
    String changeLog;
    int versionCode;

    int minSdk;
    int sizeInByte;

    public String[] getAbis() {
        return abis;
    }

    public String getUrl() {
        return url;
    }

    public int getMinSdk() {
        return minSdk;
    }

    public int getSizeInByte() {
        return sizeInByte;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getChangeLog() {
        return changeLog;
    }
}