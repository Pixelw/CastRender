package tech.pixelw.castrender.utils;

import android.util.Log;

import tech.pixelw.castrender.BuildConfig;

/**
 * @author Carl Su
 * @date 2019/12/19
 */
public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final String NULL_LOG_MSG = "!(null log msg)";
    public static final String EMPTY_LOG_MSG = "!(empty log msg)";

    //set project logging level here
    public static int level = BuildConfig.DEBUG ? VERBOSE : INFO;


    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            msg = avoidNullMsg(msg);
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            msg = avoidNullMsg(msg);
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            msg = avoidNullMsg(msg);
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            msg = avoidNullMsg(msg);
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            msg = avoidNullMsg(msg);
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (level <= WARN) {
            msg = avoidNullMsg(msg);
            Log.w(tag, msg, throwable);
        }
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (level <= ERROR) {
            msg = avoidNullMsg(msg);
            Log.e(tag, msg, throwable);
        }
    }

    public static String avoidNullMsg(String msg){
        if (msg == null){
            return NULL_LOG_MSG;
        }
        if (msg.length() == 0){
            return EMPTY_LOG_MSG;
        }
        return msg;
    }
}
