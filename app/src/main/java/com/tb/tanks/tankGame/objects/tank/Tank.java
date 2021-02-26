package com.tb.tanks.tankGame.objects.tank;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.tb.tanks.physic.RecBody2D;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.animation.Sprite;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.tiles.HealthBar;
import com.tb.tanks.tankGame.particles.Explosion;
import com.tb.tanks.tankGame.particles.FireShotFlame;
import com.tb.tanks.tankGame.util.PlayerDefine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_PLAYER_FIRE_FLAME_HIDE;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_PLAYER_INPUT_FIRE;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_PLAYER_INPUT_MOVE;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_PLAYER_INPUT_POWER;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_TANK_ADD_PLAYER;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_TANK_PLAYER_HEATH;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_TANK_PLAYER_SCORE;
import static com.tb.tanks.ConnectionP2P.P2PMessage.MESSAGE_TANK_PLAYER_WEAPON;


/**
 * Mario is the main object in the game and is the center of the screen and attention at all
 * time. As a result, he is also the most complicated object in terms of animation, collision detection,
 * user input etc.
 */

public class Tank extends Sprite {

    /* Static Constant Fields.
     * Gravity:   Effects the amount of pull objects feel toward the ground. pixels/ms
     * Friction:  Effects the amount of sliding an object displays before coming to a stop.
     * S_X:       Starting X position of Mario.
     * S_Y:       Starting Y position of Mario.
     * S_DY:      Starting Dy of Mario.
     * S_JH:      Effects the height of Mario's first jump.
     * Anim_Time: The time between each of Mario's Animations.
     *
     * Terminal_Walking_Dx:  Max speed when Mario is walking.
     * Terminal_R3unning_Dx:  Max speed when Mario is running.
     * Terminal_Fall_Dy:     Max speed Mario can fall at.
     * Walking_Dx_Inc:       The increase in speed per update when walking until terminal runnning is reached.
     * Running_Dx_Inc:       The increase in speed per update when running until terminal walking is reached.
     * Start_Run_Anim_Thres: The speed where mario switches to the running animation.
     */

    private static final int STARTING_X = 25;
    private static final int STARTING_Y = 140;
    private static final float STARTING_DY = .03f;
    private static final int STARTING_LIFE = 3;
    private static final int ANIM_TIME = 125;
    private int score = 0;
    private Weapon weapon;
    public TileMap map;
    /*boolean variable to identify if fireball is to be fired*/

    private boolean isRotate = false;

    /* INITIAL_JUMP_HEIGHT + dx*JUMP_MULTIPLIER */

    /* Boolean variables used to identify which keys are pressed. */
    private boolean isDownHeld, isRightHeld, isLeftHeld, isShiftHeld, isSpaceHeld;
    /* Boolean variables used to identify where Mario is with respect to Platforms. */
    private boolean isRightOfPlatform, isLeftOfPlatform, isBelowPlatform, isAbovePlatform;
    /* Boolean variables used to identify where Mario is with respect to Slopes. */
    private boolean isUpSlope, isDownSlope, onSlopedTile;
    /* Boolean variables used to identify the state of Mario. */
    private boolean isJumping, frictionLock, isInvisible;

    /* Animation variables. */
    private Animation idleTank;

    private int health;
    /*boolean which keeps track of size of mario*/
    private boolean small = false;
    private boolean hasFire = false;
    private boolean isAlive = true;
    private RecBody2D bodyToMove2D;
    private RecBody2D bodyToHit2D;
    public boolean isLevelClear = false;
    private Stack<Bullet> bullets = new Stack<>();
    private ArrayList<FireShotFlame> fireShotFlames;
    private String playerID = "";


    private HealthBar healthBar;
    private Explosion explosion;

    private TankSoundManager soundManager;

    public Tank(TankSoundManager soundManager) {

        super(STARTING_X, STARTING_Y);
        dy = STARTING_DY;
        health = PlayerDefine.PLAYER_HEATH;

        this.soundManager = soundManager;

        this.setFirstDegree(90.f);
        //TankResourceManager.Tank = Bitmap.createScaledBitmap(TankResourceManager.Tank, 43, 64, true);
        PointF[] points = new PointF[4];
        points[0] = new PointF(-70, -130);
        points[1] = new PointF(70, -130);

        points[3] = new PointF(-35, 48);
        points[2] = new PointF(35, 48);

        bodyToMove2D = new RecBody2D(points, new PointF(STARTING_X, STARTING_Y), this.degree);

        PointF[] points2 = new PointF[4];
        points2[0] = new PointF(-65, -122);
        points2[1] = new PointF(65, -122);

        points2[3] = new PointF(-65, 66);
        points2[2] = new PointF(65, 66);

        bodyToHit2D = new RecBody2D(points2, new PointF(STARTING_X, STARTING_Y), this.degree);

        healthBar = new HealthBar(soundManager);

        healthBar.setMaxHealth(this.health);
        healthBar.setHealthCurrent(this.health);

        explosion = new Explosion(soundManager);

        fireShotFlames = new ArrayList<FireShotFlame>();

        this.setDegree(0.f);

        idleTank = new Animation(ANIM_TIME).addFrame(TankResourceManager.Tank);
        setAnimation(idleTank);

        weapon = new Weapon(soundManager);
        weapon.setX(this.x);
        weapon.setY(this.y);
        weapon.setDegree(degree);

        for (int i = 0; i < 20; i++) {
            Bullet bll = new Bullet(soundManager);
            bll.setDegree(degree);
            bll.setX(this.x);
            bll.setY(this.y);
            bullets.push(bll);
        }

        for (int i = 0; i < 10; i++) {
            FireShotFlame fireShotFlame = new FireShotFlame(soundManager);
            fireShotFlame.setDegree(degree);
            fireShotFlame.setX(this.x);
            fireShotFlame.setY(this.y);
            fireShotFlames.add(fireShotFlame);
        }
    }

    public void setIdleTank(Bitmap idleTankBM) {
        this.idleTank = new Animation(ANIM_TIME).addFrame(idleTankBM);
        setAnimation(this.idleTank);
    }

    public HealthBar getHealthBar() {
        return healthBar;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    public Stack<Bullet> getBullets() {
        return bullets;
    }

    public RecBody2D getBodyToMove2D() {
        return bodyToMove2D;
    }

    public RecBody2D getBodyToHit2D() {
        return bodyToHit2D;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
        for (Bullet bll : bullets) {
            bll.setId(playerID);
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<FireShotFlame> getFireShotFlames() {
        return fireShotFlames;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        bodyToMove2D.setParentX(x);
        bodyToHit2D.setParentX(x);
        healthBar.setX(x);
        explosion.setX(x);
        weapon.setX(x);

        for (FireShotFlame fireShotFlame : fireShotFlames) {
            //if (!fireShotFlame.isVisible()) {
            float rdi = (float) Math.toRadians(weapon.getDegree());
            float s = (float) Math.sin(rdi);
            float c = (float) Math.cos(rdi);
            float xnew = 0 * c + (getHeight() / 2 + 30) * s;
            fireShotFlame.setX(x + xnew);
            fireShotFlame.setDegree(weapon.getDegree());
            break;
            //}
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        bodyToMove2D.setParentY(y);
        bodyToHit2D.setParentY(y);
        healthBar.setY(y);
        explosion.setY(y);
        weapon.setY(y);

        for (FireShotFlame fireShotFlame : fireShotFlames) {
            //if (!fireShotFlame.isVisible()) {
            float rdi = (float) Math.toRadians(weapon.getDegree());
            float s = (float) Math.sin(rdi);
            float c = (float) Math.cos(rdi);
            float ynew = 0 * s - (getHeight() / 2 + 30) * c;
            fireShotFlame.setY(y + ynew);
            fireShotFlame.setDegree(weapon.getDegree());
            break;
            // }
        }

    }

    @Override
    public void setDegree(float degree) {
        super.setDegree(degree);
        bodyToMove2D.setAngle(degree);
        bodyToHit2D.setAngle(degree);
        explosion.setDegree(degree);

        for (FireShotFlame fireShotFlame : fireShotFlames) {
            //if (!fireShotFlame.isVisible()) {
            float rdi = (float) Math.toRadians(weapon.getDegree());
            float s = (float) Math.sin(rdi);
            float c = (float) Math.cos(rdi);
            float xnew = 0 * c + (getHeight() / 2 + 30) * s;
            float ynew = 0 * s - (getHeight() / 2 + 30) * c;
            fireShotFlame.setX(x + xnew);
            fireShotFlame.setY(y + ynew);
            fireShotFlame.setDegree(weapon.getDegree());
            break;
            //}
        }
    }

    public boolean isSmall() {
        return small;
    }

    public boolean getHasFire() {
        return hasFire;
    }

    public void setHasFire(boolean hasFire, boolean shouldPlaySound) {
        if (hasFire && isAlive) {
            this.hasFire = hasFire;
            for (FireShotFlame fireShotFlame : fireShotFlames) {
                if (!fireShotFlame.isVisible()) {
                    if (shouldPlaySound)
                        soundManager.playTankFire();
                    fireShotFlame.setVisible(true);
                    break;
                }
            }
        } else {
            this.hasFire = false;
        }
    }

    @Override
    public void draw(Canvas g, float x, float y, float offsetX, float offsetY) {
        if (isAlive) {
            draw(g, x + offsetX, y + offsetY);
            weapon.draw(g, x + offsetX, y + offsetY);
            bodyToMove2D.draw(g, x, y);
            bodyToHit2D.draw(g, x, y);
        }
    }

    public void drawFireShotFlames(Canvas g, float x, float y) {
        for (FireShotFlame fireShotFlame : fireShotFlames) {
            fireShotFlame.draw(g, x + fireShotFlame.getX(), y + fireShotFlame.getY());
        }
    }

    public void drawBullets(Canvas g, float x, float y) {
        for (Bullet bll : bullets) {
            bll.draw(g, x + bll.getX(), y + bll.getY());
            bll.getFireShotImpact().draw(g, x + bll.getFireShotImpact().getX(), y + bll.getFireShotImpact().getY());
        }
    }


    public void setIsRotate(boolean rotate) {
        this.isRotate = rotate;
    }

    public boolean isRotate() {
        return this.isRotate;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        healthBar.setHealthCurrent(health);
        if (health <= 0) {
            if (isAlive) {
                soundManager.playTankExplosion();
                explosion.setVisible(true);
            }
            isAlive = false;
        } else isAlive = true;
    }


    public void resetHealth() {
        this.health = STARTING_LIFE;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public String fire() {
        return "{playerID: " + this.getPlayerID() + ", isFire: true, TYPE_MESSAGE: " + MESSAGE_PLAYER_INPUT_FIRE + "}";
    }


    /**
     * Fixes Y movement on tiles and platforms where animation height changes by setting the mario's y
     * value to the difference between animation heights.
     */
    public void setAnimation(Animation newAnim) {
        super.setAnimation(newAnim);
    }


    public void update(TileMap map, float time) {
        if (isAlive) {
            update(map, time, false);

            for (FireShotFlame fireShotFlame : fireShotFlames) {
                if (!fireShotFlame.isVisible()) {
                    fireShotFlame.setDegree(weapon.getDegree());
                }
                fireShotFlame.update((int) time);
            }

            bodyToMove2D.Update();
            bodyToHit2D.Update();
        }

        for (Bullet bll : bullets) {
            if (bll.isVisible()) {
                bll.getBodyToHit2D().setParentX(bll.getX());
                bll.getBodyToHit2D().setParentY(bll.getY());
            }

        }
        for (Bullet bll : bullets) {
//            if (!bll.isVisible()) {
//                bll.setDegree(weapon.getDegree());
//            }
            bll.getFireShotImpact().update((int) time);
        }

        explosion.update((int) time);
    }

    public void update(TileMap map, float time, boolean lockInput) {

    }

    public void updateBullets(JSONArray bulletsJSON, boolean isOther) {
        if (bulletsJSON == null || bulletsJSON.length() <= 0) return;
        int i = 0;
        for (Bullet bll : bullets) {
            try {
                JSONObject obj = (JSONObject) bulletsJSON.get(i);
                bll.setX((float) obj.getDouble("x"));
                bll.setY((float) obj.getDouble("y"));
                bll.setDegree((float) obj.getDouble("degree"));
                bll.setVisible(obj.getBoolean("isVisible"));
                if (!obj.getBoolean("isVisible")) {
                    bll.setCollision(false);
                }

                if (!bll.isVisible() && bll.isBeforeVisible() && isOther) {
                    bll.getFireShotImpact().setX(bll.getX());
                    bll.getFireShotImpact().setY(bll.getY());
                    bll.getFireShotImpact().setVisible(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    public void updateFireFlames(JSONArray fireFlamesJson) {
        if (fireFlamesJson == null || fireFlamesJson.length() <= 0) return;
        int i = 0;
        for (FireShotFlame fireShotFlame : fireShotFlames) {
            try {
                JSONObject obj = (JSONObject) fireFlamesJson.get(i);
                fireShotFlame.setX((float) obj.getDouble("x"));
                fireShotFlame.setY((float) obj.getDouble("y"));
                fireShotFlame.setDegree((float) obj.getDouble("degree"));
                fireShotFlame.setVisible(obj.getBoolean("isVisible"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }
    }


    public void getsDamaged(int dame) {
        this.health -= dame;
        if (health < 0) {
            health = 0;
        }
        this.setHealth(health);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void setWeaponDegree(float degree) {
        weapon.setDegree(degree);
        for (FireShotFlame fireShotFlame : fireShotFlames) {
            //if (!fireShotFlame.isVisible()) {
            float rdi = (float) Math.toRadians(weapon.getDegree());
            float s = (float) Math.sin(rdi);
            float c = (float) Math.cos(rdi);
            float xnew = 0 * c + (getHeight() / 2 + 30) * s;
            float ynew = 0 * s - (getHeight() / 2 + 30) * c;
            fireShotFlame.setX(x + xnew);
            fireShotFlame.setY(y + ynew);
            fireShotFlame.setDegree(weapon.getDegree());
            break;
            //}
        }
    }

    private void GameOver() {
        // TODO Auto-generated method stub

    }

    private void StageClear() {
        // TODO Auto-generated method stub
        this.isLevelClear = true;
        //isSystemDriven=false;
    }

    public String jsonToSendAddPlayer() {
        JSONObject json = new JSONObject();
        try {
            json.put("playerID", this.getPlayerID());
            json.put("x", this.getX());
            json.put("y", this.getY());
            json.put("width", this.getWidth());
            json.put("height", this.getHeight());
            json.put("degree", this.getDegree());
            json.put("degreeWeapon", this.getWeapon().getDegree());
            json.put("heath", this.healthBar.getHealthCurrent());
            json.put("TYPE_MESSAGE", MESSAGE_TANK_ADD_PLAYER);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String jsonToSendPlayerHeath() {
        JSONObject json = new JSONObject();
        try {
            json.put("playerID", this.getPlayerID());
            json.put("heath", this.getHealth());
            json.put("TYPE_MESSAGE", MESSAGE_TANK_PLAYER_HEATH);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String jsonToSendPlayeScore(int score) {
        JSONObject json = new JSONObject();
        try {
            json.put("playerID", this.getPlayerID());
            json.put("score", score);
            json.put("TYPE_MESSAGE", MESSAGE_TANK_PLAYER_SCORE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String jsonToSendPlayeWeapon(double angleWeapon) {
        JSONObject json = new JSONObject();
        try {
            json.put("playerID", this.getPlayerID());
            json.put("angleWeapon", angleWeapon);
            json.put("TYPE_MESSAGE", MESSAGE_TANK_PLAYER_WEAPON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String jsonToSendUpdateFireFlames() {
        JSONObject json = new JSONObject();
        JSONArray jsonFireFlames = new JSONArray();
        for (FireShotFlame fireShotFlame : fireShotFlames) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("x", fireShotFlame.getX());
                jsonObject.put("y", fireShotFlame.getY());
                jsonObject.put("degree", fireShotFlame.getDegree());
                jsonObject.put("isVisible", fireShotFlame.isVisible());
                jsonFireFlames.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            json.put("playerID", this.getPlayerID());
            json.put("fireFlames", jsonFireFlames);
            json.put("TYPE_MESSAGE", MESSAGE_PLAYER_FIRE_FLAME_HIDE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String jsonToSendUpdatePlayer(double angle, float powerToSend, boolean isTankNotMove) {
        JSONObject json = new JSONObject();

        try {
            json.put("playerID", this.getPlayerID());
            json.put("angle", angle);
            json.put("power", powerToSend);
            json.put("isNotMove", isTankNotMove);
            json.put("TYPE_MESSAGE", MESSAGE_PLAYER_INPUT_MOVE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String jsonToSendUpdatePlayerPower(float powerToSend) {
        JSONObject json = new JSONObject();

        try {
            json.put("playerID", this.getPlayerID());
            json.put("power", powerToSend);
            json.put("TYPE_MESSAGE", MESSAGE_PLAYER_INPUT_POWER);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }


}

