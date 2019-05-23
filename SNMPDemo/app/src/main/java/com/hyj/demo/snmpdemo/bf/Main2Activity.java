package com.hyj.demo.snmpdemo.bf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hyj.demo.snmpdemo.R;

import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;

public class Main2Activity extends AppCompatActivity {
    private Test2 test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Address targetAddress = new UdpAddress("192.168.0.5/32150");
        test2 = new Test2();
        test2.initSnmp(targetAddress);
//        findViewById(R.id.btRequest).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                test2.sendResponse(new OID(new int[]{1, 3, 6, 1, 4, 1, 12619, 1, 3, 3, 0}));
//            }
//        });
    }
}
