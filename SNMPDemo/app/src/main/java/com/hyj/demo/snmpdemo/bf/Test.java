package com.hyj.demo.snmpdemo.bf;

import android.util.Log;

import com.hyj.demo.snmpdemo.GetOID;
import com.hyj.demo.snmpdemo.MIBtree;
import com.hyj.demo.snmpdemo.models.SNMPResponseListener;
import com.hyj.demo.snmpdemo.utils.SnmpUtilSendGet;

import org.snmp4j.AbstractTarget;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.Vector;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/8/31 15:39
 * @description :
 * =========================================================
 */
public class Test implements CommandResponder {

    private static AbstractTarget comtarget;
    private Snmp snmp;
    private int version = 1;

    public static void main(String arg[]) {
//        Test test = new Test();
//        test.initServer("192.168.0.11");
//        test.sendGetRequest(new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 3, 3, 0}), new SNMPResponseListener() {
//            @Override
//            public void onSNMPResponseReceived(Vector<? extends VariableBinding> variableBinding) {
//                String result = variableBinding.get(0).toValueString();
//                System.out.print("--->result:" + result);
//            }
//        });

        String Address = "192.168.0.2";

        SnmpUtilSendGet util = new SnmpUtilSendGet("udp:" + Address + "/32150", "managerv3", "MD5", "authpassword", "DES",
                "pripassword", SnmpConstants.version3);
        try {
            util.sendPDU(MIBtree.SYS_UPTIME_MILLS_OID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Test() {

    }

    public Test(int version) {
        this.version = version;
    }

    public void initServer(String ip) {
        TransportMapping transport;
        try {
            transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + "8899"));
            // Create Snmp object for sending data to Agent
            snmp = new Snmp(transport);
            if (version == 3) {
                //设置安全模式
                initV3SafeMode();
            }
            snmp.listen();
            snmp.addCommandResponder(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address targetAddress = new UdpAddress(ip + "/32150");
        if (version == 3) {
            initV3Target();
        } else {
            comtarget = new CommunityTarget();
            if (version == 1) {
                initV1Target();
            } else {
                initV2Target();
            }
        }
        comtarget.setAddress(targetAddress);
        comtarget.setRetries(3);
        comtarget.setTimeout(5000);
    }

    /**
     * 设置v3版本安全模式
     */
    private void initV3SafeMode() {
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
//        snmp.setLocalEngine(MPv3.createLocalEngineID(), 0, 0);
    }

    private void initV3Target() {
        UsmUser user1 = new UsmUser(GetOID.NO_AUTH,
                null, null,
                null, null);

        UsmUser user2 = new UsmUser(GetOID.AUTH,
                GetOID.AUTH_PROTOCOL, GetOID.AUTH_PASS,
                null, null);

        UsmUser user3 = new UsmUser(GetOID.PRIV,
                GetOID.AUTH_PROTOCOL, GetOID.AUTH_PASS,
                GetOID.PRIV_PROTOCOL, GetOID.PRIV_PASS);
        snmp.getUSM().addUser(GetOID.NO_AUTH, user1);
        snmp.getUSM().addUser(GetOID.AUTH, user2);
        snmp.getUSM().addUser(GetOID.PRIV, user3);
        comtarget = new UserTarget();
        //设置安全级别
        ((UserTarget) comtarget).setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
        ((UserTarget) comtarget).setSecurityName(GetOID.NO_AUTH);
        comtarget.setVersion(SnmpConstants.version3);
    }

    private void initV1Target() {
        comtarget.setVersion(SnmpConstants.version1);
        ((CommunityTarget) comtarget).setCommunity(new OctetString("public"));
    }

    private void initV2Target() {
        comtarget.setVersion(SnmpConstants.version2c);
        ((CommunityTarget) comtarget).setCommunity(new OctetString("public"));
    }

    public void sendGetRequest(OID oid, final SNMPResponseListener responseListener) {
        try {
            // Create the PDU object
            PDU pdu = null;
            if (version == 3) {
                pdu = new ScopedPDU();

            } else {
                pdu = new PDU();
            }
            final OID[] oids = new OID[]{
                    MIBtree.DEVICE_MODEL_NUMBER_OID,
                    MIBtree.SYS_UPTIME_MILLS_OID,
                    MIBtree.DEVICE_SERIAL_NUMBER_OID,
                    MIBtree.DEVICE_MANUFACTURER_OID,
                    MIBtree.FIRMWARE_VERSION_OID,
                    MIBtree.SOFTWARE_VERSION_OID,
                    MIBtree.NETWORK_STATE,
                    MIBtree.HARD_DISK_STATE_OID,
                    MIBtree.HARD_DISK_AVAILABLE_SPACE_OID,
                    MIBtree.CPU_TEMPERATURE_OID,
                    MIBtree.DEVICE_VOLTAGE_OID,
                    MIBtree.MUTE_STATE_OID,
                    MIBtree.BATTERY_STATUS_OID
            };
            for (int i = 0; i < oid.size(); i++) {
                pdu.add(new VariableBinding(oids[i]));
            }
//            pdu.add(new VariableBinding(oid));
            pdu.setType(PDU.GET);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    // Always cancel async request when response has been received
                    // otherwise a memory leak is created! Not canceling a request
                    // immediately can be useful when sending a request to a broadcast
                    // address.
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    PDU request = event.getRequest();
                    if (response != null) {
                        System.out.println("Got Response from Agent: " + response.toString());
                        int errorStatus = response.getErrorStatus();
                        int errorIndex = response.getErrorIndex();
                        String errorStatusText = response.getErrorStatusText();
                        if (errorStatus == PDU.noError) {
                            System.out.println("Snmp Get Response = " + response.getVariableBindings());
                            responseListener.onSNMPResponseReceived(response.getVariableBindings());
                        } else {
                            System.out.println("Error: Request Failed");
                            System.out.println("Error Status = " + errorStatus);
                            System.out.println("Error Index = " + errorIndex);
                            System.out.println("Error Status Text = " + errorStatusText);
                        }
                    } else {
                        System.out.println("Error: Agent Timeout... ");
                    }
                }
            };

            System.out.println("Sending Request to Agent...");
//            ResponseEvent responseEvent = snmp.send(pdu, comtarget);
//            ScopedPDU response = (ScopedPDU) responseEvent.getResponse();
//            Vector<? extends VariableBinding> variableBindings = response.getVariableBindings();
//
//            String result = variableBindings.get(0).toValueString();
//
//            Log.d("----->result", result);
            snmp.send(pdu, comtarget, null, listener);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        Log.d("---->监听到来自设备的信息", "----");

        Log.d("---->监听到来自设备的信息", "getSecurityName----" + new String(event.getSecurityName()).toString());

        System.out.println("----&gt; 开始解析ResponderEvent: &lt;----");
        if (event == null || event.getPDU() == null) {
            System.out.println("[Warn] ResponderEvent or PDU is null");
            return;
        }
        Vector<? extends VariableBinding> vbVect = event.getPDU().getVariableBindings();
        for (VariableBinding vb : vbVect) {
            String msg = StringUtil.getInstance().getChinese(vb.getVariable().toString());
            System.out.println(vb.getOid() + " = " + msg);
        }
        System.out.println("----&gt;  本次ResponderEvent 解析结束 &lt;----");
    }
}
