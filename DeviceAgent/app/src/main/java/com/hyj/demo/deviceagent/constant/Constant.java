package com.hyj.demo.deviceagent.constant;

import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/12 11:05
 * @description :
 * =========================================================
 */
public class Constant {
    public static int VERSION = 3;

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

    public static String PUBLIC = "public";
    public static final String SNMP_PORT = "161";//36880
    public static String TRAP_IP = "192.168.0.6/8899";
    public static final int MIN_SIZE = 100;

}
