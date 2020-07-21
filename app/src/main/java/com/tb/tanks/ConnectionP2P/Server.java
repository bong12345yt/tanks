package com.tb.tanks.ConnectionP2P;

import com.tb.tanks.ConnectionP2P.ObjectInfo.PlayerInfo;
import com.tb.tanks.tankGame.particles.FireShotFlame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server extends Thread {
    Socket socket;
    ServerSocket serverSocket;
    public HashMap<String, SendReceive> sendReceives = new HashMap<>();
    private UpdateGameState updateGameState = null;
    private HashMap<String, PlayerInfo> players = new HashMap<String, PlayerInfo>();

    MessageHandler messageServerHandler = new MessageHandler() {
        @Override
        public void onReceiveSmg(int size, byte[] buff, int type) {
            byte[] readBuff = buff;
            String temMsg = new String(readBuff, 0, size);
            try {
                JSONObject json = new JSONObject(temMsg);
                type = json.getInt("TYPE_MESSAGE");
                String playerId = json.getString("playerID");

                switch (type) {
                    case P2PMessage.MESSAGE_TANK_ADD_PLAYER: {
                        PlayerInfo player = players.get(playerId);
                        player.setX((float) json.getDouble("x"));
                        player.setY((float) json.getDouble("y"));
                        player.setWidth(json.getInt("width"));
                        player.setHeight(json.getInt("height"));
                        player.setDegree((float) json.getDouble("degree"));
                        player.setAdded(true);
                        break;
                    }
                    case P2PMessage.MESSAGE_PLAYER_INPUT_MOVE: {
                        PlayerInfo player = players.get(playerId);
                        if (player != null) {
                            player.setAngle((float) json.getDouble("angle"));
                            player.setPower((float) json.getDouble("power"));
                            //player.setNotMove(json.getBoolean("isNotMove"));
                        }
                        break;
                    }
                    case P2PMessage.MESSAGE_TANK_PLAYER_HEATH: {
                        PlayerInfo player = players.get(playerId);
                        if (player != null) {
                            player.setHeath(json.getInt("heath"));
                        }
                        break;
                    }
                    case P2PMessage.MESSAGE_PLAYER_UPDATE_NOT_MOVE: {
                        PlayerInfo player = players.get(playerId);
                        if (player != null) {
                            player.setNotMove(json.getBoolean("isNotMove"));
                        }
                        break;
                    }
                    case P2PMessage.MESSAGE_PLAYER_INPUT_FIRE: {
                        PlayerInfo player = players.get(playerId);
                        if (player != null) {
                            if(json.getBoolean("isFire"))
                                player.fire();
                        }
                        break;
                    }
                    case P2PMessage.MESSAGE_COLLISION_BULLETS_TILES: {
                        PlayerInfo player = players.get(playerId);
                        if (player != null) {
                            player.eraseBulletWhenCollision(json.getInt("index"));
                        }
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onReceiveSmg(String json) {

        }
    };

    public void setUpdateGameState(com.tb.tanks.ConnectionP2P.UpdateGameState updateGameState) {
        this.updateGameState = updateGameState;
    }

    public Thread getUpdateGameState() {
        return UpdateGameState;
    }

    Thread UpdateGameState = new Thread(new Runnable() {
        private long lastUpdateTime = 0;
        public static final float FRAME_UPDATE = 1000/60;
        @Override
        public void run() {
            while (true) {

                long now = System.nanoTime();
                float dt = (float) (now - lastUpdateTime) / 1000000;
                lastUpdateTime = now;

                if (updateGameState != null)
                    updateGameState.updateState();
                for (PlayerInfo player : players.values()) {
                    player.update(dt/1000);
                }

                //send to client
                if (players.size() > 0) {
                    Object[] playerInfos = players.values().toArray();
                    for (int i = playerInfos.length - 1; i >= 0; i--) {
                        PlayerInfo player = (PlayerInfo) playerInfos[i];
                        if (player.isAdded())
                            sendReceives.get(player.getPlayerID()).writeObjectJSON(createUpdateString(player));
                    }
                }
                float wait = FRAME_UPDATE - dt;
                //System.out.println("Bong wait: " + (long) wait + " - " + dt);
                //if(wait > 0) {
                    try {
                        Thread.sleep((long) FRAME_UPDATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
               // }

            }
        }
    });

    public byte[] createUpdate(PlayerInfo me) {
        JSONArray others = new JSONArray();
        for (PlayerInfo player : players.values()) {
            if (player.getPlayerID() != me.getPlayerID()) {
                others.put(player.serializeForUpdate());
            }
        }
        JSONObject json = new JSONObject();
        try {
            json.put("time", System.nanoTime());
            json.put("me", me.serializeForUpdate());
            json.put("others", others);
            json.put("TYPE_MESSAGE", P2PMessage.MESSAGE_GAME_UPDATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString().getBytes();
    }

    public String createUpdateString(PlayerInfo me) {
        JSONArray others = new JSONArray();
        for (PlayerInfo player : players.values()) {
            if (player.getPlayerID() != me.getPlayerID()) {
                others.put(player.serializeForUpdate());
            }
        }
        JSONObject json = new JSONObject();
        try {
            json.put("time", System.nanoTime());
            json.put("me", me.serializeForUpdate());
            json.put("others", others);
            json.put("TYPE_MESSAGE", P2PMessage.MESSAGE_GAME_UPDATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public HashMap<String, SendReceive> getSendReceives() {
        return sendReceives;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
                SendReceive sendReceive = new SendReceive(socket);
                sendReceives.put(socket.getInetAddress().getHostAddress(), sendReceive);
                sendReceive.setMessageHandler(messageServerHandler);
                PlayerInfo player = new PlayerInfo();
                player.setPlayerID(socket.getInetAddress().getHostAddress());
                players.put(player.getPlayerID(), player);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
