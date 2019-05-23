package com.hyj.demo.deviceagent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


import com.hyj.demo.deviceagent.broadcast.MountReceiver;
import com.hyj.demo.deviceagent.constant.Constant;
import com.hyj.demo.deviceagent.constant.MIBtree;
import com.hyj.demo.deviceagent.utils.CacheUtil;
import com.hyj.demo.deviceagent.utils.LogUtil;
import com.hyj.demo.deviceagent.utils.StorageUtil;
import com.hyj.demo.deviceagent.utils.ThreadPoolUtil;

import org.snmp4j.AbstractTarget;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class AgentService extends Service implements CommandResponder {
    public MountReceiver mountReceiver;
    public Intent batteryStatus;
    /**
     * Keeps track of all current registered clients.
     */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    /**
     * Holds last value set by a client.
     */
    int mValue = 0;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_VALUE = 3;
    public static final int MSG_SNMP_REQUEST_RECEIVED = 4;
    public static final int MSN_SEND_DANGER_TRAP = 5;
    public static final int MSG_MANAGER_MESSAGE_RECEIVED = 6;

    public static String lastRequestReceived = "";

    private static AbstractTarget comtarget;
    private Snmp snmp;
    private static final String SNMP_PORT = "32150";

    private static ArrayList<Address> registeredManagers = null;

    //private MIBtree MIB_MAP;
    private Timer timer;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    sendMessageToClients(MSG_SET_VALUE);
                    break;
                case MSN_SEND_DANGER_TRAP:
//                    new SendTrap().execute();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendMessageToClients(int msgCode) {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                mClients.get(i).send(Message.obtain(null,
                        msgCode, 0, 0));
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new RefreshTrapData(), 0, 50000);
        new AgentListener().start();
    }

    /**
     * 轮询去检测硬件信息
     */
    public class RefreshTrapData extends TimerTask {
        @Override
        public void run() {
            if (mountReceiver == null) {
                mountReceiver = new MountReceiver();
            }
            //空间不足检测
            boolean isHasInnerHdd = !TextUtils.isEmpty(CacheUtil.getInstance().getInnerHddPath());
            if (isHasInnerHdd) {
                long[] size = StorageUtil.getInstance().getSdcardSize(CacheUtil.getInstance().getInnerHddPath());
                int space = (int) (size[0] / 1024 / 1024 / 1024);
                final VariableBinding spaceVB = new VariableBinding(MIBtree.HARD_DISK_AVAILABLE_SPACE_OVER_OID, new OctetString("硬盘存储空间不足"));
                if (space < Constant.MIN_SIZE) {
                    //如果空间小于最小存储空间，发出警告
                    ThreadPoolUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            SnmpManager.getInstance().sendTrap(spaceVB);
                        }
                    });
                }
            } else {
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        //如果硬盘未被加载，发出警告
                        LogUtil.getInstance().print("硬盘未挂载");
                        VariableBinding hardDiskVB = new VariableBinding(MIBtree.HARD_DISK_AVAILABLE_SPACE_OVER_OID, new OctetString("硬盘未挂载"));
                        SnmpManager.getInstance().sendTrap(hardDiskVB);
                    }
                });

            }

            //温度过高检测
            if (batteryStatus == null) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                batteryStatus = AgentService.this.registerReceiver(null, ifilter);
            }
            switch (batteryStatus.getIntExtra("health",
                    BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    LogUtil.getInstance().print("状态良好");
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    //电池过热，发出警告
                    ThreadPoolUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            VariableBinding variableBinding = new VariableBinding(MIBtree.BATTERY_HEALTH_OVERHEAT_OID, new OctetString("温度过高"));
                            SnmpManager.getInstance().sendTrap(variableBinding);
                        }
                    });
                    break;
            }

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {

    }

//    private class SendTrap extends AsyncTask<Void, Void, Void> {
//        protected Void doInBackground(Void... params) {
//            SnmpManager.getInstance().sendTrap(null);
//            return null;
//        }
//    }

    private class AgentListener extends Thread {
        public void run() {
            SnmpManager.getInstance().init(AgentService.this);
            SnmpManager.getInstance().addCommandResponder(AgentService.this);
            addSafeUser();
        }
    }


    @Override
    public synchronized void processPdu(CommandResponderEvent commandResponderEvent) {
        LogUtil.getInstance().print("processPdu");
        PDU command = (PDU) commandResponderEvent.getPDU().clone();
        SnmpManager.getInstance().registerManager(commandResponderEvent.getPeerAddress());
        if (command != null) {
            lastRequestReceived = command.toString() + " " + commandResponderEvent.getPeerAddress()+"\n";
            sendMessageToClients(MSG_SNMP_REQUEST_RECEIVED);
            if (command.getType() == PDU.GET) {
                SnmpManager.getInstance().handleGetRequest(command);
            }
            SnmpManager.getInstance().sendV3Response(commandResponderEvent, command);
        }
    }


    /**
     * 添加安全用 户
     */
    private void addSafeUser() {
        UsmUser user1 = new UsmUser(Constant.NO_AUTH,
                null, null,
                null, null);

        UsmUser user2 = new UsmUser(Constant.AUTH,
                Constant.AUTH_PROTOCOL, Constant.AUTH_PASS,
                null, null);

        UsmUser user3 = new UsmUser(Constant.PRIV,
                Constant.AUTH_PROTOCOL, Constant.AUTH_PASS,
                Constant.PRIV_PROTOCOL, Constant.PRIV_PASS);
        SnmpManager.getInstance().addUser(user1);
        SnmpManager.getInstance().addUser(user2);
        SnmpManager.getInstance().addUser(user3);
    }


}
