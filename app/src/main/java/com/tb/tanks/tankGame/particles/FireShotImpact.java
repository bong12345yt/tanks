package com.tb.tanks.tankGame.particles;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.animation.Sprite;

public class FireShotImpact extends Sprite {
    private static final int ANIM_TIME = 75;
    private Animation idleImpact;
    private boolean isVisible = false;

    public FireShotImpact(TankSoundManager soundManager) {

        super(0, 0);

        this.setDegree(0.f);
        idleImpact = new Animation(ANIM_TIME);
        for (int i = 0; i < TankResourceManager.FireShotImpacts.length; i++) {
            idleImpact.addFrame(TankResourceManager.FireShotImpacts[i]);
        }
        setAnimation(idleImpact);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public void update(int time) {
        if (isVisible) {
            super.update(time);
            shouldHide();
        }

    }

    public void shouldHide() {
        if (currentAnimation().getCurrFrameIndex() >= TankResourceManager.FireShotImpacts.length - 1) {
            isVisible = false;
            currentAnimation().setCurrFrameIndex(0);
            currentAnimation().setAnimTime(0);
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
