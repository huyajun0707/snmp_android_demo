package com.hyj.demo.snmpdemo;

import org.snmp4j.smi.OID;


public class MIBtree {
    //设备型号
    public static final OID DEVICE_MODEL_NUMBER_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 1});
    //运行时间
    public static final OID SYS_UPTIME_MILLS_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 2});
    //序列号serial number
    public static final OID DEVICE_SERIAL_NUMBER_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 3});
    //制造商
    public static final OID DEVICE_MANUFACTURER_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 4});
    //固件版本Firmware version
    public static final OID FIRMWARE_VERSION_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 5});
    //软件版本Software version
    public static final OID SOFTWARE_VERSION_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 6});
    //网络状态Network state
    public static final OID NETWORK_STATE = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 7});
    //硬盘状态Hard disk state
    public static final OID HARD_DISK_STATE_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 8});
    //硬盘可用空间 Hard disk available space
    public static final OID HARD_DISK_AVAILABLE_SPACE_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 9});
    //CPU温度temperature
    public static final OID CPU_TEMPERATURE_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 10});
    //电压 Voltage
    public static final OID DEVICE_VOLTAGE_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 11});
    //静音状态 Mute state
    public static final OID MUTE_STATE_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 12});
    //电源模式 Mute state
    public static final OID BATTERY_STATUS_OID = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 13});
    public static final OID TEST = new OID(new int[]{1, 3, 6, 1, 6, 3, 15, 1, 1,4,1});

}
