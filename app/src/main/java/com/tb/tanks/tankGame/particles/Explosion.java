package com.tb.tanks.tankGame.particles;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.animation.Sprite;

public class Explosion extends Sprite {
    private static final int ANIM_TIME = 40;
    private Animation idleExplosion;
    private boolean isVisible = false;
    private ShouldHide shouldHide;

    public Explosion(TankSoundManager soundManager) {

        super(0, 0);

        this.setDegree(0.f);
        idleExplosion = new Animation(ANIM_TIME);
        for (int i = 0; i < TankResourceManager.Explosions.length; i++) {
            idleExplosion.addFrame(TankResourceManager.Explosions[i]);
        }
        setAnimation(idleExplosion);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public ShouldHide getShouldHide() {
        return shouldHide;
    }

    public void setShouldHide(ShouldHide shouldHide) {
        this.shouldHide = shouldHide;
    }

    @Override
    public void update(int time) {
        if (isVisible) {
            super.update(time);
            shouldHide();
        }

    }

    public void shouldHide() {
        if (currentAnimation().getCurrFrameIndex() >= TankResourceManager.Explosions.length - 1) {
            isVisible = false;
            currentAnimation().setCurrFrameIndex(0);
            currentAnimation().setAnimTime(0);
            if(shouldHide != null){
                shouldHide.shouldHide(this);
            }
        }
    }

    @Override
    public void draw(Canvas g, float x, float y) {
        if (isVisible) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(-currentAnimation().getImage().getWidth() / 2, -currentAnimation().getImage().getHeight() / 2);
            matrix.postRotate(degree);
            matrix.postTranslate(x, y);
            g.drawBitmap(currentAnimation().getImage(), matrix, null);
        }
    }

}
