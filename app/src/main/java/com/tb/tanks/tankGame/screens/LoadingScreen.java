package com.tb.tanks.tankGame.screens;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.framework.gfx.AndroidGraphics;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.util.SpriteMap;

public class LoadingScreen extends Screen {
	private boolean drawnOnce=false;
	private Bitmap[] font;
  
	public LoadingScreen(Game game) {
         super(game);
	     font=new SpriteMap(TankResourceManager.loadImage("items/font_white_8.png"),96,1).getSprites();
    }

    @Override
    public void update(float deltaTime) {
    	if (!drawnOnce) return;
        ((TankGame)game).soundManager.loadResouces();
        ((TankGame)game).resourceManager.loadResouces();
        ((AndroidGame) game).setScreenWithFade(new GuiMenuScreen(game));//mainMenuScreen);
        Settings.loadPreferences((Context) game);
     }

    @Override
    public void paint(float deltaTime) {
    	
        Graphics g = game.getGraphics();
        //g.drawARGB(200, , g, b)
        Paint paint=new Paint(color.holo_blue_bright);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(18);
        paint.setAntiAlias(true);
        //g.drawString("Loading....", 100,200,paint);//game.getScreenWidth()/2,game.getScreenHeight()/2, paint);
        ((AndroidGraphics) g).drawBitmapFont(font,"LOADING GAME....",game.getScreenWidth()/2-50,game.getScreenHeight()/2-6,8);
        //g.drawImage(Assets.splash,0, 0);
       	drawnOnce=true;
        
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackPressed() {

    }
}