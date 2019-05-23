package com.hyj.demo.snmpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyj.demo.snmpdemo.bf.StringUtil;
import com.hyj.demo.snmpdemo.bf.Test;
import com.hyj.demo.snmpdemo.models.SNMPResponseListener;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.util.Vector;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/3 15:02
 * @description :  snmp设备管理服务端Demo(已通待封装)
 * =========================================================
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, CommandResponder {
    private static final String community = "public";
    private static final int snmpVersion = SnmpConstants.version1;
    private static CommunityTarget comtarget;
    public static String port = "32150";
    private EditText etIp;
    Address address;
    Snmp snmp;
    private Test test = null;
    private Button btSetIp, btRequestV1, btRequestV2, btRequestV3_1, btRequestV3_2, btRequestV3_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etIp = findViewById(R.id.etIp);
        btSetIp = findViewById(R.id.btSetIP);
        btRequestV1 = findViewById(R.id.btRequestV1);
        btRequestV2 = findViewById(R.id.btRequestV2);
        btRequestV3_1 = findViewById(R.id.btRequestV3_1);
        btRequestV3_2 = findViewById(R.id.btRequestV3_2);
        btRequestV3_3 = findViewById(R.id.btRequestV3_3);
        btSetIp.setOnClickListener(this);
        btRequestV1.setOnClickListener(this);
        btRequestV2.setOnClickListener(this);
        btRequestV3_1.setOnClickListener(this);
        btRequestV3_2.setOnClickListener(this);
        btRequestV3_3.setOnClickListener(this);

        etIp.setText("192.168.0.111");
//        findViewById(R.id.btSetIP).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        test.initServer(etIp.getText().toString());

//                        new Test4().client();
//                        address = GenericAddress.parse("udp:" + etIp.getText().toString() + "/32150");

//                    }
//                }).start();
//            }
//        });

//        findViewById(R.id.btRequest).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        test.sendGetRequest(new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 1}), new SNMPResponseListener() {
//                            @Override
//                            public void onSNMPResponseReceived(Vector<? extends VariableBinding> variableBinding) {
//                                String result = variableBinding.get(0).toValueString();
//                                Log.d("--->result:", result);
//                            }
//                        });
//                    }
//                }).start();

//                for (int i = 0; i < oids.length; i++) {
//                    final int finalI = i;
//                ThreadPoolUtil.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        test.sendGetRequest(MIBtree.CPU_TEMPERATURE_OID, new SNMPResponseListener() {
//                            @Override
//                            public void onSNMPResponseReceived(Vector<? extends VariableBinding> variableBinding) {
//                                for (int i = 0; i < variableBinding.size(); i++) {
//                                    String result = variableBinding.get(i).toValueString();
//                                    Log.d("--->" + i, "result:" + result);
//                                }
//                            }
//                        });
//                        GetOID.getOIDValueV1(MIBtree.SYS_UPTIME_MILLS_OID, address, new CommandResponder() {
//                            @Override
//                            public void processPdu(CommandResponderEvent commandResponderEvent) {
//                                Log.d("----->", "processPdu");
//                            }
//                        });
//
//                    }
//                });
//            }

//            }

//        });


//        findViewById(R.id.btGetModeNumberg).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        test.sendGetRequest(new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 1, 4, 0}), new SNMPResponseListener() {
//                            @Override
//                            public void onSNMPResponseReceived(Vector<? extends VariableBinding> variableBinding) {
//                                String result = variableBinding.get(0).toValueString();
//                                Log.d("--->result:", result);
//                            }
//                        });
//                    }
//                }).start();
//            }
//        });
    }

    public static OID[] oids = new OID[]{
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSetIP:
                address = GenericAddress.parse("udp:" + etIp.getText().toString() + "/36880");
                break;
            case R.id.btRequestV1:
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        GetOID.getOIDValueV1(MIBtree.BATTERY_STATUS_OID, address, MainActivity.this, listener);
                    }
                });
                break;
            case R.id.btRequestV2:
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        GetOID.getOIDValueV2(MIBtree.SYS_UPTIME_MILLS_OID, address, MainActivity.this, listener);
                    }
                });
                break;
            case R.id.btRequestV3_1:
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        GetOID.getOIDValueV3(1, MIBtree.SYS_UPTIME_MILLS_OID, address, MainActivity.this, listener);
                    }
                });
                break;
            case R.id.btRequestV3_2:
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        GetOID.getOIDValueV3(2, MIBtree.SYS_UPTIME_MILLS_OID, address, MainActivity.this, listener);
                    }
                });
                break;
            case R.id.btRequestV3_3:
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        GetOID.getOIDValueV3(3, MIBtree.SYS_UPTIME_MILLS_OID, address, MainActivity.this, listener);
                    }
                });
                break;
        }
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        Log.d("---->监听到来自设备的信息", "getSecurityName----" + new String(event.getSecurityName()).toString());
        if (event == null || event.getPDU() == null) {
            System.out.println("[Warn] ResponderEvent or PDU is null");
            return;
        }
        Vector<? extends VariableBinding> vbVect = event.getPDU().getVariableBindings();
        for (VariableBinding vb : vbVect) {
            String msg = new String(OctetString.fromHexString(vb.getVariable().toString()).getValue());
            System.out.println(vb.getOid() + " = " + msg);
        }
        System.out.println("----&gt;  本次ResponderEvent 解析结束 &lt;----");

    }

    ResponseListener listener = new ResponseListener() {
        public void onResponse(ResponseEvent event) {
            System.out.println("onResponse: ");
            // Always cancel async request when response has been received
            // otherwise a memory leak is created! Not canceling a request
            // immediately can be useful when sending a request to a broadcast
            // address.
//            ((Snmp) event.getSource()).cancel(event.getRequest(), this);
            System.out.println("onResponse11: " + event.getResponse()+event.getPeerAddress());
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
}
