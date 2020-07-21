package com.tb.tanks.tankGame.screens;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.erz.joysticklibrary.JoyStick;
import com.tb.tanks.ConnectionP2P.MessageHandler;
import com.tb.tanks.ConnectionP2P.SendReceive;
import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Input.KeyEvent;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.framework.input.JoyStickEvent;
import com.tb.tanks.physic.RecBody2D;
import com.tb.tanks.tankGame.core.GameLoader;
import com.tb.tanks.tankGame.core.GameRenderer;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.tile.GameTile;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.tank.Bullet;
import com.tb.tanks.tankGame.objects.tank.Tank;
import com.tb.tanks.tankGame.util.GameState;
import com.tb.tanks.tankGame.util.PlayerDefine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_COLLISION_BULLETS_TILES;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_GAME_UPDATE;

public class WorldScreen extends Screen {

    private Tank tank;
    private Tank tankOther;
    private TileMap map;
    private TileMap backgroundMap;
    private TileMap foregroundMap;
    private GameRenderer renderer;
    public GameLoader gameLoader;
    public int period = 20;
    Paint paint, paint2;
    Bitmap frameBuffer;
    /**
     * to simulate key right key left using accelerometer
     */
    private int eventID = 0;
    private Canvas gameCanvas;
    private boolean isSystemDriven = false;
    /**
     * time in seconds
     */
    private boolean lockUpdates = true;
    private boolean lockInputs = true;
    private int blink = 0;
    private int[] worldLocations;
    private SendReceive sendReceive;

    private float xOther;
    private float yOther;
    private float degreeOther;

    private GameState gameState;



    public WorldScreen(Game game) {
        super(game);
        frameBuffer = ((AndroidGame) game).getBuffer();
        gameCanvas = new Canvas(frameBuffer);
        // Initialize game objects here
        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(12);
        paint.setTextAlign(Paint.Align.CENTER);
        // paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        // paint.setFilterBitmap(true);

        paint2 = new Paint();
        paint2.setTextSize(60);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.WHITE);
        //Settings.loadPreferences((((AndroidGame)game)).getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, 0));


        if (game.getWifiManagerP2P().getHost() != null) {
            if (game.getWifiManagerP2P().getHost().getState() == Thread.State.NEW) {
                game.getWifiManagerP2P().getHost().start();
            }

            while (sendReceive == null) {
                sendReceive = game.getWifiManagerP2P().getHost().getSendReceive();
                if (sendReceive != null) {
                    sendReceive.setMessageHandler(messageClientHandler);
                }
            }
        }

        loadGame();
        lockUpdates = false;
        lockInputs = false;
        Settings.world = 1;
        Settings.level = 0;
        RecBody2D.setIsDrawBody(false);

        gameState = new GameState();

        if (game.getWifiManagerP2P().getServer() != null) {
            game.getWifiManagerP2P().getServer().getUpdateGameState().start();
        }

        ((AndroidGame) game).getFireButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tank != null)
                    tank.setHasFire(true);
            }
        });
    }


    MessageHandler messageClientHandler = new MessageHandler() {
        @Override
        public void onReceiveSmg(int size, byte[] buff, int type) {
            byte[] readBuff = buff;
            String temMsg = new String(readBuff, 0, size);
            try {
                JSONObject json = new JSONObject(temMsg);
                if (json == null) return;
                type = json.getInt("TYPE_MESSAGE");
                switch (type) {
                    case MESSAGE_GAME_UPDATE:
                        if (gameState != null)
                            gameState.processGameUpdate(json);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onReceiveSmg(String json) {
            String temMsg = new String(json);
            try {
                JSONObject jsonOBJ = new JSONObject(temMsg);
                int type = jsonOBJ.getInt("TYPE_MESSAGE");
                if (jsonOBJ == null) return;
                switch (type) {
                    case MESSAGE_GAME_UPDATE:
                        gameState.processGameUpdate(jsonOBJ);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void loadGame() {
        lockUpdates = true;

        try {
            gameLoader = new GameLoader((AndroidGame) game);
            renderer = new GameRenderer();
            renderer.setDrawHudEnabled(false);
            TankResourceManager.loadBackground(gameLoader.getBackGroundImageIndex());
            renderer.setBackground(TankResourceManager.Background);
            map = gameLoader.loadMap("maps/map4.txt",
                    ((TankGame) game).soundManager); // use the ResourceManager

            Settings.resetScores();
            tank = new Tank(((TankGame) game).soundManager);

            tankOther = new Tank(((TankGame) game).soundManager);

            tank.setPlayerID(sendReceive.getLocalIP());
            if(game.getWifiManagerP2P().isHost()){
                tank.setX(1200);
                tank.setY(1200);
            }
            else{
                tank.setX(4800);
                tank.setY(4600);
            }


            tankOther.setX(-3000);
            tankOther.setY(-3000);

            sendReceive.writeObjectJSON(tank.jsonToSendAddPlayer());

            eventID = 0;
            map.setPlayer(tank); // set the games main player to mario
            map.setPlayerOther(tankOther);
            worldLocations = new int[map.creatures().size()];
            for (int i = 0; i < map.creatures().size(); i++) {
                worldLocations[i] = (int) map.creatures().get(i).getX();
            }

        } catch (IOException e) {
            System.out.println("Invalid Map.");
            Log.e("Errrr", "invalid map");
        }
        lockUpdates = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){

                }
            }
        });
    }


    @Override
    public void update(float deltaTime) {
        if (((AndroidGame) game).isScreenTransitionActive()) return;
        if (lockUpdates) return;
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        updateRunning(touchEvents, deltaTime);
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        JoyStickEvent event = ((AndroidGame) game).getJoyStickEvent();

        boolean isTankNotMove = CollisionTileAndTankPlayer();
        if(!isTankNotMove){
            isTankNotMove = CollisionTankPlayerAndTankPlayerOther() && tankOther.isAlive();
        }
        CollisionTileAndTankPlayerBullets();
        CollisionTankOtherAndBullets();
        CollisionTankAndBulletsOther();
        CollisionTileAndTankPlayerOtherBullets();

        JSONObject js = gameState.getCurrentState();
        //System.out.println(js.toString());
        if (tank.getHasFire()) {
            sendReceive.writeObjectJSON(tank.fire());
            tank.setHasFire(false);
        }
        if (event.direction != JoyStick.DIRECTION_CENTER && tank.isAlive()) {
            float powerToSend = 0.0f;
            try {
                tank.setDegree((float) ((JSONObject) js.get("me")).getDouble("degree"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!isTankNotMove) {
                powerToSend = (float) event.power;
                try {
                    tank.setX((float) ((JSONObject) js.get("me")).getDouble("x"));
                    tank.setY((float) ((JSONObject) js.get("me")).getDouble("y"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            sendReceive.writeObjectJSON(tank.jsonToSendUpdatePlayer(event.angle, powerToSend, isTankNotMove));
        } else {
            sendReceive.writeObjectJSON(tank.jsonToSendUpdatePlayer(Math.toRadians(tank.getDegree()) + Math.PI / 2, 0, isTankNotMove));
        }

        try {
            if (((JSONObject) js.get("me")).has("bullets")){
                tank.updateBullets(((JSONObject) js.get("me")).getJSONArray("bullets"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray obj = js.getJSONArray("others");
            if (obj.length() > 0) {
                tankOther.setX((float) obj.getJSONObject(0).getDouble("x"));
                tankOther.setY((float) obj.getJSONObject(0).getDouble("y"));
                tankOther.setDegree((float) obj.getJSONObject(0).getDouble("degree"));
                tankOther.setHealth(obj.getJSONObject(0).getInt("heath"));
                if (obj.getJSONObject(0).has("bullets")) {
                    tankOther.updateBullets(obj.getJSONObject(0).getJSONArray("bullets"));
                    for(Bullet bll:tankOther.getBullets()){
                        if(!bll.isBeforeVisible() && bll.isVisible()) tankOther.setHasFire(true);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tank.update(map, period);
        tankOther.update(map, period);
    }

    private boolean CollisionTileAndTankPlayer() {
        boolean isTankNotMove = false;
        GameTile[][] tiles = map.getTiles();
        int sizei = tiles.length;
        for (int i = 0; i < sizei; i++) {
            int sizej = tiles[i].length;
            for (int j = 0; j < sizej; j++) {
                GameTile tile = map.getTiles()[i][j];

                if (tile != null) {
                    if (tile.getBody2D() != null && !isTankNotMove) {
                        isTankNotMove = RecBody2D.CheckCollision(tile.getBody2D(), tank.getBodyToMove2D());
                    }
                }
                if (isTankNotMove) {
                    return isTankNotMove;
                }
            }
        }
        return isTankNotMove;
    }

    private boolean CollisionTankPlayerAndTankPlayerOther() {
        return RecBody2D.CheckCollision(tankOther.getBodyToHit2D(), tank.getBodyToMove2D());
    }

    private void CollisionTileAndTankPlayerBullets() {
        GameTile[][] tiles = map.getTiles();
        int sizei = tiles.length;
        for (int i = 0; i < sizei; i++) {
            int sizej = tiles[i].length;
            for (int j = 0; j < sizej; j++) {
                GameTile tile = map.getTiles()[i][j];
                if (tile != null) {
                    if (tile.getBody2D() != null) {
                        int k = 0;
                        for (Bullet bll : tank.getBullets()) {
                            if (bll.isVisible() && RecBody2D.CheckCollision(tile.getBody2D(), bll.getBodyToHit2D())) {
                                bll.setVisible(false);
                                bll.getFireShotImpact().setX(bll.getX());
                                bll.getFireShotImpact().setY(bll.getY());
                                bll.getFireShotImpact().setVisible(true);
                                sendReceive.writeObjectJSON("{playerID: " + tank.getPlayerID() + ", index:" + k + ", TYPE_MESSAGE: " + MESSAGE_COLLISION_BULLETS_TILES + "}");
                            }
                            k++;
                        }
                    }
                }

            }
        }
    }

    private void CollisionTileAndTankPlayerOtherBullets() {
        GameTile[][] tiles = map.getTiles();
        int sizei = tiles.length;
        for (int i = 0; i < sizei; i++) {
            int sizej = tiles[i].length;
            for (int j = 0; j < sizej; j++) {
                GameTile tile = map.getTiles()[i][j];
                if (tile != null) {
                    if (tile.getBody2D() != null) {
                        int k = 0;
                        for (Bullet bll : tankOther.getBullets()) {
                            if (bll.isVisible() && RecBody2D.CheckCollision(tile.getBody2D(), bll.getBodyToHit2D())) {
                                bll.getFireShotImpact().setX(bll.getX());
                                bll.getFireShotImpact().setY(bll.getY());
                                bll.getFireShotImpact().setVisible(true);
                            }
                            k++;
                        }
                    }
                }

            }
        }
    }

    private void CollisionTankOtherAndBullets() {
        if (tankOther.getBodyToHit2D() != null && tankOther.isAlive()) {
            int k = 0;
            for (Bullet bll : tank.getBullets()) {
                if (bll.isVisible() && RecBody2D.CheckCollision(tankOther.getBodyToHit2D(), bll.getBodyToHit2D())) {
                    bll.setVisible(false);
                    bll.getFireShotImpact().setX(bll.getX());
                    bll.getFireShotImpact().setY(bll.getY());
                    bll.getFireShotImpact().setVisible(true);
                    sendReceive.writeObjectJSON("{playerID: " + tank.getPlayerID() + ", index:" + k + ", TYPE_MESSAGE: " + MESSAGE_COLLISION_BULLETS_TILES + "}");
                }
                k++;
            }
        }
    }

    private void CollisionTankAndBulletsOther() {
        if (tank.getBodyToHit2D() != null && tank.isAlive()) {
            int k = 0;
            for (Bullet bll : tankOther.getBullets()) {
                if (bll.isVisible() && RecBody2D.CheckCollision(tank.getBodyToHit2D(), bll.getBodyToHit2D())) {
                    bll.getFireShotImpact().setX(bll.getX());
                    bll.getFireShotImpact().setY(bll.getY());
                    bll.getFireShotImpact().setVisible(true);
                    tank.getsDamaged(PlayerDefine.PLAYER_DAMGE);
                    sendReceive.writeObjectJSON(tank.jsonToSendPlayerHeath());
                }
                k++;
            }
        }
    }


    @Override
    public void paint(float deltaTime) {
        drawRunningUI();
    }

    private void goToMenu() {
        GuiMenuScreen mainMenuScreen = new GuiMenuScreen(game);
        ((AndroidGame) game).setScreenWithFade(mainMenuScreen);
    }

    private void drawRunningUI() {
        renderer.draw(gameCanvas, map, backgroundMap, foregroundMap,
                frameBuffer.getWidth(), frameBuffer.getHeight());

        //GameRenderer.drawStringDropShadowAsEntity(gameCanvas, "WORLD-1",  worldLocations[0]+8, 100,0,0);
        //GameRenderer.drawStringDropShadowAsEntity(gameCanvas, "WORLD-2",  worldLocations[1]+8, 100,0,0);
        //GameRenderer.drawStringDropShadowAsEntity(gameCanvas, "WORLD-3",  worldLocations[2]+8, 100,0,0);
        //GameRenderer.drawStringDropShadowAsEntity(gameCanvas, "WORLD-4",  worldLocations[3]+8, 100,0,0);

        blink++;
        if (blink >= 30) {
            //GameRenderer.drawStringDropShadowAsHud(gameCanvas, "GET THE STAR TO ENTER......... ",frameBuffer.getWidth()/2, 16,0,0);
        }
        if (blink == 80) blink = 0;
    }


    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackPressed() {
        goToMenu();
        //showControls_SetUp_Dialog();
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width,
                             int height) {

        if (event.x > x && event.x < x + width - 1 && event.y > y
                && event.y < y + height - 1)
            return true;
        else
            return false;
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    private void processTouchEvent(TouchEvent event) {
        if (!Settings.mUseOnScreenControls) {
            if (event.type == TouchEvent.TOUCH_DOWN) {

            }
            return;
        }


        KeyEvent keyEvent = new KeyEvent();

        if (inBounds(event, 5, game.getScreenHeight() - 54, 48, 48)) {
            keyEvent.keyCode = android.view.KeyEvent.KEYCODE_DPAD_LEFT;
        } else if (inBounds(event, 60, game.getScreenHeight() - 54, 48, 48)) {
            keyEvent.keyCode = android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
        } else if (inBounds(event, game.getScreenWidth() - 108, game.getScreenHeight() - 54, 48, 48)) {
            keyEvent.keyCode = android.view.KeyEvent.KEYCODE_S;
        } else if (inBounds(event, game.getScreenWidth() - 53, game.getScreenHeight() - 54, 48, 48)) {
            keyEvent.keyCode = android.view.KeyEvent.KEYCODE_SPACE;
        } else {
            //mario.processEvent(0);
        }

        if (event.type == TouchEvent.TOUCH_DOWN) {

        } else if (event.type == TouchEvent.TOUCH_UP) {

        }
    }


}