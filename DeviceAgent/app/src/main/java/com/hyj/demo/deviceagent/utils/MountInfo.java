package com.hyj.demo.deviceagent.utils;

import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.VolumeInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MountInfo {
    private static final String TAG = "MountInfo";
    private String[] path = new String[8];
    private int[] type = new int[8];
    private String[] label = new String[8];

    public MountInfo() {
        try {
            // support for DevType
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                IMountService mountService = IMountService.Stub.asInterface(service);
                VolumeInfo[] list = mountService.getVolumes(0);
                List<VolumeInfo> mountList = new ArrayList<>();
                for (int i = 0; i < list.length; i++) {
                    if (2 == list[i].state) {
                        mountList.add(list[i]);
                    }
                }
                VolumeInfo sdcard = new VolumeInfo("sdcard", 0, null, null, "SDCARD");
                sdcard.internalPath = "/mnt/sdcard";
                sdcard.state = VolumeInfo.STATE_MOUNTED;
                mountList.add(sdcard);
                int index = mountList.size();
                Log.i(TAG, " index =  " + index);
                for (int i = 0; i < index; i++) {
                    path[i] = mountList.get(i).internalPath;
                    if (null == path[i]) {
                        continue;
                    }
                    label[i] = path[i].substring(path[i].lastIndexOf('/') + 1);
                    String typeStr = mountList.get(i).devType;
                    if (typeStr == null) {
                        typeStr = "USB2.0";
                    }
                    if (path[i].contains("/mnt/sdcard") || path[i].contains("/storage/emulated/0")) {
                        type[i] = 3;
                    } else if (typeStr.equals("SDCARD")) {
                        type[i] = 3;
                    } else if (typeStr.equals("SATA")) {
                        type[i] = 2;
                    } else if (typeStr.equals("USB2.0")) {
                        type[i] = 6;
                    } else if (typeStr.equals("USB3.0")) {
                        type[i] = 1;
                    } else if (typeStr.equals("UNKOWN")) {
                        type[i] = 4;
                    } else if (typeStr.equals("CD-ROM")) {
                        type[i] = 5;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 硬盘
     *
     * @return
     */
    public String getInnerHddPath() {
        String sataPath = null;
        for (int i = 0; i < type.length; i++) {
            if (type[i] == 2) {
                sataPath = path[i];
            }
        }
        return sataPath;
    }

    /**
     * 外接USB
     *
     * @return
     */
    public List<String> getExtraHddPath() {
        List<String> extraPaths = new ArrayList<>();
        for (int i = 0; i < type.length; i++) {
            if (type[i] == 1) {
                extraPaths.add(path[i]);
            }
        }
        return extraPaths;
    }


}
