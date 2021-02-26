package com.tb.tanks.tankGame.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Input.KeyEvent;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.tankGame.core.GameLoader;
import com.tb.tanks.tankGame.core.GameRenderer;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.tank.Tank;

import java.io.IOException;
import java.util.List;

public class GameScreen extends Screen {
	enum GameState {
		Ready, Running, SwitchLevel,Paused,GameOver
	}

	GameState state = GameState.Ready;

	// Variable Setup
	int livesLeft = 1;
	private static Tank tank;
	private TileMap map;
	private TileMap backgroundMap;
	private TileMap foregroundMap;
	private GameRenderer renderer;
	public GameLoader gameLoader;
	public int period = 20;
	Paint paint, paint2;
	Bitmap frameBuffer;
	boolean pauseDrawn = false;
	boolean readyDrawn = false;
	/** to simulate key right key left using accelerometer */
	private int eventID = 0;
	private Canvas gameCanvas;
    private boolean isSystemDriven=false;
    /**time in seconds  */
    private float timeRemaining=0;
	private float switchTime=0;
	private boolean lockInputs=true;
	private String msg="";
	private boolean savedScores=false;
	
    public GameScreen(Game game) {
		super(game);
		frameBuffer = ((AndroidGame) game).getBuffer();
		gameCanvas = new Canvas(frameBuffer);
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
		if (tank==null){
			Log.i("Mario","New Mario Created" +state);
			tank = new Tank(((TankGame) game).soundManager);
			}
		loadGame();	
		Settings.setPlayer(tank);
		lockInputs=false;
		state = GameState.Running;
		((TankGame)game).soundManager.loadGameMusic();
	}

    private void reLoadGame(int beginX) {
    	gameLoader = new GameLoader((AndroidGame) game);


    }

	public void loadGame() {
		// soundManager = new MarioSoundManager((Activity) game);
		// ResourceManager.prepareManager((Activity) game);
		readyDrawn = false;
		lockInputs=true;
		//lockUpdates=true;
		isSystemDriven=true;
		try {
			gameLoader = new GameLoader((AndroidGame) game);
			renderer = new GameRenderer();
			Log.e("D","maps/world"+Settings.world+"/map"+Settings.level+"/map3.txt");
			map = gameLoader.loadMap("maps/world"+Settings.world+"/map"+Settings.level+"/map3.txt",
					((TankGame) game).soundManager); // use the ResourceManager
			TankResourceManager.loadBackground(gameLoader.getBackGroundImageIndex());
			renderer.setBackground(TankResourceManager.Background);
			map.setPlayer(tank); // set the games main player to mario
			Settings.setPlayer(tank);

		} catch (IOException e) {
			System.out.println("Invalid Map.");
			Log.e("Errrr", "invalid map");
		}
		msg="";
		
	}


	@Override
	public void update(float deltaTime) {
		if(((AndroidGame)game).isScreenTransitionActive()) return;
		   
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    // We have four separate update methods
		// Depending on the state of the game, we call different update methods.
		
		if (state == GameState.Ready){
			updateReady(touchEvents,deltaTime);
		}else if (state == GameState.Running){
			//if (lockUpdates)return;
			updateRunning(touchEvents, deltaTime);
		}else if (state == GameState.Paused){
			updatePaused(touchEvents);
		}else if (state == GameState.GameOver){
			updateGameOver(touchEvents);
		}
	}

	private void updateReady(List<TouchEvent> touchEvents, float deltaTime) {
		// It starts with a "Ready" screen.
		// When the user touches the screen, the game begins.
		// state now becomes GameState.Running.
		// Now the updateRunning() method will be called!
		if (touchEvents.size() > 0)
		{
			state = GameState.Running;
			isSystemDriven=false;
			/*
			if (lockUpdates==false){
				isSystemDriven=true;
				switchTime=0;
			}else{
				lockUpdates=false;
			}
			*/
			touchEvents.clear();
			updateRunning(touchEvents, 0);
			paint(0);
			lockInputs=false;
			//((MarioGame) game).soundManager.playStageEnter();
		}
		lockInputs=true;
		updateRunning(touchEvents, 0);
	}

	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

	}

	
	private void updatePaused(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 0, 200, 240)) {

					if (!inBounds(event, 0, 0, 35, 35)) {
						resume();
					}
				}

				if (inBounds(event, 0, 160, 300, 160)) {
					// nullify();
					goToMenu();
				}
			}
		}
	}

	
	private void updateGameOver(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				GameOver();
				return;
			}
		}
		
		List<KeyEvent> keyEvents = game.getInput().getKeyEvents();
		if ( keyEvents != null && keyEvents.size() > 0) {
			GameOver();
			return;
		}
		lockInputs=true;
		updateRunning(touchEvents, 0);
        paint(0);
	}


	@Override
	public void paint(float deltaTime) {
		// First draw the game elements.
		// Secondly, draw the UI above the game elements.
		if (state == GameState.Ready)
			drawReadyUI();
		if (state == GameState.Running)
			drawRunningUI();
		if (state == GameState.SwitchLevel)
			drawSwitchGameUI();
		if (state == GameState.Paused)
			drawPausedUI();
		if (state == GameState.GameOver)
			drawGameOverUI();
	}
	
	private void drawSwitchGameUI(){
		renderer.draw(gameCanvas, map, backgroundMap, foregroundMap,
				frameBuffer.getWidth(), frameBuffer.getHeight());
		Graphics g = game.getGraphics();
		g.drawARGB((int) (50*switchTime), 0, 0, 0);
	}
	
	private void goToMenu() {
		GuiMenuScreen mainMenuScreen = new GuiMenuScreen(game);
		((AndroidGame) game).setScreenWithFade(mainMenuScreen);
		//mario=null;
	}

	private void drawReadyUI() {
		//if (readyDrawn)
		//	return;
		readyDrawn = true;
		Graphics g = game.getGraphics();
		// Canvas canvas=new Canvas(frameBuffer);
		renderer.draw(gameCanvas, map, backgroundMap, foregroundMap,
				frameBuffer.getWidth(), frameBuffer.getHeight());
		g.drawARGB((int) (42*switchTime), 0, 0, 0);
		GameRenderer.drawStringDropShadowAsHud(gameCanvas,"PAUSED!",frameBuffer.getWidth()/2,frameBuffer.getHeight()/2-16,2,0);
		GameRenderer.drawStringDropShadowAsHud(gameCanvas,"TAP TO RESUME, PRESS BACK TO QUIT",frameBuffer.getWidth()/2+50,frameBuffer.getHeight()/2+8,1,0);
	}

	private void drawRunningUI() {
		// Graphics g = game.getGraphics();
		// Canvas gameCanvas=new Canvas(frameBuffer);
		renderer.draw(gameCanvas, map, backgroundMap, foregroundMap,
				frameBuffer.getWidth(), frameBuffer.getHeight());
		if (msg.length()>0)GameRenderer.drawStringDropShadowAsHud(gameCanvas, msg,frameBuffer.getWidth()/2,frameBuffer.getHeight()/2 ,1,0);
		if (switchTime<3){
			lockInputs=false;
		}else if(switchTime>0) {
			Graphics g = game.getGraphics();
			g.drawARGB((int) (42*switchTime), 0, 0, 0);
		}
	}

	private void drawPausedUI() {
		if (pauseDrawn)
			return;
		pauseDrawn = true;
		Graphics g = game.getGraphics();
		// Darken the entire screen so you can display the Paused screen.
		g.drawARGB(120, 0, 0, 0);
		/*
		 * int w=frameBuffer.getWidth(); int h=frameBuffer.getHeight();
		 * //g.drawRect(0, 0, w, h, Color.argb(200, 100, 100, 100));
		 * g.drawString("Resume",w/2-50, h/2-30, paint2); g.drawString("Menu",
		 * w/2-40, h/2+30, paint2);
		 */
	}

	private void drawGameOverUI() {
		renderer.draw(gameCanvas, map, backgroundMap, foregroundMap,
				frameBuffer.getWidth(), frameBuffer.getHeight());
		GameRenderer.drawStringDropShadowAsHud(gameCanvas,"GAME OVER...",frameBuffer.getWidth()/2,frameBuffer.getHeight()/2-16,2,0);
		GameRenderer.drawStringDropShadowAsHud(gameCanvas,"TAP TO RETURN TO MAIN MENU",frameBuffer.getWidth()/2+50,frameBuffer.getHeight()/2+8,1,0);
	}
	
	@Override
	public void pause() {
		if (state == GameState.Running) {
			state= GameState.Ready;
			lockInputs=true;
		}
	}
	
	public void showPauseDialog() {
		lockInputs=true;
		//state = GameState.Paused;
		AlertDialog dialog = new AlertDialog.Builder((AndroidGame) game)
					.setTitle("Quit Game")
					.setPositiveButton("Quit",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
                                                    int whichButton) {
									goToMenu();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
                                                    int whichButton) {
									state = GameState.Running;
								}
							}).setMessage("Return to main menu?")
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							state = GameState.Running;
							//lockInputs=false;
							//lockUpdates=false;
						}
					}).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	
	private void GameOver() {
		if (Settings.world==4){
			Settings.addHighScore(4, 1, Settings.getScore());
			Settings.addRecordTime(4, 1, 10000);
		}
		state = GameState.Running;
		isSystemDriven=false;
		lockInputs=false;
		((AndroidGame) game).setScreenWithFade(new GuiMenuScreen(game));
		goToMenu();
		return;
        /*
		((AndroidGame)game).runOnUiThread(new Runnable(){
			   @Override
				public void run() {
					// TODO Auto-generated method stub
				   showGameOverDialog(); 
				}
		   }
		);
		*/
	}	
	
	private void showGameOverDialog(){

	}

	
	@Override
	public void resume() {
		//state = GameState.Ready;
		//lockUpdates=false;
		///if (state == GameState.Paused)
		//	state = GameState.Running;
		//pauseDrawn = false;
	}

	@Override
	public void onStop() {

	}

	@Override
	public void dispose() {

	}

	
	@Override
	public void onBackPressed() {
		//pause();
		if (state == GameState.Ready) {
			goToMenu();
		}else if (state == GameState.Running) {
			pause();
			//showPauseDialog();
		} else if (state == GameState.GameOver) {
			goToMenu();
		}

	}

	private boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {

		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1)
			return true;
		else
			return false;
	}
	
	private void processTouchEvent(TouchEvent event ){

	}
	
	public static Tank getTank(){
		return tank;
	}
}