package com.tb.tanks.ConnectionP2P;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread {
    Socket socket;
    String hostAdd;
    private SendReceive sendReceive = null;

    public SendReceive getSendReceive() {
        return sendReceive;
    }

    public Client(InetAddress hostAddress)
    {
        hostAdd=hostAddress.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888), 500);
            sendReceive = new SendReceive(socket);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
