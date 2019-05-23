package com.hyj.demo.deviceagent.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.hyj.demo.deviceagent.base.BaseApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 操作挂载工具类
 */

public class StorageUtil {
    private static final String TAG = "StorageUtil";

    private static StorageUtil mStorageUtil;

    private StorageUtil() {
        // cannot be instantiated
    }

    public static synchronized StorageUtil getInstance() {
        if (mStorageUtil == null) {
            mStorageUtil = new StorageUtil();
        }
        return mStorageUtil;
    }

    public static void releaseInstance() {
        if (mStorageUtil != null) {
            mStorageUtil = null;
        }
    }

    public boolean hasUsbExtension() {
        boolean hasExtension = false;
        if (getUsbExtension()[0] != 0) {
            hasExtension = true;
        }
        return hasExtension;
    }

    /**
     * 获取外接内存的大小，单位字节.当没有查到时值为0
     *
     * @return 数组Data，Data[0] totalSize；Data[1] availableSize；
     */
    public long[] getUsbExtension() {
        long[] usbData = new long[2];
        StorageManager storageManager = (StorageManager) BaseApplication.getInstance()
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            long totalSize = 0;
            long availableSize = 0;
            for (int i = 0; i < ((String[]) invoke).length; i++) {
                String path = ((String[]) invoke)[i];
                LogUtil.getInstance().i(TAG, "mPath----> " + path);
                int j = path.lastIndexOf("/");
                int length = path.length();
                if ((length - j) > 3) {
                    String s = path.substring(j + 1, j + 4);
                    LogUtil.getInstance().i(TAG, "s " + s);
                    if (TextUtils.equals(s, "sda")) {
                        long[] sdcardSize = getSdcardSize(path);
                        totalSize += sdcardSize[0];
                        availableSize += sdcardSize[1];
                    }
                }
            }
            usbData[0] = totalSize;
            usbData[1] = availableSize;
            return usbData;

        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        return usbData;
    }

    /**
     * 获取SD卡,u盘的大小信息
     *
     * @param path sd卡或usb路径
     * @return 数组Data，Data[0] totalSize  总空间大小；Data[1] availableSize 可用空间大小；
     */
    public long[] getSdcardSize(String path) {
        long[] uData = new long[2];
        StatFs stat = new StatFs(path);
        long availableBytes = stat.getAvailableBytes();
        LogUtil.getInstance().i(TAG, "availableBytes :" + (availableBytes / 1024 / 1024) + "MB");
        long totalBytes = stat.getTotalBytes();
        LogUtil.getInstance().i(TAG, "totalBytes :" + (totalBytes / 1024 / 1024) + "MB");
        uData[0] = totalBytes;
        uData[1] = availableBytes;
        return uData;
    }

    /**
     * 根据路径判断 是否挂载
     *
     * @param mountPoint
     * @return
     */
    public boolean checkMounted(String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) BaseApplication.getInstance()
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod(
                    "getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager,
                    mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取所有存储点
     *
     * @return
     */
    public String[] getExtSDCardPath() {
        StorageManager storageManager = (StorageManager) BaseApplication.getInstance().getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[]) invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
