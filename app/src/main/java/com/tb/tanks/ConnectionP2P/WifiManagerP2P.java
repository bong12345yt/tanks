package com.tb.tanks.ConnectionP2P;

import android.app.Activity;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WifiManagerP2P implements WifiP2pManager.ActionListener, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {
    private WifiManager wifiManager;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private Activity mainActivity;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private PeersAvailableListener peersAvailableListener;
    private Server server;
    private Client client;
    private boolean isHost = false;

    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    public WifiManagerP2P(Activity mainActivity){
        this.mainActivity = mainActivity;
        wifiManager = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        manager = (WifiP2pManager) mainActivity.getApplicationContext().getSystemService(mainActivity.WIFI_P2P_SERVICE);
        channel = manager.initialize(mainActivity, mainActivity.getMainLooper(), null);
        receiver = new WifiDirecBroadcastReciver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public String getIp(){
        byte[] ipAddress = BigInteger.valueOf(wifiManager.getConnectionInfo().getIpAddress()).toByteArray();
        InetAddress myaddr = null;
        try {
            myaddr = InetAddress.getByAddress(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return myaddr.getHostAddress();
    }

    public PeersAvailableListener getPeersAvailableListener() {
        return peersAvailableListener;
    }

    public void setPeersAvailableListener(PeersAvailableListener peersAvailableListener) {
        this.peersAvailableListener = peersAvailableListener;
    }

    public String[] getDeviceNameArray() {
        return deviceNameArray;
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public WifiP2pManager getManager() {
        return manager;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }


    public void SetEnableWifi(boolean enable){
        if(wifiManager.isWifiEnabled() != enable){
            wifiManager.setWifiEnabled(enable);
        }
    }

    public Server getServer() {
        return server;
    }

    public Client getHost() {
        return client;
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean IsWifiEnable(){
        return wifiManager.isWifiEnabled();
    }

    public void discover(){
        manager.discoverPeers(channel, this);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(mainActivity.getApplicationContext(), "Discovery Started!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int i) {
        Toast.makeText(mainActivity.getApplicationContext(), "Discovery Starting Failed!", Toast.LENGTH_SHORT).show();
        if(i == WifiP2pManager.P2P_UNSUPPORTED){
            Toast.makeText(mainActivity.getApplicationContext(), "P2P isn't support on this device!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        WifiP2pDeviceList deviceList = ((WifiDirecBroadcastReciver)receiver).getDeviceList();
        if(!deviceList.getDeviceList().equals(peers)){
            peers.clear();
            peers.addAll(deviceList.getDeviceList());
            deviceNameArray = new String[peers.size()];
            deviceArray = new WifiP2pDevice[peers.size()];
            int index = 0;
            for(WifiP2pDevice device: peers){
                deviceNameArray[index] = device.deviceName;
                deviceArray[index] = device;
            }

            if(peersAvailableListener != null){
                peersAvailableListener.onPeersAvailable(this);
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity.getApplicationContext(),android.R.layout.simple_list_item_1, deviceNameArray);
            //listView.setAdapter(adapter);
        }

        if(peers.size() == 0){
            Toast.makeText(mainActivity.getApplicationContext(), "No Device Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void connect(int i){
        final WifiP2pDevice device = deviceArray[i];
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mainActivity.getApplicationContext(), "Connected to " + device.deviceName + "...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(mainActivity.getApplicationContext(), "Connected failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disconnect() {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mainActivity.getApplicationContext(), "Disconnect!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int reason) {
                Log.d("Disconnect PEER", "onFailure" + String.valueOf(reason));
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
        if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
            server = new Server();
            server.start();
            client = new Client(groupOwnerAddress);
            //client.start();
            isHost = true;
            Toast.makeText(mainActivity.getApplicationContext(), "You are Host!", Toast.LENGTH_SHORT).show();
        }else if(wifiP2pInfo.groupFormed){
            client = new Client(groupOwnerAddress);
            //client.start();
            isHost = false;
            Toast.makeText(mainActivity.getApplicationContext(), "You are Client!", Toast.LENGTH_SHORT).show();
        }
    }
}
