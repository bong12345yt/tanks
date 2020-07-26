package com.tb.tanks.tankGame.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Html;
import android.util.Log;

import com.tb.tanks.ConnectionP2P.P2PConnectionListener;
import com.tb.tanks.ConnectionP2P.PeersAvailableListener;
import com.tb.tanks.ConnectionP2P.WifiManagerP2P;
import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.framework.input.AndroidInput;
import com.tb.tanks.gui.AndroidButton;
import com.tb.tanks.gui.AndroidImageButton;
import com.tb.tanks.gui.AndroidListView;
import com.tb.tanks.gui.AndroidPanel;
import com.tb.tanks.gui.AndroidPic;
import com.tb.tanks.gui.AndroidSlider;
import com.tb.tanks.gui.Component;
import com.tb.tanks.gui.ComponentClickListener;
import com.tb.tanks.gui.ComponentItemClickListener;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.R;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.preferences.PreferenceConstants;
import com.tb.tanks.tankGame.preferences.SetPreferencesActivity;

import java.io.IOException;
import java.util.List;

public class GuiMenuScreen extends Screen implements PeersAvailableListener {

	AndroidPanel panel;
	AndroidButton btnPlay, btnHighscore, btnOptions, btnHelp, btnAbout;
	AndroidImageButton btnSearch;
	AndroidPic marioPic;
	AndroidSlider slider;
	private AndroidListView lstPlayer;

	private int width,height;
	private String mSelectedControlsString;
	private Canvas gameCanvas;
	private Bitmap background;

	Bitmap frameBuffer;
	public GuiMenuScreen(final Game game) {
		super(game);
		width = game.getScreenWidth();
		height =game.getScreenHeight();
		frameBuffer = ((AndroidGame) game).getBuffer();
		gameCanvas = new Canvas(frameBuffer);
		background= TankResourceManager.loadImage("backgrounds/tank_menu_bg.png");
		panel = new AndroidPanel(" SUPERMARIO ", 0, 0, width, height);
		panel.setTitleBarheight(36);
		panel.setForeColor(Color.WHITE);
		int tbh = panel.getTitleBarheight()-10;

		btnPlay = new AndroidButton("PLAY", 10, tbh + 10, 140, 35);
		btnHighscore = new AndroidButton("HIGHSCORE", 10, tbh + 60, 140, 35);
		Bitmap imgSearchNormal = TankResourceManager.loadImage("gui/btn_search_normal.png");
		Bitmap imgSearchFocus = TankResourceManager.loadImage("gui/btn_search_focus.png");
		btnSearch = new AndroidImageButton("", width/2 - imgSearchNormal.getWidth()/2, height/2 - imgSearchNormal.getHeight()/2,  imgSearchNormal.getWidth(), imgSearchNormal.getHeight());
		btnSearch.setBackgroundNormal(imgSearchNormal);
		btnSearch.setBackgroundFocused(imgSearchFocus);
		btnOptions = new AndroidButton("OPTIONS", 10, tbh + 110, 140, 35);
		btnHelp = new AndroidButton("ABOUT", 10, tbh + 160, 140, 35);
		btnAbout = new AndroidButton("i", 10, tbh + 170, 50, 35);
		lstPlayer = new AndroidListView(width/2 - 500,height/2 - 350,1000,700);
		String []strs = {"Bong 1", "Bong 2", "Danh 1", "Ban 1", "Dat 1", "Bao 1", "Anh 1"};
		lstPlayer.setListText(strs);
		lstPlayer.setVisible(false);

		btnPlay.setTextSize(20);
		btnHighscore.setTextSize(20);
		btnOptions.setTextSize(20);
		btnHelp.setTextSize(20);
		btnAbout.setTextSize(20);
		/*
		 * btnPlay.setForeColor(Color.WHITE);
		 * btnHighscore.setForeColor(Color.WHITE);
		 * btnOptions.setForeColor(Color.WHITE);
		 * btnHelp.setForeColor(Color.WHITE);
		 * btnAbout.setForeColor(Color.WHITE);
		 */
		//panel.addComponent(btnPlay);
		//panel.addComponent(btnHighscore);
		panel.addComponent(btnSearch);
		panel.addComponent(lstPlayer);
		//panel.addComponent(btnOptions);
		//panel.addComponent(btnHelp);
		//panel.addComponent(btnAbout);

		((TankGame)game).soundManager.loadMenuMusic();
		game.getWifiManagerP2P().SetEnableWifi(true);
		game.getWifiManagerP2P().setPeersAvailableListener(this);
		if(game.getWifiManagerP2P().getHost() != null){
			boolean isClose = game.getWifiManagerP2P().getHost().getSocket().isClosed();
			if(!isClose) {
				try {
					game.getWifiManagerP2P().getHost().getSocket().close();
					game.getWifiManagerP2P().getHost().getSendReceive().setSocket(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		game.getWifiManagerP2P().setP2PConnectionListener(new P2PConnectionListener() {
			@Override
			public void onConnect(boolean isError) {
				if(isError) {
					Log.e("Tanks","Connection failed!");
				}
				WorldScreen worldScreen=new WorldScreen(game);
				worldScreen.loadGame();
				((AndroidGame) game).setScreenWithFade(worldScreen);
				worldScreen.affterLoadGame();

			}
		});

		btnSearch.addListener(new ComponentClickListener() {
			@Override
			public void onClick(Component source) {
				game.getWifiManagerP2P().discover();
			}
		});

		lstPlayer.setComponentItemClickListener(new ComponentItemClickListener() {
			@Override
			public void onItemClick(Component source, int index) {
				game.getWifiManagerP2P().connect(index);
			}
		});

	}

	@Override
	public void update(float deltaTime) {
		if(((AndroidGame)game).isScreenTransitionActive()) return;
		
		Graphics g = game.getGraphics();
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		if(!lstPlayer.isVisible()) btnSearch.setVisible(true);
		
		if (touchEvents ==null || touchEvents.size()==0) return;
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			//btnPlay.processEvent(event);
			//btnHighscore.processEvent(event);
			//btnOptions.processEvent(event);
			lstPlayer.processEvent(event);
			btnSearch.processEvent(event);
			
			btnPlay.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					 WorldScreen worldScreen=new WorldScreen(game);
					 configureInputs();

					 ((AndroidGame) game).setScreenWithFade(worldScreen);
					  worldScreen.loadGame();
	            	 Log.i("Game","starts");
				}
			});
			
			btnHighscore.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					//((AndroidGame) game).setScreenWithFade(new HighScoreScreen(game));
					game.getWifiManagerP2P().discover();
				}
			});


			
			btnOptions.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					//game.setScreen(new GuiOptionScreen(game));
					
//				     Intent i = new Intent(((AndroidGame)game).getBaseContext(), SetPreferencesActivity.class);
//			            i.putExtra("controlConfig", false);
//			            ((AndroidGame)game).startActivity(i);
					game.getWifiManagerP2P().disconnect();
				}
			});

			btnHelp.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					game.getWifiManagerP2P().connect(0);
				}
			});
			btnAbout.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					Intent i = new Intent(((AndroidGame)game).getBaseContext(), SetPreferencesActivity.class);
                    i.putExtra("controlConfig", true);
                    ((AndroidGame)game).startActivity(i);  
				}
			});
		}
	}

	@Override
	public void paint(float deltaTime) {

		// game.getGraphics().drawARGB(155, 100, 100,100);
		//Canvas g = ((AndroidGraphics) (game.getGraphics())).getCanvas();
		//g.drawRGB((Color.BLACK & 0xff0000) >> 16, (Color.BLACK & 0xff00) >> 8,
		//		(Color.BLACK & 0xff));
		gameCanvas.drawRGB(Color.BLACK, Color.BLACK,Color.BLACK);
		gameCanvas.drawBitmap(background, null,new Rect(0,0,frameBuffer.getWidth(),frameBuffer.getHeight()), null);
		//gameCanvas.drawBitmap(TankResourceManager.logo,frameBuffer.getWidth()/2,15, null);
		panel.draw(gameCanvas, 0, -10);

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
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	private void configureInputs(){
	    	SharedPreferences prefs = ((AndroidGame)game).getSharedPreferences(PreferenceConstants.PREFERENCE_MAIN_NAME, 0);
	        final int lastVersion = prefs.getInt(PreferenceConstants.PREFERENCE_LAST_VERSION, 0);
	        if (lastVersion == 0) {
	        	// This is the first time the game has been run.  
	        	// Pre-configure the control options to match the device.
	        	// The resource system can tell us what this device has.
	        	// TODO: is there a better way to do this?  Seems like a kind of neat
	        	// way to do custom device profiles.
	        	final String navType = ((AndroidGame)game).getString(R.string.nav_type);
	        	mSelectedControlsString = ((AndroidGame)game).getString(R.string.control_setup_dialog_trackball);
	        	Log.i("Mario", "navType="+navType);
	        	if (navType != null) {
	        		if (navType.equalsIgnoreCase("DPad")) {
	        			// Turn off the click-to-attack pref on devices that have a dpad.
	        			SharedPreferences.Editor editor = prefs.edit();
	                	editor.putBoolean(PreferenceConstants.PREFERENCE_CLICK_ATTACK, false);
	                	editor.commit();
	                	mSelectedControlsString = ((AndroidGame)game).getString(R.string.control_setup_dialog_dpad);
	        		} else if (navType.equalsIgnoreCase("None")) {
	        			SharedPreferences.Editor editor = prefs.edit();
	        			//SensorManager manager = (SensorManager) ((AndroidGame)game).getSystemService(Context.SENSOR_SERVICE);
	        			if (((AndroidInput) game.getInput()).hasAccelerometer()) {
	        				//Log.i("Mario", "Accelerometer:"+manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size());
	        				// Turn on tilt controls if available
	                    	editor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, true);
	                    	editor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, false);
	                    	mSelectedControlsString = ((AndroidGame)game).getString(R.string.control_setup_dialog_tilt);
	         			}else{
	         				//Use on ScreenControl if there's nothing else.
	        				editor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, true);
	        				editor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false);
	        				mSelectedControlsString = ((AndroidGame)game).getString(R.string.control_setup_dialog_screen);
	        			}
	        			editor.commit();
	               	
	        		}			
	           	}
	        	SharedPreferences.Editor editor = prefs.edit();
	       	 	editor.putInt(PreferenceConstants.PREFERENCE_LAST_VERSION, TankGame.VERSION);
	        	editor.commit();
	        	Settings.loadPreferences((AndroidGame)game);
	        	((AndroidGame)game).runOnUiThread(new Runnable(){
	        		public void run(){
	        			showControls_SetUp_Dialog();
	        		}
	        	});
	    
	        }
	    }
	
	private void showControls_SetUp_Dialog(){
		String messageFormat =  ((AndroidGame)game).getResources().getString(R.string.control_setup_dialog_message);
		String message = String.format(messageFormat, mSelectedControlsString);
		CharSequence sytledMessage = Html.fromHtml(message);  // lame.
		AlertDialog dialog = new AlertDialog.Builder((AndroidGame) game)
		.setTitle(R.string.control_setup_dialog_title)
     	.setPositiveButton(R.string.control_setup_dialog_ok, null)
        .setNegativeButton(R.string.control_setup_dialog_change, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	Intent i = new Intent(((AndroidGame)game).getBaseContext(), SetPreferencesActivity.class);
                    i.putExtra("controlConfig", true);
                    ((AndroidGame)game).startActivity(i);  
                }
         })
	    .setMessage(sytledMessage)
		.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}


	@Override
	public void onPeersAvailable(WifiManagerP2P wifiManagerP2P) {
		btnSearch.setVisible(false);
		lstPlayer.setVisible(true);
		lstPlayer.setListText(wifiManagerP2P.getDeviceNameArray());
	}
}
