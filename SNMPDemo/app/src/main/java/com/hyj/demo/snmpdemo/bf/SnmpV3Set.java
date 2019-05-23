package com.hyj.demo.snmpdemo.bf;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.TSM;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/13 11:32
 * @description :
 * =========================================================
 */
public class SnmpV3Set {

    public static void main(String[] args) throws Exception {
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);

        OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
        SecurityModels.getInstance().addSecurityModel(usm);

        OctetString securityName = new OctetString("your-security-name");
        OID authProtocol = AuthMD5.ID;
        OID privProtocol = PrivDES.ID;
        OctetString authPassphrase = new OctetString("your-auth-passphrase");
        OctetString privPassphrase = new OctetString("your-priv-passphrase");

        snmp.getUSM().addUser(securityName, new UsmUser(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase));
        SecurityModels.getInstance().addSecurityModel(new TSM(localEngineId, false));
        UserTarget target = new UserTarget();
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(securityName);

        target.setAddress(GenericAddress.parse(String.format("udp:%s/%s", "your-target-ip", "your-port-number")));
        target.setVersion(SnmpConstants.version3);
        target.setRetries(2);
        target.setTimeout(60000);
        transport.listen();

        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID("your-oid"), new OctetString("Hello world!")));
        pdu.setType(PDU.SET);
        ResponseEvent event = snmp.send(pdu, target);
        if (event != null) {
            pdu = event.getResponse();
            if (pdu.getErrorStatus() == PDU.noError) {
                System.out.println("SNMPv3 SET Successful!");
            } else {
                System.out.println("SNMPv3 SET Unsuccessful.");
            }
        } else {
            System.out.println("SNMP send unsuccessful.");
        }
    }

}
