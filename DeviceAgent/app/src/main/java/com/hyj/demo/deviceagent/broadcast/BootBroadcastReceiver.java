package com.hyj.demo.deviceagent.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyj.demo.deviceagent.utils.LogUtil;
import com.onex.system.SystemUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2019/1/15 9:28
 * @description :
 * =========================================================
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";

    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.getInstance().i(TAG, "Boot this system , BootBroadcastReceiver onReceive()");

        if (intent.getAction().equals(ACTION_BOOT)) {
            LogUtil.getInstance().i(TAG, "BootBroadcastReceiver onReceive(), Do thing!");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SystemUtils.EnableSATAPowerOn(true);
                }
            }, 3000);
        }
    }

}
