package com.tb.tanks.tankGame.screens;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Screen;

public class SplashLoadingScreen extends Screen {
    
	
	
	public SplashLoadingScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
       Graphics g = game.getGraphics();
       game.setScreen(new LoadingScreen(game));
    }

    @Override
    public void paint(float deltaTime) {
    	 
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