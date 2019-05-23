package com.hyj.demo.snmpdemo.bf;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
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
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/11 15:26
 * @description :
 * =========================================================
 */
public class Snmp_manager {
    private Snmp snmp = null;
    private String version = null;

    public Snmp_manager(String version) {
        try {
            this.version = version;
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            if (version.equals("3")) {
                //设置安全模式
                USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);
            }
            //开始监听消息
            transport.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param syn  是否是同步模式
     * @param bro  是否是广播
     * @param pdu  要发送的报文
     * @param addr 目标地址
     * @throws IOException
     */
    public void sendMessage(Boolean syn, final Boolean bro, PDU pdu, String addr) throws IOException {
        //生成目标地址对象
        Address targetAddress = GenericAddress.parse(addr);
        Target target = null;
        if (version.equals("3")) {
            //添加用户
            snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                    new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
            target = new UserTarget();
            //设置安全级别
            ((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
            ((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
            target.setVersion(SnmpConstants.version3);
        } else {
            target = new CommunityTarget();
            if (version.equals("1")) {
                target.setVersion(SnmpConstants.version1);
                ((CommunityTarget) target).setCommunity(new OctetString("public"));
            } else {
                target.setVersion(SnmpConstants.version2c);
                ((CommunityTarget) target).setCommunity(new OctetString("public"));
            }
        }
        // 目标对象相关设置
        target.setAddress(targetAddress);
        target.setRetries(5);
        target.setTimeout(1000);
        if (syn.equals(true)) {
            //发送报文  并且接受响应
            ResponseEvent response = snmp.send(pdu, target);
            //处理响应
            System.out.println("Synchronize message from " + response.getPeerAddress() + "/nrequest:"
                    + response.getRequest() + "/nresponse:" + response.getResponse());
        } else {
            //设置监听对象
            ResponseListener listener = new ResponseListener() {
                @Override
                public void onResponse(ResponseEvent event) {
                    if (bro.equals(false)) {
                        ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    }
                    //处理响应
                    PDU request = event.getRequest();
                    PDU response = event.getResponse();
                    System.out.println("Asynchronise message from "
                            + event.getPeerAddress() + "/nrequest:" + request
                            + "/nresponse:" + response);
                }
            };
            //发送报文
            snmp.send(pdu, target, null, listener);
        }
    }

    public static void main(String[] args) {
        Snmp_manager manager = new Snmp_manager("1");
        //构造报文
        PDU pdu = new PDU(); //  PDU pdu = new ScopedPDU();
        //设置要获取的对象ID
        OID oids = new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 1, 13});
        pdu.add(new VariableBinding(oids));
        //设置报文类型
        pdu.setType(PDU.GET);
        //((ScopedPDU) pdu).setContextName(new OctetString("priv"));
        try {
            //发送消息   其中最后一个是想要发送的目标地址
            manager.sendMessage(false, true, pdu, "udp:192.168.0.3/32150");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
