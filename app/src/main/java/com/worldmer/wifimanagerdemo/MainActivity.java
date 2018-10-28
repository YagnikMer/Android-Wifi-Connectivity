package com.worldmer.wifimanagerdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvStatus;
    Button btnwifistatus, btnwifiscan;

    boolean isWifiStatus = false;

    WifiManager manager;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        tvStatus = findViewById(R.id.tvstatus);
        btnwifistatus = findViewById(R.id.btnonoff);
        btnwifiscan = findViewById(R.id.btnwifiscan);

        manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        btnwifistatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWifiStatus)
                    manager.setWifiEnabled(false);
                else
                    manager.setWifiEnabled(true);
                isWifiStatus = manager.isWifiEnabled();
                tvStatus.setText(isWifiStatus == true ? "ON" : "OFF");
                btnwifistatus.setText(isWifiStatus == true ? "OFF" : "ON");
                btnwifiscan.setEnabled(isWifiStatus);
            }
        });
        btnwifiscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWifiStatus) {
                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                    } else {
                        startActivity(new Intent(activity, ScanWifiActivity.class));
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isWifiStatus = manager.isWifiEnabled();
        tvStatus.setText(isWifiStatus == true ? "ON" : "OFF");
        btnwifistatus.setText(isWifiStatus == true ? "OFF" : "ON");
        btnwifiscan.setEnabled(isWifiStatus);
    }
}
