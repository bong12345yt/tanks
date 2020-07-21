package com.tb.tanks.tankGame.screens;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.tankGame.core.GameLoader;
import com.tb.tanks.tankGame.core.GameRenderer;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.base.Creature;
import com.tb.tanks.tankGame.objects.tank.Tank;

import java.util.ArrayList;
import java.util.List;

public class LevelCompleteScreen extends Screen {

	private Tank tank;
	private TileMap map;
	private TileMap backgroundMap;
	private TileMap foregroundMap;
	private GameRenderer renderer;
	public GameLoader gameLoader;
	public int period = 20;
	Paint paint, paint2;
	Bitmap frameBuffer;
	public Bitmap tmpBitmap;
	private Canvas tmpCanvas;
	private Bitmap background;
    private boolean lockInputs=true;
	private int blink=0;
	private Bitmap bmp;
	private Point initialPt,finalPt;
	ArrayList<Point> pearls;
	private int pearlSize=10;
	private Bitmap bmpLevel;
	private boolean lockUpdates=false;
	public LevelCompleteScreen(Game game) {
		super(game);
		frameBuffer = ((AndroidGame) game).getBuffer();
		tmpBitmap= Bitmap.createBitmap(2*frameBuffer.getWidth(), 2*frameBuffer.getHeight(), Config.RGB_565);
		Creature.WAKE_UP_VALUE_DOWN_RIGHT=game.getScreenWidth()/8;
		new Canvas(frameBuffer);
		tmpCanvas=new Canvas(tmpBitmap);
		// Initialize game objects here
		// Defining a paint object
		paint = new Paint();
		paint.setTextSize(12);
		paint.setTextAlign(Paint.Align.CENTER);
		// paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		// paint.setFilterBitmap(true);

		paint2 = new Paint();
		paint2.setTextSize(60);
		paint2.setTextAlign(Paint.Align.CENTER);
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		//Settings.loadPreferences((((AndroidGame)game)).getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, 0));
		pearls=new ArrayList<Point>();
		//pearlSize= TankResourceManager.pearl1.getWidth();
		lockInputs=true;
		loadGame();
		lockUpdates=false;
	}

 	public void loadGame() {

 	}

   	@Override
	public void update(float deltaTime) {
 		if(((AndroidGame)game).isScreenTransitionActive()) return;
   		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		updateRunning(touchEvents, deltaTime);
	}
	
	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

	}

	@Override
	public void paint(float deltaTime) {
		drawRunningUI();
	}
	
	private void goToMenu() {
		if (lockInputs || Settings.level==0)return;
		GuiMenuScreen mainMenuScreen = new GuiMenuScreen(game);
		((AndroidGame) game).setScreenWithFade(mainMenuScreen);
	}

	private void drawRunningUI() {

	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		Creature.WAKE_UP_VALUE_DOWN_RIGHT=game.getScreenWidth()/16;
		//tmpBitmap.recycle();
	}

	@Override
	public void onBackPressed() {
		goToMenu();
	}

		@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	
	public Bitmap getBitmap(){
		return tmpBitmap;
	}
	
}