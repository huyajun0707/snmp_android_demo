package com.hyj.demo.deviceagent.utils;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/7/12 14:33
 * @description :   Log工具
 * =========================================================
 */
public class LogUtil {

    private static LogUtil mInstance;

    private boolean isDebug;

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    private LogUtil() {
    }

    public static synchronized LogUtil getInstance() {
        if (mInstance == null) {
            mInstance = new LogUtil();
        }
        return mInstance;
    }

    public static void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public void v(String tag, String msg) {
        if (isDebug)
            android.util.Log.v(tag, "----->" + msg);
    }

    public void v(String tag, String msg, Throwable t) {
        if (isDebug)
            android.util.Log.v(tag, msg, t);
    }

    public void d(String tag, String msg) {
        if (isDebug)
            android.util.Log.d(tag, "----->" + msg);
    }

    public void d(String tag, String msg, Throwable t) {
        if (isDebug)
            android.util.Log.d(tag, msg, t);
    }

    public void i(String tag, String msg) {
        if (isDebug)
            android.util.Log.i(tag, "----->" + msg);
    }

    public void i(String tag, String msg, Throwable t) {
        if (isDebug)
            android.util.Log.i(tag, msg, t);
    }

    public void w(String tag, String msg) {
        if (isDebug)
            android.util.Log.w(tag, msg);
    }

    public void w(String tag, String msg, Throwable t) {
        if (isDebug)
            android.util.Log.w(tag, msg, t);
    }

    public void e(String tag, String msg) {
        if (isDebug)
            android.util.Log.e(tag, "----->" + msg);
    }

    public void e(String tag, String msg, Throwable t) {
        if (isDebug)
            android.util.Log.e(tag, msg, t);
    }

    public void print(Object object) {
        if (isDebug) {
            if (object != null) {
                System.out.println("----->" + object.toString());
            }
        }
    }
}
