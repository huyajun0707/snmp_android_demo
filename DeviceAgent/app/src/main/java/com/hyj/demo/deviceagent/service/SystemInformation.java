package com.hyj.demo.deviceagent.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;


import com.hyj.demo.deviceagent.BuildConfig;
import com.hyj.demo.deviceagent.R;
import com.hyj.demo.deviceagent.broadcast.MountReceiver;
import com.hyj.demo.deviceagent.constant.EnumNetWorkType;
import com.hyj.demo.deviceagent.constant.MIBtree;
import com.hyj.demo.deviceagent.entity.NetWorkType;
import com.hyj.demo.deviceagent.utils.CacheUtil;
import com.hyj.demo.deviceagent.utils.IpUtil;
import com.hyj.demo.deviceagent.utils.LogUtil;
import com.hyj.demo.deviceagent.utils.StorageUtil;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;


public class SystemInformation {

    private Context context;
    private static SystemInformation mSystemInformation;

    private SystemInformation(Context context) {
        // cannot be instantiated
        this.context = context;
    }

    public static synchronized SystemInformation getInstance(Context context) {
        if (mSystemInformation == null) {
            mSystemInformation = new SystemInformation(context);
        }
        return mSystemInformation;
    }

    public static void releaseInstance() {
        if (mSystemInformation != null) {
            mSystemInformation = null;
        }
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    private VariableBinding getDeviceModeNumber() {
        String deviceModeNumber = "1+X播放机";
        OID oid = (OID) MIBtree.DEVICE_MODEL_NUMBER_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new OctetString(deviceModeNumber));
        LogUtil.getInstance().print("getDeviceModeNumber----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    private VariableBinding getDeviceSerialNumber() {
        String deviceSerialNumber = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        OID oid = (OID) MIBtree.DEVICE_SERIAL_NUMBER_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new OctetString(deviceSerialNumber));
        LogUtil.getInstance().print("getDeviceSerialNumber----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取运行时间 (从开机到现在的毫秒数)
     *
     * @return
     */
    private VariableBinding getSystemUptimeMills() {
        String deviceModeNumber = String.valueOf(SystemClock.uptimeMillis());
        OID oid = (OID) MIBtree.SYS_UPTIME_MILLS_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new OctetString(deviceModeNumber));
        LogUtil.getInstance().print("getSystemUptimeMills----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取制造商
     *
     * @return
     */
    private VariableBinding getDeviceManufacturer() {
        String data = "";
        OID oid = (OID) MIBtree.DEVICE_MANUFACTURER_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new OctetString(data));
        LogUtil.getInstance().print("getDeviceManufacturer----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取固件版本
     *
     * @return
     */
    private VariableBinding getFirmwareVersion() {
        int data = 1;
        OID oid = (OID) MIBtree.FIRMWARE_VERSION_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32(data));
        LogUtil.getInstance().print("getFirmwareVersion----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取软件版本
     *
     * @return
     */
    private VariableBinding getSoftwareVersion() {
        int data = BuildConfig.VERSION_CODE;
        OID oid = (OID) MIBtree.SOFTWARE_VERSION_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32(data));
        LogUtil.getInstance().print("getSoftwareVersion----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取网络状态
     *
     * @return
     */
    private VariableBinding getNetworkState() {
        int status = 0;
        NetWorkType netWorkType = IpUtil.getNetWorkType();
        if (netWorkType != null) {
            if (netWorkType.getType() == EnumNetWorkType.CONN_4G) {
                status = 1;
            } else if (netWorkType.getType() == EnumNetWorkType.CONN_WIFI) {
                status = 2;
            } else if (netWorkType.getType() == EnumNetWorkType.CONN_WIRE) {
                status = 3;
            } else if (netWorkType.getType() != EnumNetWorkType.CONN_NO) {
                status = 0;
            }
        }
        OID oid = (OID) MIBtree.NETWORK_STATE.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32(status));
        LogUtil.getInstance().print("getNetworkState----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取硬盘状态
     *
     * @return
     */
    private VariableBinding getHardDiskState(Context context) {
        MountReceiver mountReceiver = new MountReceiver();
        context.registerReceiver(mountReceiver, MountReceiver.getIntentFilter());
        boolean isHasInnerHdd = !TextUtils.isEmpty(CacheUtil.getInstance().getInnerHddPath());
        OID oid = (OID) MIBtree.HARD_DISK_STATE_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32(isHasInnerHdd ? 1 : 0));
        context.unregisterReceiver(mountReceiver);
        LogUtil.getInstance().print("getHardDiskState----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取硬盘可用空间
     *
     * @return
     */
    private VariableBinding getHardDiskAvailableSpace(Context context) {
        MountReceiver mountReceiver = new MountReceiver();
        context.registerReceiver(mountReceiver, MountReceiver.getIntentFilter());
        boolean isHasInnerHdd = !TextUtils.isEmpty(CacheUtil.getInstance().getInnerHddPath());
        VariableBinding vb;
        OID oid = (OID) MIBtree.HARD_DISK_AVAILABLE_SPACE_OID.clone();
        if (isHasInnerHdd) {
            long[] size = StorageUtil.getInstance().getSdcardSize(CacheUtil.getInstance().getInnerHddPath());
            int space = (int) (size[0] / 1024 / 1024 / 1024);
            vb = new VariableBinding(oid.append(0), new Integer32(space));
        } else {
            vb = new VariableBinding(oid.append(0), new Integer32(0));
        }
        context.unregisterReceiver(mountReceiver);
        LogUtil.getInstance().print("getHardDiskAvailableSpace----");
        return vb;
    }

    /**
     * 获取cpu温度
     *
     * @return
     */
    private VariableBinding getCpuTemperature(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        double temperature = batteryStatus.getIntExtra("temperature", 0) / 10.0;  //电池温度(数值)
        OID oid = (OID) MIBtree.CPU_TEMPERATURE_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32((int) temperature));
        LogUtil.getInstance().print("getCpuTemperature----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取电压
     *
     * @return
     */
    private VariableBinding getVoltage() {
        String data = "";
        OID oid = (OID) MIBtree.DEVICE_VOLTAGE_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new OctetString(data));
        LogUtil.getInstance().print("getVoltage----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取静音状态
     *
     * @return
     */
    private VariableBinding getMuteState(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        OID oid = (OID) MIBtree.MUTE_STATE_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32(current == 0 ? 1 : 0));
        LogUtil.getInstance().print("getMuteState----" + vb.toValueString());
        return vb;
    }

    /**
     * 获取电源模式battery_status
     *
     * @return
     */
    private VariableBinding getBatteryStatus(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        OID oid = (OID) MIBtree.BATTERY_STATUS_OID.clone();
        VariableBinding vb = new VariableBinding(oid.append(0), new Integer32(isCharging ? 1 : 0));
        LogUtil.getInstance().print("getBatteryStatus----" + vb.toValueString());
        return vb;
    }


    public VariableBinding getVariableBinding(OID oid) {
        if (oid.equals(new OID(MIBtree.DEVICE_MODEL_NUMBER_OID))) {
            return getDeviceModeNumber();
        } else if (oid.equals(new OID(MIBtree.SYS_UPTIME_MILLS_OID))) {
            return getSystemUptimeMills();
        } else if (oid.equals(new OID(MIBtree.DEVICE_SERIAL_NUMBER_OID))) {
            return getDeviceSerialNumber();
        } else if (oid.equals(new OID(MIBtree.DEVICE_MANUFACTURER_OID))) {
            return getDeviceManufacturer();
        } else if (oid.equals(new OID(MIBtree.FIRMWARE_VERSION_OID))) {
            return getFirmwareVersion();
        } else if (oid.equals(new OID(MIBtree.SOFTWARE_VERSION_OID))) {
            return getSoftwareVersion();
        } else if (oid.equals(new OID(MIBtree.NETWORK_STATE))) {
            return getNetworkState();
        } else if (oid.equals(new OID(MIBtree.DEVICE_VOLTAGE_OID))) {
            return getVoltage();
        } else if (oid.equals(new OID(MIBtree.BATTERY_STATUS_OID))) {
            return getBatteryStatus(context);
        } else if (oid.equals(new OID(MIBtree.CPU_TEMPERATURE_OID))) {
            return getCpuTemperature(context);
        } else if (oid.equals(new OID(MIBtree.MUTE_STATE_OID))) {
            return getMuteState(context);
        } else if (oid.equals(new OID(MIBtree.HARD_DISK_STATE_OID))) {
            return getHardDiskState(context);
        } else if (oid.equals(new OID(MIBtree.HARD_DISK_AVAILABLE_SPACE_OID))) {
            return getHardDiskAvailableSpace(context);
        }
        return null;
    }

}
