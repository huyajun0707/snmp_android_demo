package com.hyj.demo.deviceagent.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;


import com.hyj.demo.deviceagent.utils.CacheUtil;
import com.hyj.demo.deviceagent.utils.CustomToastUtil;
import com.hyj.demo.deviceagent.utils.LogUtil;
import com.hyj.demo.deviceagent.utils.MountInfo;

import java.util.List;

public class MountReceiver extends BroadcastReceiver {
    private static final String TAG = "MountReceiver";
    private static IntentFilter intentFilter;



    public static IntentFilter getIntentFilter() {
        if (intentFilter == null) {
            intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            intentFilter.addDataScheme("file");
        }
        return intentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MountInfo mountInfo;
        switch (intent.getAction()) {
            case Intent.ACTION_MEDIA_MOUNTED:
                CustomToastUtil.getInstance().showToast(context, "设备已挂载", Toast.LENGTH_SHORT);
                mountInfo = new MountInfo();
                CacheUtil.getInstance().setInnerHddPath(mountInfo.getInnerHddPath());
                LogUtil.getInstance().i(TAG, "onReceive: " + mountInfo.getInnerHddPath());
                List<String> extraHddPath = mountInfo.getExtraHddPath();
                CacheUtil.getInstance().setExtraHddPath(extraHddPath);
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED:
                CustomToastUtil.getInstance().showToast(context, "设备已卸载", Toast.LENGTH_SHORT);
                mountInfo = new MountInfo();
                CacheUtil.getInstance().setInnerHddPath(mountInfo.getInnerHddPath());
                LogUtil.getInstance().i(TAG, "onReceive: " + mountInfo.getInnerHddPath());
                CacheUtil.getInstance().setExtraHddPath(mountInfo.getExtraHddPath());
                break;
            default:
                break;
        }

    }
}