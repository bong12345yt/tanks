package com.tb.tanks.tankGame.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.gui.AndroidText;
import com.tb.tanks.gui.CircleLoading;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.util.SpriteMap;

public class LoadingScreen extends Screen {
    private boolean drawnOnce = false;
    private Bitmap[] font;
    private Bitmap logo;
    Canvas canvas;
    private int screenWidth;
    private int screenHeight;
    Bitmap frameBuffer;
    private CircleLoading circleLoading;
    private int loadingTime = 3000;
    private long currentTime = 0;
    private AndroidText androidText = null;

    public LoadingScreen(Game game) {
        super(game);

        frameBuffer = ((AndroidGame) game).getBuffer();
        canvas = new Canvas(frameBuffer);
        androidText = new AndroidText("Loading", frameBuffer.getWidth() - 200,frameBuffer.getHeight() - 150,200,100);
        androidText.setTextSize(40);
        androidText.setColor(Color.WHITE);
        circleLoading = new CircleLoading(0,0);
        circleLoading.setX(frameBuffer.getWidth() - circleLoading.getWidth() - 20);
        circleLoading.setY(frameBuffer.getHeight() - circleLoading.getHeight() - androidText.getHeight());
        screenWidth = frameBuffer.getWidth();
        screenHeight = frameBuffer.getHeight();
        font = new SpriteMap(TankResourceManager.loadImage("items/font_white_8.png"), 96, 1).getSprites();
        logo = TankResourceManager.loadImage("gui/Logo.png");
        circleLoading.setVisible(true);
        currentTime = System.currentTimeMillis();

    }

    @Override
    public void update(float deltaTime) {
        if (!drawnOnce){
            ((TankGame) game).soundManager.loadResouces();
            ((TankGame) game).resourceManager.loadResouces();
            ((TankGame) game).guiResourceManager.loadResouces();
        }

        if(System.currentTimeMillis() - currentTime >= loadingTime && drawnOnce){
            ((AndroidGame) game).setScreenWithFade(new GuiMenuScreen(game));//mainMenuScreen);
            Settings.loadPreferences((Context) game);
            //circleLoading.setVisible(false);
        }
        circleLoading.update((int) (deltaTime*1000));
    }

    @Override
    public void paint(float deltaTime) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(logo, (screenWidth - logo.getHeight()) / 2, (screenHeight - logo.getHeight()) / 2, null);
        circleLoading.draw(canvas, 0, 0);
        androidText.draw(canvas, 0, 0);
        drawnOnce = true;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackPressed() {

    }
}