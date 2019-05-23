package com.hyj.demo.deviceagent.utils;

import android.app.Activity;
import android.util.Log;

import java.io.DataOutputStream;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/12/13 16:33
 * @description :
 * =========================================================
 */
public class SystemUtil extends Activity{


    public static boolean rootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {

            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }


}
