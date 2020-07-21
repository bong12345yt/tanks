package com.tb.tanks.tankGame.objects.tiles;

import android.graphics.Bitmap;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.tile.GameTile;
import com.tb.tanks.tankGame.core.tile.TileMap;

import java.util.Random;


public class QuestionBlock extends GameTile {

	private TankSoundManager soundManager;
	private TileMap map;
	
	private Animation active;
	private Animation dead;
	private boolean isActive;
	private boolean hasCoin;
	private boolean hasMushroom;
	
	public QuestionBlock(int pixelX, int pixelY, TileMap map, TankSoundManager soundManager, boolean hasCoin,
						 boolean hasMushroom) {
		
		// int pixelX, int pixelY, Animation anim, Image img
		super(pixelX, pixelY, null, null);
		
		setIsSloped(false);
		isActive = true;
		this.hasCoin = hasCoin;
		this.hasMushroom = hasMushroom;
		this.soundManager = soundManager;
		this.map = map;

		
		Random r = new Random();
		setAnimation(active);
	}
	
	public void update(int time) {
		super.update(time);
		if(getOffsetY() != 0) { setOffsetY(getOffsetY() + 2); }
	}
	
	public void doAction() {

	}
}