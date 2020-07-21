package com.tb.tanks.tankGame.core;

import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.gui.GUIResourceManager;
import com.tb.tanks.tankGame.objects.base.Creature;
import com.tb.tanks.tankGame.screens.SplashLoadingScreen;

/**
 * Main Entry Class for the game (Only activity used in game)
 * @author mahesh
 *
 */
public class TankGame extends AndroidGame {
	boolean firstTimeCreate = true;
	public TankResourceManager resourceManager;
	public TankSoundManager soundManager;
	public GUIResourceManager guiResourceManager;
	public static final int QUIT_GAME_DIALOG = 0;
	public static final int VERSION = 1;

	public Screen getStartScreen() {
		 if (firstTimeCreate) {
	            //Assets.load(this);
	            soundManager=new TankSoundManager(this);
	            resourceManager=new TankResourceManager(this);
			 	guiResourceManager = new GUIResourceManager(this);
	            firstTimeCreate = false;
	            Creature.WAKE_UP_VALUE_DOWN_RIGHT=WIDTH/16;
	            //Creature.WAKE_UP_VALUE_DOWN_RIGHT=HEIGHT/16;
	      }
         return new SplashLoadingScreen(this);
	}

	
	 @Override
	 public void onResume() {
	      super.onResume();
	      TankSoundManager.playMusic();
	      
	 }

	@Override
    public void onPause() {
	    super.onPause();
	   	TankSoundManager.pauseMusic();
    }
	
	@Override
	public void setScreenWithFade(Screen screen) {
		soundManager.playswitchScreen();
		super.setScreenWithFade(screen);
	}


	
}
