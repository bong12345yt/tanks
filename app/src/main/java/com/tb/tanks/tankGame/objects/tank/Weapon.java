package com.tb.tanks.tankGame.objects.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.animation.Sprite;

public class Weapon extends Sprite {
    private static final int ANIM_TIME = 125;
    private Animation idleWeapon;
    public Weapon(TankSoundManager soundManager) {
        idleWeapon = new Animation(ANIM_TIME).addFrame(TankResourceManager.Weapon);
        setAnimation(idleWeapon);
    }

    public void setIdleWeapon(Bitmap idleWeapon) {
        this.idleWeapon = new Animation(ANIM_TIME).addFrame(idleWeapon);
        setAnimation(this.idleWeapon);
    }

    public void Update(float deltaTime){

    }

    @Override
    public void draw(Canvas g, float x, float y) {
        super.draw(g, x, y);
    }
}
