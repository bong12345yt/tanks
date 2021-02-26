package com.tb.tanks.ConnectionP2P;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;


public class WifiDirecBroadcastReciver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiManagerP2P wifiManagerP2P;
    private WifiP2pDeviceList deviceList;
    public P2PConnectionListener p2PConnectionListener = null;

    public WifiDirecBroadcastReciver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, WifiManagerP2P wifiManagerP2P){
        this.manager = mManager;
        this.channel = mChannel;
        this.wifiManagerP2P = wifiManagerP2P;
    }

    public WifiP2pDeviceList getDeviceList() {
        return deviceList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "Wifi is TURN ON!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Wifi is TURN OFF!", Toast.LENGTH_SHORT).show();
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null){
                deviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                manager.requestPeers(channel, wifiManagerP2P);
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if(manager == null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                manager.requestConnectionInfo(channel, wifiManagerP2P);
            } else {
                Toast.makeText(context, "Device Disconnected!", Toast.LENGTH_SHORT).show();
                if(p2PConnectionListener != null){
                    p2PConnectionListener.onDisconnect();
                }
            }

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
