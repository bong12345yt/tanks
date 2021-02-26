package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.tb.tanks.framework.Input;
import com.tb.tanks.tankGame.objects.tank.Tank;

import java.util.HashMap;

public class ScoreBoard extends Component {

    private Bitmap bmBackground;
    private Bitmap bmTitle;
    private final String title = "Score Board";
    private Paint paintTitle;
    private Paint paintScore;
    private Rect boundT;
    private Rect boundS;

    private HashMap<String, Tank> players;

    public ScoreBoard(int x, int y, int w, int h) {
        super(x, y, w, h);
        Typeface tf = GUIResourceManager.loadFont("fonts/DancingScript-Bold.ttf");
        paintTitle = new Paint();
        paintTitle.setTextAlign(Paint.Align.LEFT);
        paintTitle.setAntiAlias(true);
        paintTitle.setTextSize(45);
        paintTitle.setTypeface(tf);
        paintTitle.setColor(Color.WHITE);

        paintScore = new Paint();
        paintScore.setTextAlign(Paint.Align.LEFT);
        paintScore.setAntiAlias(true);
        paintScore.setTextSize(40);
        paintScore.setTypeface(tf);
        paintScore.setColor(Color.WHITE);

        bmBackground = GUIResourceManager.score_board_bg;
        bmTitle = GUIResourceManager.score_board_title;
        boundT = new Rect();
        boundS = new Rect();
        paintTitle.getTextBounds(title, 0, title.length(), boundT);

        players = new HashMap<String, Tank>();

    }

    public void addPlayer(String key, Tank player) {
        players.put(key, player);
    }

    @Override
    public void draw(Canvas g, int X, int Y) {
        g.drawBitmap(bmBackground, X + x, Y + y, null);
        g.drawBitmap(bmTitle, X + x, Y + y, null);

        g.drawText(title, X + x + (width - boundT.width()) / 2, Y + y + bmTitle.getHeight() - (bmTitle.getHeight() - boundT.height()) / 2, paintTitle);

        String meScore = "me: " + players.get("me").getScore();
        paintScore.getTextBounds(meScore, 0, meScore.length(), boundS);
        int heightMe = boundS.height();
        g.drawText(meScore, X + x + 20, Y + y + 20 +  bmTitle.getHeight() + heightMe, paintScore);

        String otherScore = "other: " + players.get("other").getScore();
        paintScore.getTextBounds(otherScore, 0, otherScore.length(), boundS);
        g.drawText(otherScore, X + x + 20, Y + y + 40 + bmTitle.getHeight() + heightMe + boundS.height(), paintScore);
    }

    @Override
    public void processEvent(Input.TouchEvent event) {

    }
}
