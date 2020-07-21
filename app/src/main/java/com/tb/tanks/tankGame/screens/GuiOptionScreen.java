package com.tb.tanks.tankGame.screens;

import android.graphics.Canvas;
import android.graphics.Color;

import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGraphics;
import com.tb.tanks.gui.AndroidButton;
import com.tb.tanks.gui.AndroidPanel;
import com.tb.tanks.gui.AndroidSlider;
import com.tb.tanks.gui.AndroidText;
import com.tb.tanks.gui.Component;
import com.tb.tanks.gui.ComponentClickListener;
import com.tb.tanks.gui.SliderChangeListener;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.framework.Input;

import java.util.List;


public class GuiOptionScreen extends Screen {

	AndroidPanel panel;
	AndroidButton btnOk;
	AndroidSlider sliderMusicVol, sliderSoundVol, sliderSensitivity;
	AndroidText txtMusic, txtSound, txtSensitivity;
	public int musicVol;// 0-10
	public int soundVol;// 0-10
	public int Sensitivity; // 0-10
	private int width,height;

	public GuiOptionScreen(Game game) {
		super(game);
		width = game.getScreenWidth();
		height = game.getScreenHeight();
		panel = new AndroidPanel(" OPTIONS ", 0, 0, width, height);
		panel.setTitleBarheight(40);
		panel.setForeColor(Color.WHITE);
		int sliderwidth = width/2;
		int ctrlx=width - sliderwidth + (sliderwidth-20)* Settings.musicVolume/100;
		sliderMusicVol = new AndroidSlider(width - sliderwidth -10,
				panel.getTitleBarheight() + 30, sliderwidth, 15, ctrlx);//width - sliderwidth -10 +  20);
		sliderMusicVol.addListener(new SliderChangeListener(){
			@Override
			public void onChange(Component source, int val) {
				Settings.musicVolume=val;
				TankSoundManager.setMusicVolume(val/100.0f);
			}
		});
		
		ctrlx=width - sliderwidth + (sliderwidth-20)*Settings.soundVolume/100;
		
		sliderSoundVol = new AndroidSlider(width - sliderwidth -10,
				panel.getTitleBarheight() + 80, sliderwidth, 15,  ctrlx);//width - sliderwidth -10 +  20);
		sliderSoundVol.addListener(new SliderChangeListener(){
			@Override
			public void onChange(Component source, int val) {
				Settings.soundVolume=val;
				TankSoundManager.setSoundVolume(sliderSoundVol.getValue()/100.0f);
			}
		});
		
		ctrlx=width - sliderwidth + (sliderwidth-20)*Settings.tiltSensitivity/100;
		sliderSensitivity = new AndroidSlider(width - sliderwidth -10,
				panel.getTitleBarheight() + 130, sliderwidth, 15, ctrlx);//width - sliderwidth -10 +  20);
		sliderSensitivity.addListener(new SliderChangeListener(){
			@Override
			public void onChange(Component source, int val) {
				Settings.setSensitivity(val);
			}
		});
		
		btnOk = new AndroidButton("OK", width/2- 40, panel.getTitleBarheight() + 160,
				80, 30);

		txtMusic = new AndroidText("MusicVol", 10,
				panel.getTitleBarheight()+30 , width/3, 5);
		txtMusic.setAlign(0);
		txtMusic.setTextSize(20);

		txtSound = new AndroidText("SoundVol", 10,
				panel.getTitleBarheight() + 80, width/3, 5);
		txtSound.setAlign(0);
		txtSound.setTextSize(20);

		txtSensitivity = new AndroidText("Sensitivity", 10,
				panel.getTitleBarheight() + 130, width/3, 5);
		txtSensitivity.setAlign(0);
		txtSensitivity.setTextSize(20);

		btnOk.setTextSize(20);
		panel.addComponent(sliderMusicVol);
		panel.addComponent(txtMusic);
		panel.addComponent(sliderSoundVol);
		panel.addComponent(sliderSensitivity);
		panel.addComponent(btnOk);
		panel.addComponent(txtSound);
		panel.addComponent(txtSensitivity);
	}

	@Override
	public void update(float deltaTime) {
		Graphics g = game.getGraphics();
		List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			Input.TouchEvent event = touchEvents.get(i);
			sliderMusicVol.processEvent(event);
			sliderSoundVol.processEvent(event);
			sliderSensitivity.processEvent(event);
			btnOk.processEvent(event);
			btnOk.addListener(new ComponentClickListener() {
				@Override
				public void onClick(Component c) {
					game.setScreen(new GuiMenuScreen(game));
					//Settings.save(game.getFileIO());
				}
			});
		}
	}

	@Override
	public void paint(float deltaTime) {

		// game.getGraphics().drawARGB(155, 100, 100,100);
		Canvas g = ((AndroidGraphics) (game.getGraphics())).getCanvas();
		g.drawRGB((Color.BLACK & 0xff0000) >> 16, (Color.BLACK & 0xff00) >> 8,
				(Color.BLACK & 0xff));

		panel.draw(g, 0, 0);
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
		game.setScreen(new GuiMenuScreen(game));
	}

}
