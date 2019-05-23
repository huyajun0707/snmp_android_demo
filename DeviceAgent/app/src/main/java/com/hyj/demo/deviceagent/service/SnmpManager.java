package com.hyj.demo.deviceagent.service;

import android.content.Context;
import android.util.Log;

import com.hyj.demo.deviceagent.constant.Constant;
import com.hyj.demo.deviceagent.utils.LogUtil;
import com.hyj.demo.deviceagent.utils.StringUtil;

import org.snmp4j.AbstractTarget;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.mp.StatusInformation;
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
import java.util.ArrayList;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/13 16:06
 * @description :
 * =========================================================
 */
public class SnmpManager {
    private static SnmpManager snmpManager;
    private static Snmp snmp;
    private static Snmp trapSnmp;
    private static TransportMapping transport;
    private static Context mContext;
    private static ArrayList<Address> registeredManagers = null;
    private static AbstractTarget comtarget;

    public ArrayList<Address> getRegisteredManagers() {
        return registeredManagers;
    }

    private SnmpManager() {
        TransportMapping transport;
        try {
            transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + Constant.SNMP_PORT));
            snmp = new Snmp(transport);
            USM usm = new USM(SecurityProtocols.getInstance(), new
                    OctetString(MPv3.createLocalEngineID()), 0);
            LogUtil.getInstance().print("EngineId" + new String(MPv3.createLocalEngineID().clone()));
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.setLocalEngine(MPv3.createLocalEngineID(), 0, 0);
//            snmp.addCommandResponder(this);
            snmp.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // cannot be instantiated
    }

    public static synchronized SnmpManager getInstance() {
        if (snmpManager == null) {
            snmpManager = new SnmpManager();
        }
        return snmpManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void addUser(UsmUser usmUser) {
        snmp.getUSM().addUser(usmUser.getSecurityName(), usmUser);
    }

    public void addCommandResponder(CommandResponder listener) {
        snmp.addCommandResponder(listener);
    }

    public static void registerManager(Address address) {
        if (registeredManagers == null) {
            registeredManagers = new ArrayList<>();
            registeredManagers.add(address);
        } else {
            boolean exists = false;
            for (Address a : registeredManagers) {
                if (a.toString().equals(address.toString())) exists = true;
            }
            if (!exists) registeredManagers.add(address);
        }
    }

    public void handleGetRequest(PDU command) {
        VariableBinding varBind;
        for (int i = 0; i < command.size(); i++) {
            varBind = command.get(i);
            varBind.setVariable(answerForGet(varBind.getOid()));
        }
    }

    public Variable answerForGet(OID oid) {
        return SystemInformation.getInstance(mContext).getVariableBinding(oid).getVariable();
    }


    /**
     * v3版本返回信息设置
     *
     * @param event
     * @param command
     */
    public void sendV3Response(CommandResponderEvent event, PDU command) {
        LogUtil.getInstance().print("sendV3Response");
        try {
            command.setType(PDU.RESPONSE);
            event.getMessageDispatcher().returnResponsePdu(
                    event.getMessageProcessingModel(),
                    event.getSecurityModel(),
                    event.getSecurityName(),
                    event.getSecurityLevel(), command,
                    event.getMaxSizeResponsePDU(), event.getStateReference(),
                    new StatusInformation());
        } catch (MessageException e) {
            e.printStackTrace();
        }

    }

    /**
     * v1版本response处理
     *
     * @param address
     * @param command
     */
    private void sendV1Response(Address address, PDU command) {
        command.setType(PDU.RESPONSE);
        CommunityTarget comtarget = new CommunityTarget();
        //设置安全级别
        comtarget.setVersion(SnmpConstants.version1);
        comtarget.setAddress(address);
        comtarget.setCommunity(new OctetString(Constant.PUBLIC));
        comtarget.setRetries(0);
        comtarget.setTimeout(1500);
        try {
            snmp.send(command, comtarget);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * v2c版本response处理
     *
     * @param address
     * @param command
     */
    private void sendV2Response(Snmp snmp, Address address, PDU command) {
        command.setType(PDU.RESPONSE);
        CommunityTarget comtarget = new CommunityTarget();
        //设置安全级别
        comtarget.setVersion(SnmpConstants.version2c);
        comtarget.setAddress(address);
        comtarget.setCommunity(new OctetString(Constant.PUBLIC));
        comtarget.setRetries(0);
        comtarget.setTimeout(1500);
        try {
            snmp.send(command, comtarget);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送trap消息
     *
     * @param variableBinding
     */
    public void sendTrap(VariableBinding variableBinding) {
        PDUv1 pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setGenericTrap(PDUv1.COLDSTART);
//        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 2, 1, 1, 2}), new OctetString("snmp trap 一天")));
        // Specify receiver
        pdu.add(variableBinding);
        Address targetAddress = new UdpAddress(Constant.TRAP_IP);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setVersion(SnmpConstants.version1);
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        Log.d("--->SendTrap", "begain");
        try {
            snmp.trap(pdu, target);
        } catch (IOException e) {
            Log.d("-----IOException", e.getMessage());
            e.printStackTrace();
        }
    }


}
