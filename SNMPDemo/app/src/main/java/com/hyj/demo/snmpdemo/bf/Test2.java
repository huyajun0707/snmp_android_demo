package com.hyj.demo.snmpdemo.bf;

import android.util.Log;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
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
 * @date :   2018/8/31 15:49
 * @description :
 * =========================================================
 */
public class Test2 implements CommandResponder {
    private Snmp snmp;
    private CommunityTarget target;
    private static final String SNMP_PORT = "8899";

    public static void main(String args[]) {
//        Address targetAddress = new UdpAddress("192.168.0.5/32150");
//        new Test2().sendResponse(targetAddress, new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 3, 3, 0}));

    }

    public void initSnmp(final Address address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                target = new CommunityTarget();
                target.setCommunity(new OctetString("public"));
                target.setVersion(SnmpConstants.version1);
                target.setAddress(address);
                target.setRetries(0);
                target.setTimeout(1500);
                try {
                    TransportMapping transport;
                    transport = new DefaultUdpTransportMapping(new UdpAddress("192.168.0.12/" + SNMP_PORT));
                    snmp = new Snmp(transport);
                    snmp.addCommandResponder(new CommandResponder() {
                        @Override
                        public void processPdu(CommandResponderEvent commandResponderEvent) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    snmp.listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void sendResponse(final OID oid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PDU pdu = new PDU();
                pdu.add(new VariableBinding(new OID(oid)));
                pdu.setType(PDU.GET);
                System.out.println(pdu.toString());
                // Specify receiver

                try {
                    snmp.send(pdu, target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void processPdu(CommandResponderEvent commandResponderEvent) {
        PDU command = (PDU) commandResponderEvent.getPDU().clone();
        String lastRequestReceived = command.toString() + " " + commandResponderEvent.getPeerAddress();
        Log.d("--------->msg", lastRequestReceived);
    }


}
