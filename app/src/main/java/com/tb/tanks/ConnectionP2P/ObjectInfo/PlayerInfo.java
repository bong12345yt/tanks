package com.tb.tanks.ConnectionP2P.ObjectInfo;

import com.tb.tanks.tankGame.objects.tank.Bullet;
import com.tb.tanks.tankGame.particles.FireShotFlame;
import com.tb.tanks.tankGame.util.PlayerDefine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

public class PlayerInfo {
    private String playerID;
    private float x;
    private float y;
    private float angle;
    private float degree;
    private float power;
    private boolean isNotMove;
    private boolean isAdded;
    private int width;
    private int height;
    private float speed;
    private int heath;
    private Stack<Bullet> bullets;
    private ArrayList<FireShotFlame> fireShotFlames;

    public PlayerInfo(){
        heath = PlayerDefine.PLAYER_HEATH;
        speed = 400.f;
        isAdded = false;
        bullets = new Stack<>();
        fireShotFlames = new ArrayList<FireShotFlame>();
        for (int i = 0; i < 10; i++) {
            Bullet bll = new Bullet(null);
            bll.setDegree(degree);
            bll.setX(this.x);
            bll.setY(this.y);
            bullets.push(bll);
        }

        for (int i = 0; i < 10; i++) {
            FireShotFlame fireShotFlame = new FireShotFlame(null);
            fireShotFlame.setDegree(degree);
            fireShotFlame.setX(this.x);
            fireShotFlame.setY(this.y);
            fireShotFlames.add(fireShotFlame);
        }
    }

    public boolean isAdded() {
        return isAdded;
    }

    public int getHeath() {
        return heath;
    }

    public void setHeath(int heath) {
        this.heath = heath;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
        for (Bullet bll : bullets) {
            if (!bll.isVisible()) {
                bll.setDegree(degree);
            }
        }
    }

    public float getX() {
        return x;
    }

    public boolean isNotMove() {
        return isNotMove;
    }

    public void setNotMove(boolean notMove) {
        isNotMove = notMove;
    }

    public void setX(float x) {
        this.x = x;
    }

    public ArrayList<FireShotFlame> getFireShotFlames() {
        return fireShotFlames;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public void fire(){
        for (Bullet bll : bullets) {
            if (!bll.isVisible()) {
                float rdi = (float) Math.toRadians(degree);
                float s = (float) Math.sin(rdi);
                float c = (float) Math.cos(rdi);
                float xnew = 0 * c + (getHeight() - 50) * s;
                float ynew = 0 * s - (getHeight() - 50) * c;
                bll.setX(x + xnew);
                bll.setY(y + ynew);
                bll.setDegree(degree);
                bll.setVisible(true);
                break;
            }
        }
    }

    public void eraseBulletWhenCollision(int index){
        bullets.get(index).setVisible(false);
    }

    public void update(float deltaTime){
        if(!isNotMove && power > 0){
            x += deltaTime*speed*(float)Math.sin(angle - Math.PI/2);
            y -= deltaTime*speed*(float)Math.cos(angle - Math.PI/2);
        }

        for (Bullet bll : bullets) {
            if (bll.isVisible()){
                bll.update(null, deltaTime);
            }
        }
        this.setDegree((float)Math.toDegrees(angle - Math.PI/2));
    }

    public JSONObject serializeForUpdate(){
        JSONObject  result = new JSONObject();
        JSONArray jsonBulletsArray = new JSONArray();
        for(Bullet bll:bullets){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("x", bll.getX());
                jsonObject.put("y", bll.getY());
                jsonObject.put("degree", bll.getDegree());
                jsonObject.put("isVisible", bll.isVisible());
                jsonBulletsArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            result.put("id", playerID);
            result.put("x", x);
            result.put("y", y);
            result.put("degree", degree);
            result.put("bullets", jsonBulletsArray);
            result.put("heath", this.heath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
