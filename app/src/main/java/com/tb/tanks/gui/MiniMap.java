package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

import com.tb.tanks.framework.Input;
import com.tb.tanks.tankGame.objects.tank.Tank;

import java.util.HashMap;

public class MiniMap extends Component {
    private Bitmap background;
    private HashMap<String, Bitmap> playersMini;
    private HashMap<String, Tank> players;
    private int realMapWidth;
    private int realMapHeight;
    private Point realMapPos;
    private float scaleX;
    private float scaleY;

    public MiniMap(int x, int y, int w, int h) {
        super(x, y, w, h);
        background = GUIResourceManager.background_mini_map;
        playersMini = new HashMap<String, Bitmap>();
        playersMini.put("me", GUIResourceManager.tank_mini_map);
        playersMini.put("other", GUIResourceManager.tank_other_mini_map);
        players = new HashMap<String, Tank>();
    }

    public int getRealMapWidth() {
        return realMapWidth;
    }

    public void setRealMapWidth(int realMapWidth) {
        this.realMapWidth = realMapWidth;
        scaleX = (float)width / realMapWidth;
    }

    public int getRealMapHeight() {
        return realMapHeight;
    }

    public void setRealMapHeight(int realMapHeight) {
        this.realMapHeight = realMapHeight;
        scaleY = (float)height / realMapHeight;
    }

    public Point getRealMapPos() {
        return realMapPos;
    }

    public void setRealMapPos(Point realMapPos) {
        this.realMapPos = realMapPos;
    }

    public void addPlayer(String key, Tank player) {
        players.put(key, player);
    }

    @Override
    public void draw(Canvas g, int X, int Y) {
        g.drawBitmap(background, X + x, Y + y, null);
        float meX = (float)(players.get("me").getX() - realMapPos.x) * scaleX;
        float meY = (float)(players.get("me").getY() - realMapPos.y) * scaleY;
        Matrix matrix = new Matrix();
        matrix.postTranslate(-playersMini.get("me").getWidth() / 2, -playersMini.get("me").getHeight() / 2);
        matrix.postRotate(players.get("me").getDegree());
        matrix.postTranslate(X + x + meX, Y + y + meY);
        g.drawBitmap(playersMini.get("me"), matrix, null);

        float otherX = (float)(players.get("other").getX() - realMapPos.x) * scaleX;
        float otherY = (float)(players.get("other").getY() - realMapPos.y) * scaleY;
        Matrix matrixOther = new Matrix();
        matrixOther.postTranslate(-playersMini.get("other").getWidth() / 2, -playersMini.get("other").getHeight() / 2);
        matrixOther.postTranslate(X + x + otherX, Y + y + otherY);
        g.drawBitmap(playersMini.get("other"), matrixOther, null);
    }

    @Override
    public void processEvent(Input.TouchEvent event) {

    }
}
