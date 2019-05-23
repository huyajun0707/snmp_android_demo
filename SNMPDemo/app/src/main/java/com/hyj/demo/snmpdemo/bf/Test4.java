package com.hyj.demo.snmpdemo.bf;

import android.util.Log;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
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
public class Test4 implements CommandResponder {


    public void client() {
        try {
            TransportMapping transport =
                    new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/32150"));
            Snmp snmp = new Snmp(transport);
//        if (version == SnmpConstants.version3) {
            byte[] localEngineID =
                    ((MPv3) snmp.getMessageProcessingModel(MessageProcessingModel.MPv3)).createLocalEngineID();
            USM usm = new USM(SecurityProtocols.getInstance(),
                    new OctetString(localEngineID), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.setLocalEngine(localEngineID, 0, 0);
            UsmUser user = new UsmUser(
                    new OctetString("noAuthNoPriv"),
                    null, null,
                    null, null);
            snmp.getUSM().addUser(new OctetString("noAuthNoPriv"), user);
            // Add the configured user to the USM
//        }
            snmp.addCommandResponder(this);
            snmp.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void processPdu(CommandResponderEvent commandResponderEvent) {
        Log.d("---->", "processPdu");
    }
}
