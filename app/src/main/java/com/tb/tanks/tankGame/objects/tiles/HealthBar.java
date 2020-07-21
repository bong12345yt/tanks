package com.tb.tanks.tankGame.objects.tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.Sprite;
import com.tb.tanks.tankGame.core.tile.TileMap;

public class HealthBar extends Sprite {
    private static final int ANIM_TIME = 125;
    private String id;
    private Bitmap bgHeath;
    private Bitmap heathGreen;
    private boolean isVisible = true;
    private int HealthCurrent = 0;
    private int maxHealth = 0;

    private float scaleY = 1.f;
    private float scaleX = 1.f;

    public HealthBar(TankSoundManager soundManager) {

        super(0, 0);

        bgHeath = TankResourceManager.bgHeath;
        heathGreen = TankResourceManager.heathGreen;

        this.setDegree(0.f);
    }

    public String getId() {
        return id;
    }

    public int getHealthCurrent() {
        return HealthCurrent;
    }

    public void setHealthCurrent(int healthCurrent) {
        if(healthCurrent > 0) {
            HealthCurrent = healthCurrent;
            int delta = heathGreen.getWidth() - maxHealth;
            scaleX = (float) (healthCurrent + delta) / heathGreen.getWidth();
        }else{
            scaleX = 0.f;
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void update(TileMap map, float time) {

    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }



    @Override
    public void setX(float x) {
        super.setX(x);

    }

    @Override
    public void setY(float y) {
        super.setY(y);
    }

    @Override
    public void setDegree(float degree) {
        super.setDegree(degree);
    }

    @Override
    public void draw(Canvas g, float x, float y) {
        if(isVisible){
            Matrix matrix = new Matrix();
            Matrix matrix1 = new Matrix();

            matrix.postTranslate(-bgHeath.getWidth() / 2 , -bgHeath.getHeight() / 2);
            matrix.postRotate(degree);
            matrix.postTranslate(x, y);
            g.drawBitmap(bgHeath, matrix, null);

            matrix1.setScale(scaleX, scaleY);
            matrix1.postTranslate(-bgHeath.getWidth() / 2 , -bgHeath.getHeight() / 2);
            matrix1.postRotate(degree);
            matrix1.postTranslate(x, y + 3);
            g.drawBitmap(heathGreen, matrix1, null);
        }

    }
}
