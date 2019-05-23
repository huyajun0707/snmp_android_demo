package com.hyj.demo.snmpdemo.bf;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
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
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/13 10:07
 * @description :
 * =========================================================
 */
public class Test3 {

    private ScopedPDU scopedPdu;
    private UserTarget target;


    public static void main(String[] args) {
//        Test3 test3 = new Test3();
//        test3.createUserTarget();
//        test3.createPud();
//        test3.sendGet();
    }

    public void createPud() {
        scopedPdu = new ScopedPDU();
        scopedPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.2.1"))); // ifNumber
//        scopedPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.10"))); // ifInOctets
//        scopedPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.16"))); // ifOutOctets
        scopedPdu.setType(PDU.GETBULK);
        scopedPdu.setMaxRepetitions(50);
//只获取一次ifNumber
        scopedPdu.setNonRepeaters(1);
//设置上下文非默认上下文(不需要设置默认上下文)
        scopedPdu.setContextName(new OctetString("subSystemContextA"));
//设置非默认上下文引擎ID(使用目标权威引擎ID
//使用空(size == 0)八位字符串字符串)
//        scopedPdu.setContextEngineID(OctetString.fromHexString("80：00：13：70：C0：A8：01：0D"));
    }

    private void createUserTarget() {
        Address targetAddress = new UdpAddress("192.168.0.23/32150");
        target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
//将超时设置为500毫秒 - > 2 * 500毫秒= 1秒总超时
        target.setTimeout(500);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(new OctetString("nmsAdmin"));
    }

    public void sendGet() {
        Snmp snmp = null;
        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                    MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            UsmUser user = new UsmUser(
                    new OctetString("nmsAdmin"),
                    AuthMD5.ID, new OctetString("nmsAuthKey"),
                    PrivDES.ID, new OctetString("nmsPrivKey"));
            snmp.getUSM().addUser(new OctetString("nmsAdmin"), user);
            ResponseListener listener = new ResponseListener() {
                @Override
                public void onResponse(ResponseEvent event) {
                    PDU request = event.getResponse();
                    PDU response = event.getRequest();
                    if (response == null) {
                        System.out.println("请求" + request + "超时");
                    } else {
                        System.out.println("收到回复" + response + "请求" +
                                request);
                    }
                }
            };
            snmp.send(scopedPdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
