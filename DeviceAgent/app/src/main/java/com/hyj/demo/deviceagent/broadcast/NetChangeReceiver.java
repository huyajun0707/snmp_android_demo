package com.hyj.demo.deviceagent.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NetChangeReceiver extends BroadcastReceiver {
    public static final String NET_CHANGE = "net_change";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
//        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            EventBus.getDefault().post(NET_CHANGE);
//        }
    }
}
