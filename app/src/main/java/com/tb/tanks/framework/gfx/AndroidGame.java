package com.tb.tanks.framework.gfx;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.tb.tanks.ConnectionP2P.WifiManagerP2P;
import com.tb.tanks.MainActivity;
import com.tb.tanks.R;
import com.tb.tanks.framework.Audio;
import com.tb.tanks.framework.FileIO;
import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Input;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.fileio.AndroidFileIO;
import com.tb.tanks.framework.input.AndroidInput;
import com.tb.tanks.framework.input.JoyStickEvent;
import com.tb.tanks.framework.sfx.AndroidAudio;
import com.tb.tanks.tankGame.core.Settings;
import com.erz.joysticklibrary.*;

/**
 * 
 * <li>Perform window management. In our context, this means setting up an
 * activity and an AndroidFastRenderView, and handling the activity life cycle
 * in a clean way.</li> <li>Use and manage a WakeLock so that the screen does
 * not dim. Instantiate and hand out references to Graphics, Audio, FileIO, and
 * Input to interested parties.</li> <li>Manage Screens and integrate them with
 * the activity life cycle.</li>
 * 
 * @author mahesh
 * 
 */
public abstract class AndroidGame extends Activity implements Game, JoyStick.JoyStickListener {
	protected GameView renderView;
	protected Graphics graphics;
	protected Audio audio;
	protected Input input;
	protected FileIO fileIO;
	protected Screen screen;
	protected WakeLock wakeLock;
	protected Bitmap frameBuffer;
	protected int WIDTH;
	protected int HEIGHT=1024;
	private boolean switchScreen =false;
	protected Screen nextScreen=null;
	protected boolean screenTransitionActive=false;
	private JoyStick joyStick;
	private JoyStickEvent joyStickEvent;
	private WifiManagerP2P wifiManagerP2P;
	private ImageButton fireButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			View decorView = getWindow().getDecorView();
			// Hide the status bar.
			int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
		//check the current orientation of the device and set the Width and Height of our Game. 
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		float h=metrics.heightPixels;
		float w=metrics.widthPixels;

		Point size = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getWindowManager().getDefaultDisplay().getRealSize(size);
			h = size.y;
			w = size.x;
		}

		Log.v("mario"," window width & height (in pixels): " + w + ", " + h);
		float aspectRaio=w/h;
		//adjust width according to the aspect ratio, using this we can deal with any resolution.
		WIDTH=(int) (HEIGHT*aspectRaio);
		
		int frameBufferWidth = isLandscape ? WIDTH : HEIGHT;
		int frameBufferHeight = isLandscape ? HEIGHT : WIDTH;
		frameBuffer = Bitmap.createBitmap(frameBufferWidth,
				frameBufferHeight, Config.RGB_565);
		// proceed by creating floats to scale and adjust everything to the device's aspect ratio.
		float scaleX = (float) frameBufferWidth	/ w;
		float scaleY = (float) frameBufferHeight/ h;
		screen = getStartScreen();
		renderView = new GameView(this, frameBuffer);
		  // set up a new game
		graphics = new AndroidGraphics(getAssets(), frameBuffer);
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, renderView, scaleX, scaleY);
		FrameLayout game = new FrameLayout(this);
		game.addView(renderView);
		float density = getResources().getDisplayMetrics().density;

		fireButton = new ImageButton(this);
		FrameLayout.LayoutParams pr = new FrameLayout.LayoutParams((int)(70*density), (int)(70*density), Gravity.BOTTOM| Gravity.RIGHT);
		pr.setMargins(0,0,50,50);
		fireButton.setLayoutParams(pr);
		fireButton.setBackground(ContextCompat.getDrawable(this,R.drawable.round_button));

		joyStick = new JoyStick(this);
		joyStickEvent = new JoyStickEvent();


		joyStick.setLayoutParams(new FrameLayout.LayoutParams((int)(130*density), (int)(130*density), Gravity.BOTTOM));
		joyStick.setPadColor(Color.argb(50, 30,30,30));
		joyStick.setListener(this);

		joyStick.setVisibility(View.GONE);
		fireButton.setVisibility(View.GONE);

		//joyStick.setVisibility(View.GONE); //View.VISIBLE
		game.addView(joyStick);
		game.addView(fireButton);
		setContentView(game);
		//we also use the PowerManager to define the wakeLock variable and we acquire and 
		//release wakelock in the onResume and onPause methods, respectively. 
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
		wifiManagerP2P = new WifiManagerP2P(this);
	}



	@Override
	public void onBackPressed() {
		if (!screenTransitionActive)getCurrentScreen().onBackPressed();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
		screen.resume();
		renderView.getThread().unpause();
		Settings.loadPreferences(this);
		((AndroidInput) input).registerAccListener();
		registerReceiver(wifiManagerP2P.getReceiver(), wifiManagerP2P.getIntentFilter());
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			View decorView = getWindow().getDecorView();
			// Hide the status bar.
			int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	public void onMove(JoyStick joyStick, double angle, double power, int direction) {
		joyStickEvent.angle = angle;
		joyStickEvent.power = power;
		joyStickEvent.direction = direction;
	}

	public ImageButton getFireButton() {
		return fireButton;
	}

	@Override
	public void onTap() {

	}

	@Override
	public void onDoubleTap() {

	}

	@Override
	public void onPause() {
		super.onPause();
		wakeLock.release();
		renderView.getThread().pause();
		screen.pause();
		if (isFinishing())
			screen.dispose();
		((AndroidInput) input).unRegisterAccListener();
		unregisterReceiver(wifiManagerP2P.getReceiver());
	}

	public Input getInput() {
		return input;
	}

	public FileIO getFileIO() {
		return fileIO;
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public Audio getAudio() {
		return audio;
	}

	public JoyStick getJoyStick(){ return joyStick; }

	public JoyStickEvent getJoyStickEvent(){return joyStickEvent;}

	public void ShowJoyStick(final boolean visible){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(visible){
					joyStick.setVisibility(View.VISIBLE);
				}else{
					joyStick.setVisibility(View.GONE);
				}
			}

		});

	}

	public void ShowFireButton(final boolean visible){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(visible){
					fireButton.setVisibility(View.VISIBLE);
				}else{
					fireButton.setVisibility(View.GONE);
				}
			}

		});

	}

	@Override
	public WifiManagerP2P getWifiManagerP2P() {
		return wifiManagerP2P;
	}

	public void setScreen(Screen screen) {
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");
		this.screen.dispose();
		this.screen = screen;
		//screen.resume();
		screen.update(0);
		this.nextScreen=null;
		getInput().getKeyEvents().clear();
		getInput().getTouchEvents().clear();
	}

	public void setScreenWithFade(Screen screen) {
		if (screenTransitionActive) return;
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");
		this.nextScreen=screen;
		setSwitchScreen(true);
		setScreenTransitionActive(true);
	}

	public Screen getCurrentScreen() {
		return screen;
	}
	
	/**
	 * returns Buffer Bitmap of FastRendererView associated with game
	 * @return
	 */
	public Bitmap getBuffer(){
		return frameBuffer;
	}
	
	public void setBuffer(Bitmap buffer){
		frameBuffer=buffer;
		renderView.setBuffer(frameBuffer);

	}
	
	public int getScreenWidth(){
		return WIDTH;
	}
	
	public int getScreenHeight(){
		return HEIGHT;
	}

	public boolean isSwitchingScreen() {
		return switchScreen;
	}

	public void setSwitchScreen(boolean switchScreen) {
		this.switchScreen = switchScreen;
	}
	
	public Screen getNextScreen(){
		return nextScreen;
		/*
		if (nextScreen!=null){
			return nextScreen;
		}else{
			return screen;
		}
		*/
	}

	public boolean isScreenTransitionActive() {
		return screenTransitionActive;
	}

	public void setScreenTransitionActive(boolean screenTransitionActive) {
		this.screenTransitionActive = screenTransitionActive;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}