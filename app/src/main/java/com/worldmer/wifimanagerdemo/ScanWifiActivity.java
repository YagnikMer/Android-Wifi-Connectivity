package com.worldmer.wifimanagerdemo;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class ScanWifiActivity extends AppCompatActivity {
    WifiManager manager;
    ListView lvlist;
    Activity activity;
    String[] list;
    EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);
        activity = this;
        lvlist = findViewById(R.id.lvlist);
        edtPassword = findViewById(R.id.edtpassword);
        manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        final List<ScanResult> results = manager.getScanResults();
        list = new String[results.size()];
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                list[i] = results.get(i).SSID;
                Log.d("ResuitLog", "Scan : " + results.get(i).SSID);
            }
        }

        lvlist.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
        lvlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connectWiFi(results.get(i));
            }
        });
    }

    public void connectWiFi(ScanResult scanResult) {
        try {
            Log.d("WIFI_INFO", "SSID : " + scanResult.SSID);
            Log.d("WIFI_INFO", "capabilities : " + scanResult.capabilities);
            String networkPass = null;
            String networkSSID = scanResult.SSID;
            if (edtPassword.getText().toString() != null) {
                if (edtPassword.getText().toString().trim().length() > 0) {
                    networkPass = edtPassword.getText().toString();
                }
            } else {
                networkPass = "";
            }
            Log.d("WIFI_INFO", "PASSWORD : " + networkPass);
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.d("WIFI_INFO", "Security : WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.d("WIFI_INFO", "Security : WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";

            } else {
                Log.d("WIFI_INFO", "Security : OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            int networkId = manager.addNetwork(conf);
            Log.d("WIFI_INFO", "Network : "+ networkId);
            List<WifiConfiguration> list = manager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    boolean isDisconnected = manager.disconnect();
                    boolean isEnabled = manager.enableNetwork(i.networkId, true);
                    boolean isReconnected = manager.reconnect();
                    Log.d("WIFI_INFO", "WifiConfiguration SSID " + i.SSID);
                    Log.d("WIFI_INFO", "isDisconnected : " + isDisconnected);
                    Log.d("WIFI_INFO", "isEnabled : " + isEnabled);
                    Log.d("WIFI_INFO", "isReconnected : " + isReconnected);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
