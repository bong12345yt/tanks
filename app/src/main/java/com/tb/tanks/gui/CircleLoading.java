package com.tb.tanks.gui;

import android.graphics.Canvas;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.animation.Sprite;

public class CircleLoading extends Sprite {
    private boolean isVisible = false;
    private Animation loading = null;

    public CircleLoading(int x, int y) {
        super(x, y);
        loading = new Animation(25);
        for (int i = 0; i < GUIResourceManager.loadings.length; i++) {
            loading.addFrame(GUIResourceManager.loadings[i]);
        }
        setAnimation(loading);
        this.x = x;
        this.y = y;
        degree = 0;
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
        }
    }

    @Override
    public void draw(Canvas g, float X, float Y) {
        if (isVisible) {
            g.drawBitmap(currentAnimation().getImage(), X + x, Y + y, null);
        }
    }

}
