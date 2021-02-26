package com.tb.tanks.tankGame.screens;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import com.erz.joysticklibrary.JoyStick;
import com.tb.tanks.ConnectionP2P.MessageHandler;
import com.tb.tanks.ConnectionP2P.P2PConnectionListener;
import com.tb.tanks.ConnectionP2P.P2PMessage;
import com.tb.tanks.ConnectionP2P.SendReceive;
import com.tb.tanks.R;
import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Input.KeyEvent;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.framework.input.JoyStickEvent;
import com.tb.tanks.gui.AndroidDialog;
import com.tb.tanks.gui.AndroidImageButton;
import com.tb.tanks.gui.AndroidText;
import com.tb.tanks.gui.Component;
import com.tb.tanks.gui.ComponentClickListener;
import com.tb.tanks.gui.GUIResourceManager;
import com.tb.tanks.gui.MiniMap;
import com.tb.tanks.gui.ScoreBoard;
import com.tb.tanks.physic.RecBody2D;
import com.tb.tanks.tankGame.core.GameLoader;
import com.tb.tanks.tankGame.core.GameRenderer;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.tile.GameTile;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.tank.Bullet;
import com.tb.tanks.tankGame.objects.tank.Tank;
import com.tb.tanks.tankGame.util.GameState;
import com.tb.tanks.tankGame.util.PlayerDefine;
import com.tb.tanks.tankGame.util.Utils;

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
    private AndroidDialog dlgYouWin;
    private AndroidDialog dlgYouLose;
    private AndroidDialog dlgExitBatle;

    private GameState gameState;
    boolean isOtherDeviceDisconneted = false;
    private MiniMap miniMap;
    private ScoreBoard scoreBoard;
    private float WEAPON_SPEED = 0.03f;
    private float saveWeaponDegree = 0;
    private float deltaDegree = 0;
    private float eventDegree = 90;
    final static private long timeFire = 400;
    private long startFire = 0;


    public WorldScreen(final Game game) {
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

        if (game.getWifiManagerP2P().getClient() != null) {
            if (game.getWifiManagerP2P().getClient().getState() == Thread.State.NEW) {
                game.getWifiManagerP2P().getClient().start();
            }

            while (sendReceive == null) {
                sendReceive = game.getWifiManagerP2P().getClient().getSendReceive();
                if (sendReceive != null) {
                    sendReceive.setMessageHandler(messageClientHandler);
                }
            }
        }

        //loadGame();
        lockUpdates = false;
        lockInputs = false;
        Settings.world = 1;
        Settings.level = 0;
        RecBody2D.setIsDrawBody(false);

        gameState = new GameState();

        ((AndroidGame) game).getFireButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tank != null){
                    long now = System.currentTimeMillis();
                    long deltaFire = now - startFire;
                    if( deltaFire > timeFire) {
                        startFire = now;
                        Thread send = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendReceive.writeObjectJSON(tank.fire());
                            }
                        });
                        send.start();
                    }
                }
            }
        });


        AndroidImageButton btnMainMenu = new AndroidImageButton("", (GUIResourceManager.you_win.getWidth() - GUIResourceManager.menu_normal.getWidth()) / 2,
                (GUIResourceManager.you_win.getHeight() * 3 / 4 - GUIResourceManager.menu_normal.getHeight() + 10),
                GUIResourceManager.menu_normal.getWidth(), GUIResourceManager.menu_normal.getHeight());
        btnMainMenu.setBackgroundNormal(GUIResourceManager.menu_normal);
        btnMainMenu.setBackgroundFocused(GUIResourceManager.menu_focus);

        btnMainMenu.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                ((AndroidGame) game).ShowJoyStick(false);
                ((AndroidGame) game).ShowFireButton(false);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendReceive.writeObjectJSON("{playerID: " + tank.getPlayerID() + ", TYPE_MESSAGE: " + P2PMessage.MESSAGE_DISCONNECT + "}");
                    }
                });
                thread.start();


                game.getWifiManagerP2P().disconnect();
                goToMenu();
            }
        });

        AndroidImageButton btnMainMenu1 = new AndroidImageButton("", (GUIResourceManager.you_win.getWidth() - GUIResourceManager.menu_normal.getWidth()) / 2,
                (GUIResourceManager.you_win.getHeight() * 3 / 4 - GUIResourceManager.menu_normal.getHeight() + 10),
                GUIResourceManager.menu_normal.getWidth(), GUIResourceManager.menu_normal.getHeight());
        btnMainMenu1.setBackgroundNormal(GUIResourceManager.menu_normal);
        btnMainMenu1.setBackgroundFocused(GUIResourceManager.menu_focus);

        btnMainMenu1.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                ((AndroidGame) game).ShowJoyStick(false);
                ((AndroidGame) game).ShowFireButton(false);
                ((AndroidGame) game).ShowJoyStickWeapon(false);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendReceive.writeObjectJSON("{playerID: " + tank.getPlayerID() + ", TYPE_MESSAGE: " + P2PMessage.MESSAGE_DISCONNECT + "}");
                    }
                });
                thread.start();

                game.getWifiManagerP2P().disconnect();
                goToMenu();
            }
        });


        dlgExitBatle = new AndroidDialog((game.getScreenWidth() - GUIResourceManager.bg_free.getWidth()) / 2, (game.getScreenHeight() - GUIResourceManager.bg_free.getHeight()) / 2, GUIResourceManager.bg_free.getWidth(), GUIResourceManager.bg_free.getHeight());
        dlgExitBatle.setBackgroundNormal(GUIResourceManager.bg_free);
        dlgExitBatle.setVisible(false);
        AndroidText lblMessageExitBatle = new AndroidText(((AndroidGame) game).getResources().getString(R.string.game_dialog_exit_batle), (dlgExitBatle.getWidth() - 450) / 2, dlgExitBatle.getHeight() / 6, 100, 100);
        lblMessageExitBatle.setColor(Color.RED);
        lblMessageExitBatle.setTextSize(50);
        AndroidImageButton btn_ok = new AndroidImageButton("OK", 50, dlgExitBatle.getHeight() * 4 / 6 + 30, GUIResourceManager.btn_free_normal.getWidth(), GUIResourceManager.btn_free_normal.getHeight());
        btn_ok.setBackgroundNormal(GUIResourceManager.btn_free_normal);
        btn_ok.setBackgroundFocused(GUIResourceManager.btn_free_focus);
        btn_ok.setTextSize(40);
        btn_ok.setTextColor(Color.WHITE);
        btn_ok.setTextY(-5);
        btn_ok.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                tank.setHealth(0);
                sendReceive.writeObjectJSON(tank.jsonToSendPlayerHeath());
                dlgExitBatle.setVisible(false);
            }
        });

        AndroidImageButton btn_cancel = new AndroidImageButton("Cancel", dlgExitBatle.getWidth() - GUIResourceManager.btn_free_normal.getWidth() - 50, dlgExitBatle.getHeight() * 4 / 6 + 30, GUIResourceManager.btn_free_normal.getWidth(), GUIResourceManager.btn_free_normal.getHeight());
        btn_cancel.setBackgroundNormal(GUIResourceManager.btn_free_normal);
        btn_cancel.setBackgroundFocused(GUIResourceManager.btn_free_focus);
        btn_cancel.setTextSize(40);
        btn_cancel.setTextColor(Color.WHITE);
        btn_cancel.setTextY(-5);
        btn_cancel.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                dlgExitBatle.setVisible(false);
            }
        });

        dlgYouWin = new AndroidDialog((game.getScreenWidth() - GUIResourceManager.you_win.getWidth()) / 2, (game.getScreenHeight() - GUIResourceManager.you_win.getHeight()) / 2, GUIResourceManager.you_win.getWidth(), GUIResourceManager.you_win.getHeight());
        dlgYouWin.setBackgroundNormal(GUIResourceManager.you_win);
        dlgYouWin.setY(-GUIResourceManager.you_win.getHeight() / 2);
        dlgYouWin.setVisible(false);
        dlgYouLose = new AndroidDialog((game.getScreenWidth() - GUIResourceManager.you_lose.getWidth()) / 2, (game.getScreenHeight() - GUIResourceManager.you_lose.getHeight()) / 2, GUIResourceManager.you_lose.getWidth(), GUIResourceManager.you_lose.getHeight());
        dlgYouLose.setBackgroundNormal(GUIResourceManager.you_lose);
        dlgYouLose.setY(-GUIResourceManager.you_win.getHeight() / 2);
        dlgYouLose.setVisible(false);

        dlgYouWin.addComponent(btnMainMenu);
        dlgYouLose.addComponent(btnMainMenu1);
        dlgExitBatle.addComponent(lblMessageExitBatle);
        dlgExitBatle.addComponent(btn_ok);
        dlgExitBatle.addComponent(btn_cancel);


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
            game.getWifiManagerP2P().setReciverP2PConnectionListener(new P2PConnectionListener() {
                @Override
                public void onConnect(boolean isError) {

                }

                @Override
                public void onDisconnect() {
                    if (game.getWifiManagerP2P().isServer()) {
                        game.getWifiManagerP2P().getServer().isRun = false;
                    }
                    isOtherDeviceDisconneted = true;
                }
            });
            gameLoader = new GameLoader((AndroidGame) game);
            renderer = new GameRenderer();
            renderer.setDrawHudEnabled(false);
            TankResourceManager.loadBackground(gameLoader.getBackGroundImageIndex());
            renderer.setBackground(TankResourceManager.Background);
            map = gameLoader.loadMap("maps/map4.txt",
                    ((TankGame) game).soundManager); // use the ResourceManager
            Settings.resetScores();
            Settings.loadPreferences((AndroidGame) game);
            if (Settings.musicEnabled) {
                ((TankGame) game).soundManager.loadGameMusic();
                TankSoundManager.setMusicVolume(Settings.musicVolume / 100.0f);
            }

            miniMap = new MiniMap(20, 20, GUIResourceManager.background_mini_map.getWidth(), GUIResourceManager.background_mini_map.getHeight());
            if (((AndroidGame) game).hasCutout()) {
                Point standard = ((AndroidGame) game).m_cutoutHelper.standardizeToSafeX(miniMap.getX(), miniMap.getY());
                miniMap.setX(standard.x);
                miniMap.setY(standard.y);
            }
            miniMap.setRealMapWidth(gameLoader.getRealWidth());
            miniMap.setRealMapHeight(gameLoader.getRealHeight());
            miniMap.setRealMapPos(gameLoader.getRealPos());

            scoreBoard = new ScoreBoard(game.getScreenWidth() - GUIResourceManager.score_board_bg.getWidth() - 20, 20, GUIResourceManager.score_board_bg.getWidth(), GUIResourceManager.score_board_bg.getHeight());

            tank = new Tank(((TankGame) game).soundManager);
            saveWeaponDegree = tank.getWeapon().getDegree() + 90;

            tankOther = new Tank(((TankGame) game).soundManager);
            tankOther.setIdleTank(TankResourceManager.TankOther);
            tankOther.getWeapon().setIdleWeapon(TankResourceManager.WeaponOther);

            tank.setPlayerID(sendReceive.getLocalIP());
            if (game.getWifiManagerP2P().isServer()) {
                tank.setX(1200);
                tank.setY(1200);
            } else {
                tank.setX(4800);
                tank.setY(4600);
            }

            tankOther.setX(-3000);
            tankOther.setY(-3000);

            miniMap.addPlayer("me", tank);
            miniMap.addPlayer("other", tankOther);

            scoreBoard.addPlayer("me", tank);
            scoreBoard.addPlayer("other", tankOther);


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
    }

    public void affterLoadGame() {
        ((AndroidGame) game).ShowJoyStick(true);
        ((AndroidGame) game).ShowFireButton(true);
        ((AndroidGame) game).ShowJoyStickWeapon(true);

    }


    @Override
    public void update(float deltaTime) {
        if (((AndroidGame) game).isScreenTransitionActive()) return;
        if (lockUpdates) return;
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        updateRunning(touchEvents, deltaTime);
        if (touchEvents == null || touchEvents.size() == 0) return;
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            dlgYouWin.processEvent(event);
            dlgYouLose.processEvent(event);
            dlgExitBatle.processEvent(event);
        }
        updateRunning(touchEvents, deltaTime);
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        JoyStickEvent event = ((AndroidGame) game).getJoyStickEvent();
        JoyStickEvent eventWeapon = ((AndroidGame) game).getJoyStickEventWeapon();

        boolean isTankNotMove = CollisionTileAndTankPlayer();
        if (!isTankNotMove) {
            isTankNotMove = CollisionTankPlayerAndTankPlayerOther() && tankOther.isAlive();
        }

        CollisionTileAndTankPlayerBullets();
        CollisionTankOtherAndBullets();
        CollisionTankAndBulletsOther();
        CollisionTileAndTankPlayerOtherBullets();

        JSONObject js = gameState.getCurrentState();
        //System.out.println(js.toString());
        if (tank.getHasFire()) {
            tank.setHasFire(false, false);
        }
        if (event.direction != JoyStick.DIRECTION_CENTER && tank.isAlive()) {
            float powerToSend = 0.0f;
            if (!isTankNotMove) {
                powerToSend = (float) event.power;
            }
            eventDegree = (float) Math.toDegrees(event.angle);
            sendReceive.writeObjectJSON(tank.jsonToSendUpdatePlayer(event.angle, powerToSend, isTankNotMove));
            if (eventWeapon.direction == JoyStick.DIRECTION_CENTER) {
                saveWeaponDegree = (float) (Math.toDegrees(event.angle) + deltaDegree);
                float realAngle = (float) Math.toRadians(saveWeaponDegree);
                sendReceive.writeObjectJSON(tank.jsonToSendPlayeWeapon(realAngle));
            }

        } else {
            sendReceive.writeObjectJSON(tank.jsonToSendUpdatePlayerPower( 0));
        }

        if (eventWeapon.direction != JoyStick.DIRECTION_CENTER && tank.isAlive()) {
            float realAngle = (float) Math.toRadians(saveWeaponDegree);

            float degreeStandard = (float) Math.toDegrees(eventWeapon.angle);

            float saveWeaponDegreeStandard = saveWeaponDegree;
            if (degreeStandard < 0) {
                degreeStandard = 360.f + degreeStandard;
            }
            if (saveWeaponDegreeStandard < 0) {
                saveWeaponDegreeStandard = 360.f + saveWeaponDegreeStandard;
            }

            float subD = (degreeStandard - saveWeaponDegreeStandard);
            float sub360 = 360.f - Math.abs(subD);
            if (degreeStandard > saveWeaponDegreeStandard) {
                sub360 = -sub360;
            }

            float standardD = Math.abs(sub360) < Math.abs(subD) ? sub360 : subD;
            saveWeaponDegree += (standardD) * WEAPON_SPEED;
            saveWeaponDegree %= 360;

            if (eventDegree < 0) {
                eventDegree += 360.f;
            }
            deltaDegree = saveWeaponDegree - eventDegree;

            sendReceive.writeObjectJSON(tank.jsonToSendPlayeWeapon(realAngle));
        } else {
            //sendReceive.writeObjectJSON(tank.jsonToSendPlayeWeapon(Math.toRadians(tank.getWeapon().getDegree()) + Math.PI / 2));
        }

        try {
            JSONObject me = ((JSONObject) js.get("me"));

            tank.setHealth(me.getInt("heath"));
            tank.setScore(me.getInt("score"));
            tank.setWeaponDegree((float) me.getDouble("degreeWeapon"));

            tank.setX((float) me.getDouble("x"));
            tank.setY((float) me.getDouble("y"));
            tank.setDegree((float) me.getDouble("degree"));
            if (me.has("bullets")) {
                tank.updateBullets(me.getJSONArray("bullets"), false);
                for (Bullet bll : tank.getBullets()) {
                    if (!bll.isBeforeVisible() && bll.isVisible()) {
                        tank.setHasFire(true, true);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray obj = js.getJSONArray("others");
            if (obj.length() > 0) {
                if (obj.getJSONObject(0).has("id"))
                    tankOther.setPlayerID(obj.getJSONObject(0).getString("id"));
                tankOther.setX((float) obj.getJSONObject(0).getDouble("x"));
                tankOther.setY((float) obj.getJSONObject(0).getDouble("y"));
                tankOther.setDegree((float) obj.getJSONObject(0).getDouble("degree"));
                tankOther.setHealth(obj.getJSONObject(0).getInt("heath"));
                tankOther.setScore(obj.getJSONObject(0).getInt("score"));
                tankOther.setWeaponDegree((float) obj.getJSONObject(0).getDouble("degreeWeapon"));
                if (obj.getJSONObject(0).has("bullets")) {
                    tankOther.updateBullets(obj.getJSONObject(0).getJSONArray("bullets"), true);
                    for (Bullet bll : tankOther.getBullets()) {
                        if (!bll.isBeforeVisible() && bll.isVisible()) {
                            tankOther.setHasFire(true, Utils.distance(tank.getX(), tank.getY(), tankOther.getX(), tankOther.getY()) <= Utils.HEAR_SOUND_RADIUS);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (tank.getHealth() <= 0 || !game.getWifiManagerP2P().IsWifiEnable()) {
            if (!dlgYouLose.isVisible()) {
                dlgYouLose.setVisible(true);
                ((AndroidGame) game).ShowJoyStick(false);
                ((AndroidGame) game).ShowFireButton(false);
                ((AndroidGame) game).ShowJoyStickWeapon(false);

            }
            dlgYouLose.updateShowVertical();
        } else if (tankOther.getHealth() <= 0 || (isOtherDeviceDisconneted && game.getWifiManagerP2P().IsWifiEnable())) {
            if (!dlgYouWin.isVisible()) {
                dlgYouWin.setVisible(true);
                ((AndroidGame) game).ShowJoyStick(false);
                ((AndroidGame) game).ShowFireButton(false);
                ((AndroidGame) game).ShowJoyStickWeapon(false);
            }
            dlgYouWin.updateShowVertical();
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
                                if (Utils.distance(tank.getX(), tank.getY(), bll.getX(), bll.getY()) <= Utils.HEAR_SOUND_RADIUS && !bll.isCollision())
                                    ((TankGame) game).soundManager.playBulletImpactTile();
                                bll.setCollision(true);
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
                                if (Utils.distance(tank.getX(), tank.getY(), bll.getX(), bll.getY()) <= Utils.HEAR_SOUND_RADIUS && !bll.isCollision())
                                    ((TankGame) game).soundManager.playBulletImpactTile();
                                bll.getFireShotImpact().setX(bll.getX());
                                bll.getFireShotImpact().setY(bll.getY());
                                bll.getFireShotImpact().setVisible(true);
                                bll.setCollision(true);
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
                    if (!bll.isCollision()) {
                        ((TankGame) game).soundManager.playBulletImpactTank();
                        int score = tank.getScore() + 10;
                        sendReceive.writeObjectJSON(tank.jsonToSendPlayeScore(score));
                    }
                    bll.setCollision(true);
                    bll.getFireShotImpact().setX(bll.getX());
                    bll.getFireShotImpact().setY(bll.getY());
                    bll.getFireShotImpact().setVisible(true);
                    sendReceive.writeObjectJSON("{playerID: " + tank.getPlayerID() + ", index:" + k + ", TYPE_MESSAGE: " + MESSAGE_COLLISION_BULLETS_TILES + "}");
                    tankOther.getsDamaged(PlayerDefine.PLAYER_DAMGE);
                    sendReceive.writeObjectJSON(tankOther.jsonToSendPlayerHeath());
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
                    if (!bll.isCollision())
                        ((TankGame) game).soundManager.playBulletImpactTank();
                    bll.getFireShotImpact().setX(bll.getX());
                    bll.getFireShotImpact().setY(bll.getY());
                    bll.getFireShotImpact().setVisible(true);
                    bll.setCollision(true);
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
        miniMap.draw(gameCanvas, 0, 0);
        scoreBoard.draw(gameCanvas, 0, 0);
        dlgYouWin.draw(gameCanvas, 0, 0);
        dlgYouLose.draw(gameCanvas, 0, 0);
        dlgExitBatle.draw(gameCanvas, 0, 0);


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
    public void onStop() {

    }

    @Override
    public void dispose() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        game.getWifiManagerP2P().disconnect();
    }

    @Override
    public void onBackPressed() {
        if (dlgExitBatle.isVisible()) {
            dlgExitBatle.setVisible(false);
        } else {
            dlgExitBatle.setVisible(true);
        }
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