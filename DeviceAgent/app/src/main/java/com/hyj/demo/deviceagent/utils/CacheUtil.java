package com.hyj.demo.deviceagent.utils;

import java.util.List;

/**
 * 缓存工具
 *
 * @author hyj
 */
public class CacheUtil {

    private static CacheUtil mCacheUtil;
    private static String mInnerHddPath;//内接设备地址
    private static List<String> mExtraHddPath;//外接设备地址
    private static boolean isSynchPlay; //同步播放

    private CacheUtil() {
        // cannot be instantiated
    }

    public static synchronized CacheUtil getInstance() {
        if (mCacheUtil == null) {
            mCacheUtil = new CacheUtil();
        }
        return mCacheUtil;
    }

    public static void releaseInstance() {
        if (mCacheUtil != null) {
            mCacheUtil = null;
        }
    }

    public String getInnerHddPath() {
        return mInnerHddPath;
    }

    public void setInnerHddPath(String mInnerHddPath) {
        CacheUtil.mInnerHddPath = mInnerHddPath;
    }

    public List<String> getExtraHddPath() {
        return mExtraHddPath;
    }

    public void setExtraHddPath(List<String> mExtraHddPath) {
        CacheUtil.mExtraHddPath = mExtraHddPath;
    }

    public boolean isSynchPlay() {
        return isSynchPlay;
    }

    public void setSynchPlay(boolean isSynchPlay) {
        CacheUtil.isSynchPlay = isSynchPlay;
    }
}
