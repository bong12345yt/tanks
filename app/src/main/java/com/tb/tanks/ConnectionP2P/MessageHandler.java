package com.tb.tanks.ConnectionP2P;

public interface MessageHandler {
    public void onReceiveSmg(int size, byte[] buff, int type);
    public void onReceiveSmg(String json);
}
