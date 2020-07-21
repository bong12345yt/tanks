package com.tb.tanks.tankGame.screens;

import android.graphics.Canvas;
import android.widget.ListView;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.framework.gfx.AndroidGraphics;
import com.tb.tanks.gui.AndroidButton;
import com.tb.tanks.gui.AndroidListView;
import com.tb.tanks.gui.AndroidPanel;
import com.tb.tanks.gui.AndroidPic;
import com.tb.tanks.gui.AndroidText;
import com.tb.tanks.gui.Component;
import com.tb.tanks.gui.ComponentClickListener;
import com.tb.tanks.tankGame.core.GameRenderer;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.Settings;

import java.util.List;

public class GuiLevelCompleteScreen extends Screen {

	AndroidPanel panel;
	AndroidButton btnReplay, btnNext, btnMenu;
	AndroidPic imgStars;
	private int width,height;
	private int panelX = 70, panelY = 50;
	private AndroidText txtScore;
	private AndroidText txtScoreValue;
    
	private boolean drawMsg=false;
	String msg="Congratulations!!!";
	
	public GuiLevelCompleteScreen(Game game) {
		super(game);
		width = game.getScreenWidth();
		height =game.getScreenHeight();
		int h = height - 2 * panelY;
		int w = width - 2 * panelX;
		panel = new AndroidPanel("HIGHSCORE : 123450", panelX, panelY, w, h);
		panel.setTitleBarheight(30);
		int tbh = panel.getTitleBarheight();
		btnReplay = new AndroidButton("REPLAY", 10, h - 35, 80, 25);
		btnReplay.setTextSize(16);
		btnNext = new AndroidButton("NEXT", 90 + (w - 230) / 2, h - 35, 60, 25);
		btnNext.setTextSize(16);
		btnMenu = new AndroidButton("MENU", w - 80, h - 35, 70, 25);
		btnMenu.setTextSize(16);
		txtScore = new AndroidText("Score:", 10, tbh , 100, 30);
		txtScore.setTextSize(18);
		txtScoreValue = new AndroidText("12345", 10, tbh + 20, 100, 30);
		txtScoreValue.setTextSize(14);
		imgStars = new AndroidPic(TankResourceManager.star3, "", w
				- TankResourceManager.star3.getWidth() - 10, tbh ,
				TankResourceManager.star3.getWidth(),
				TankResourceManager.star3.getHeight());
		panel.addComponent(btnReplay);
		panel.addComponent(btnNext);
		panel.addComponent(btnMenu);
		panel.addComponent(txtScore);
		panel.addComponent(txtScoreValue);
		panel.addComponent(imgStars);
		
		game.setScreen(new GameScreen(game));

	}

	@Override
	public void update(float deltaTime) {
		//Graphics g = game.getGraphics();
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		if (drawMsg){
			if (len>0 & drawMsg)((AndroidGame) game).setScreenWithFade(new GameScreen(game));
			return;
		}
		
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			btnReplay.processEvent(event);
			btnNext.processEvent(event);
			btnMenu.processEvent(event);
			btnReplay.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					//game.gotoNextlevel();
					game.setScreen(new GameScreen(game));
				}
			});
			btnNext.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					Settings.level++;
					if(Settings.level>3){
						Settings.level=1;
						Settings.world++;
						drawMsg=true;
						if (Settings.getWorldsUnlocked()<Settings.world){
							Settings.setWorldsUnlocked(Settings.world);
						}
					}else{
						game.setScreen(new GameScreen(game));
						if (Settings.getLevelsUnlocked()<=Settings.level)Settings.setLevelsUnlocked(Settings.level+1);
					}
				}
			});
			btnMenu.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					game.setScreen(new GuiMenuScreen(game));
				}
			});
		}
	}

	@Override
	public void paint(float deltaTime) {
		// game.getGraphics().drawARGB(155, 100, 100,100);
		Canvas g = ((AndroidGraphics) (game.getGraphics())).getCanvas();
		//g.drawRGB((Color.BLACK & 0xff0000) >> 16, (Color.BLACK & 0xff00) >> 8,
		//		(Color.BLACK & 0xff));
		//g.drawARGB(125, 0, 0, 0);
		if (drawMsg){
			g.drawARGB(125, 0, 0, 0);
			GameRenderer.drawStringDropShadowAsHud(g, "Congratulations!!!", game.getScreenWidth()/2, game.getScreenHeight()/2,2,0);
			GameRenderer.drawStringDropShadowAsHud(g, "You have cleared world. " +(Settings.world-1) + "Tap to enter next world.",  game.getScreenWidth()/2, game.getScreenHeight()/2,1,0);
			
		}else{
			panel.draw(g, 0, 0);
		}
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
		((AndroidGame) game).setScreenWithFade(new GuiMenuScreen(game));
	}

}
