package com.tb.tanks.ConnectionP2P;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class SendReceive extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private BufferedReader in;
    private PrintWriter out;
    private MessageHandler messageHandler = null;
    private int typeMessage;

    public SendReceive(Socket skt){
        socket = skt;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);
            in = new BufferedReader(new InputStreamReader(dataInputStream));
            out = new PrintWriter(dataOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getLocalIP() {
        if(socket != null) return socket.getLocalAddress().getHostAddress();
        return "NONE";
    }

    public void setTypeMessage(int typeMessage) {
        this.typeMessage = typeMessage;
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[105];
        int bytes;
        while (socket != null){
            try {
//                bytes = inputStream.read(buffer);
//                if(bytes > 0 && messageHandler != null){
//                    messageHandler.onReceiveSmg(bytes, buffer, typeMessage);
//                }

                String str = in.readLine();
                if(str!= null && !str.isEmpty()  && messageHandler != null){
                    messageHandler.onReceiveSmg(str.getBytes().length, str.getBytes(), typeMessage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObjectJSON(String json){
        out.println(json);
        out.flush();
    }
}
