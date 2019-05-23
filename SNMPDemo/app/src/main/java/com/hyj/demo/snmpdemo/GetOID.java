package com.hyj.demo.snmpdemo;

import android.os.SystemClock;
import android.util.Log;

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
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
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
 * @date :   2018/9/13 13:41
 * @description :
 * =========================================================
 */
public class GetOID {


    //    public static final OctetString NO_AUTH = new OctetString("noAuthUser");ucs_noc
    public static final OctetString NO_AUTH = new OctetString("ucs_noc");
    public static final OctetString NO_AUTH_CONTEXT_NAME = new OctetString("noAuth");

    public static final OctetString AUTH = new OctetString("ucs_noc");
    public static final OctetString AUTH_CONTEXT_NAME = new OctetString("auth");

    public static final OctetString PRIV = new OctetString("ucs_noc");
    public static final OctetString PRIV_CONTEXT_NAME = new OctetString("priv");

    public static final OID AUTH_PROTOCOL = AuthMD5.ID;
    public static final OctetString AUTH_PASS = new OctetString("authUser");
    public static final OID PRIV_PROTOCOL = PrivDES.ID;
    public static final OctetString PRIV_PASS = new OctetString("privUser");

    public static void main(String[] args) {
        OID oid = MIBtree.HARD_DISK_AVAILABLE_SPACE_OID;
        ResponseListener listener = new ResponseListener() {
            public void onResponse(ResponseEvent event) {
                // Always cancel async request when response has been received
                // otherwise a memory leak is created! Not canceling a request
                // immediately can be useful when sending a request to a broadcast
                // address.
                ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                PDU response = event.getResponse();
//            PDU request = event.getRequest();
                if (response != null) {
                    System.out.println("Got Response from Agent: " + response.toString());
                    int errorStatus = response.getErrorStatus();
                    int errorIndex = response.getErrorIndex();
                    String errorStatusText = response.getErrorStatusText();
                    if (errorStatus == PDU.noError) {
                        System.out.println("Snmp Get Response = " + response.getVariableBindings());
                        Vector<? extends VariableBinding> vbVect = response.getVariableBindings();
                        for (VariableBinding vb : vbVect) {
                            String msg = new String(OctetString.fromHexString(vb.getVariable().toString()).getValue());
                            System.out.println(vb.getOid() + " = " + msg);
                        }
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
        Address address = GenericAddress.parse("udp:192.168.1.2/161");
//        ge getOIDValueV2(oid, address, new CommandResponder() {
//            @Override
//            public void processPdu(CommandResponderEvent commandResponderEvent) {
//
//            }
//        },listener);tOIDValueV1(oid, address, null, listener);
        getOIDValueV2(oid, address, new CommandResponder() {
            @Override
            public void processPdu(CommandResponderEvent commandResponderEvent) {

            }
        },listener);
//        getOIDValueV3(1, oid, address, new CommandResponder() {
//            @Override
//            public void processPdu(CommandResponderEvent commandResponderEvent) {
//
//            }
//        }, listener);
//        getOIDValueV3(2, oid, address, new CommandResponder() {
//            @Override
//            public void processPdu(CommandResponderEvent commandResponderEvent) {
//
//            }
//        }, listener);
//        getOIDValueV3(3, oid, address, new CommandResponder() {
//            @Override
//            public void processPdu(CommandResponderEvent commandResponderEvent) {
//
//            }
//        }, listener);
    }


    /**
     * @param type==1, snmp v3, no authentication and no privacy
     *                 type==2, snmp v3, authentication and no privacy
     *                 type==3, snmp v3, authentication and privacy
     */

    public static void getOIDValueV3(int type, OID oid, Address address, CommandResponder listener, ResponseListener responseListener) {
        try {
            long startTime = System.currentTimeMillis();
            Snmp snmp = null;
            OctetString securityName = null;
            OctetString contextName = null;
            int securityLevel = 0;
            switch (type) {
                case 1:
                    securityName = NO_AUTH;
                    contextName = NO_AUTH_CONTEXT_NAME;
                    snmp = createSnmpSession(securityName, null, null, null, null);
                    securityLevel = SecurityLevel.NOAUTH_NOPRIV;
                    break;
                case 2:
                    securityName = AUTH;
                    contextName = AUTH_CONTEXT_NAME;
                    snmp = createSnmpSession(securityName,
                            AUTH_PROTOCOL, AUTH_PASS, null, null);
                    securityLevel = SecurityLevel.AUTH_NOPRIV;
                    break;
                case 3:
                    securityName = PRIV;
                    contextName = PRIV_CONTEXT_NAME;
                    snmp = createSnmpSession(securityName,
                            AUTH_PROTOCOL, AUTH_PASS, PRIV_PROTOCOL, PRIV_PASS);
                    securityLevel = SecurityLevel.AUTH_PRIV;
                    break;
                default:
                    System.out.println("Valid type is 0~3.");
                    break;
            }
            snmp.listen();
            snmp.addCommandResponder(listener);
            UserTarget myTarget = new UserTarget();
            myTarget.setAddress(address);
            myTarget.setVersion(SnmpConstants.version3);// org.snmp4j.mp.*;
            myTarget.setSecurityLevel(securityLevel);
            myTarget.setSecurityName(securityName);
            ScopedPDU pdu = new ScopedPDU();
            OID[] oids = new OID[]{
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

//            VariableBinding var = new VariableBinding(oid);
//            pdu.add(var);
            pdu.setContextName(contextName);
            pdu.setType(PDU.GET);
//            Log.d("addresss", "-->" + address);
//            snmp.send(pdu, myTarget, null, responseListener);
            ResponseEvent response = snmp.send(pdu, myTarget);
            System.out.println(snmp.getUSM().getUserTable().getUser(securityName));
            System.out.println("response=" + response.getResponse());
            System.out.println("Error=" + response.getError());
            System.out.println("The cost time for snmpv3:" + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }
    }

    public static Snmp createSnmpSession(OctetString securityName,
                                         OID authProtocol, OctetString authPass,
                                         OID privacyProtocol, OctetString privacyPass) throws IOException {
        TransportMapping transport;
        transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + "8899"));//
        Snmp snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(), new
                OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        UsmUser user = new UsmUser(securityName,
                authProtocol, authPass,
                privacyProtocol, privacyPass);
        snmp.getUSM().addUser(securityName, user);
        return snmp;
    }

    public static void getOIDValueV2(OID oid, Address address, CommandResponder listener, ResponseListener responseListener) {
        try {
            long startTime = System.currentTimeMillis();
            TransportMapping transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + "8899"));
            Snmp snmp = new Snmp(transport);
            snmp.listen();
            snmp.addCommandResponder(listener);
            // pdu
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(oid));
//            OID[] oids = new OID[]{
//                    MIBtree.DEVICE_MODEL_NUMBER_OID,
//                    MIBtree.SYS_UPTIME_MILLS_OID,
//                    MIBtree.DEVICE_SERIAL_NUMBER_OID,
//                    MIBtree.DEVICE_MANUFACTURER_OID,
//                    MIBtree.FIRMWARE_VERSION_OID,
//                    MIBtree.SOFTWARE_VERSION_OID,
//                    MIBtree.NETWORK_STATE,
//                    MIBtree.HARD_DISK_STATE_OID,
//                    MIBtree.HARD_DISK_AVAILABLE_SPACE_OID,
//                    MIBtree.CPU_TEMPERATURE_OID,
//                    MIBtree.DEVICE_VOLTAGE_OID,
//                    MIBtree.MUTE_STATE_OID,
//                    MIBtree.BATTERY_STATUS_OID
//            };
//            for (int i = 0; i < oid.size(); i++) {
//                pdu.add(new VariableBinding(oids[i]));
//            }
            pdu.setType(PDU.GET);
            // target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(address);
            target.setVersion(SnmpConstants.version2c);
            //
            ResponseEvent response = snmp.send(pdu, target);
            PDU pduResponse = response.getResponse();
            if (pduResponse != null) {
                Vector<? extends VariableBinding> vbVect = pduResponse.getVariableBindings();
                for (VariableBinding vb : vbVect) {
                    String msg = vb.getVariable().toString();
                    System.out.println(vb.getOid() + " = " + msg);
                }
            }
            System.out.println(response.getResponse());
            System.out.println(response.getError());

//            snmp.send(pdu, target, null, responseListener);
            System.out.println("The cost time for snmpv2:" + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getOIDValueV1(OID oid, Address address, CommandResponder listener, ResponseListener responseListener) {
        try {
            long startTime = System.currentTimeMillis();
            TransportMapping transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + "8899"));
            Snmp snmp = new Snmp(transport);
            snmp.listen();
            snmp.addCommandResponder(listener);
            // pdu
            PDU pdu = new PDU();
            OID[] oids = new OID[]{
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
//            for (int i = 0; i < oid.size(); i++) {
//                pdu.add(new VariableBinding(oids[i]));
//            }
            pdu.add(new VariableBinding(oid));
            pdu.setType(PDU.GET);
            // target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(address);
            target.setVersion(SnmpConstants.version1);
            //
//            ResponseEvent response = snmp.send(pdu, target);
            snmp.send(pdu, target, null, responseListener);
//            PDU pduResponse = response.getResponse();
//            if (pduResponse != null) {
//                Vector<? extends VariableBinding> vbVect = pduResponse.getVariableBindings();
//                for (VariableBinding vb : vbVect) {
//                    String msg = new String(OctetString.fromHexString(vb.getVariable().toString()).getValue());
//                    System.out.println(vb.getOid() + " = " + msg);
//                }
//            }
//            System.out.println(response.getResponse());
//            System.out.println(response.getError());
            System.out.println("The cost time for snmpv2:" + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
