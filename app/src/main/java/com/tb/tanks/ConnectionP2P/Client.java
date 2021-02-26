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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Client(InetAddress hostAddress)
    {
        hostAdd=hostAddress.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8988), 500);
            sendReceive = new SendReceive(socket);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
