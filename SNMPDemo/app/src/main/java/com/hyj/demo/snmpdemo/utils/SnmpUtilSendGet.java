package com.hyj.demo.snmpdemo.utils;


import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
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
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.Vector;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/12 14:45
 * @description :
 * =========================================================
 */
public class SnmpUtilSendGet {
    private Address targetAddress = null;
    private OID authProtocol;
    private OID privProtocol;
    private OctetString privPassphrase;
    private OctetString authPassphrase;
    private OctetString securityName = new OctetString();
    private int version;
    private String host;
    private Target target;
    private OctetString community = new OctetString("public");
    private TransportMapping transport;

    /**
     * 初始化Snmp信息
     *
     * @param host
     * @param user
     * @param authProtocol
     * @param authPasshrase
     * @param privProtocol
     * @param privPassphrase
     * @param version
     */
    public SnmpUtilSendGet(String host, String user, String authProtocol, String authPasshrase
            , String privProtocol, String privPassphrase, int version) {
        this.authPassphrase = new OctetString(authPasshrase);
        this.securityName = new OctetString(user);
        this.privPassphrase = new OctetString(privPassphrase);
        this.version = version;
        this.host = host;
        if (authProtocol.equals("MD5")) {
            this.authProtocol = AuthMD5.ID;
        } else if (authProtocol.equals("SHA")) {
            this.authProtocol = AuthSHA.ID;
        }
        if (privProtocol.equals("DES")) {
            this.privProtocol = PrivDES.ID;
        } else if ((privProtocol.equals("AES128")) || (privProtocol.equals("AES"))) {
            this.privProtocol = PrivAES128.ID;
        } else if (privProtocol.equals("AES192")) {
            this.privProtocol = PrivAES192.ID;
        } else if (privProtocol.equals("AES256")) {
            this.privProtocol = PrivAES256.ID;
        }
    }

    /**
     * 创建snmp
     *
     * @throws IOException
     */
    public Snmp createSnmpSession() throws IOException {
        // 设置管理进程的IP和端口
        targetAddress = GenericAddress.parse(host);
        if (targetAddress instanceof TcpAddress) {
            transport = new DefaultTcpTransportMapping();
        } else {
            transport = new DefaultUdpTransportMapping();
        }
        Snmp snmp = new Snmp(transport);
        if (version == SnmpConstants.version3) {
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                    MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            // Add the configured user to the USM
            addUsmUser(snmp);
        }
        return snmp;
    }

    /**
     * 加入user信息（snmp3）
     *
     * @param snmp
     */
    private void addUsmUser(Snmp snmp) {
        snmp.getUSM().addUser(securityName,
                new UsmUser(securityName, authProtocol, authPassphrase
                        , privProtocol, privPassphrase));
    }

    /**
     * 创建PUD
     *
     * @param target
     * @return
     */
    public PDU createPDU(Target target) {
        PDU request;
        if (target.getVersion() == SnmpConstants.version3) {
            request = new ScopedPDU();
        } else {
            request = new PDU();
        }
        return request;
    }

    /**
     * 创建Target
     *
     * @return
     */
    private Target createTarget() {
        if (version == SnmpConstants.version3) {
            UserTarget target = new UserTarget();
            if (authPassphrase != null) {
                if (privPassphrase != null) {
                    target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
                } else {
                    target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
                }
            } else {
                target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
            }
            target.setSecurityName(securityName);
            return target;
        } else {
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(community);
            return target;
        }
    }

    /**
     * 向管理进程发送Get报文
     *
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public String sendPDU(OID oid) throws IOException {
        String v = null;
        Snmp snmp = createSnmpSession();
        target = createTarget();// 设置 target
        target.setAddress(targetAddress);
        target.setRetries(2);// 通信不成功时的重试次数
        target.setTimeout(1500);// 超时时间
        target.setVersion(version);// snmp版本
        transport.listen();
        // 创建 PDU
        ScopedPDU pdu = (ScopedPDU) createPDU(target);
        pdu.add(new VariableBinding(oid));
        pdu.setType(PDU.GET);
        ResponseEvent respEvnt = snmp.send(pdu, target);

        if (respEvnt != null && respEvnt.getResponse() != null) {
            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse().getVariableBindings();
            //for (int i = 0; i < recVBs.size(); i++) {
            VariableBinding recVB = recVBs.elementAt(0);
            System.out.println(recVB.getVariable().toString());
            System.out.println("------------------------------------------");
            v = recVB.getVariable().toString();
            snmp.close();
        }
        return v;


    }

}
