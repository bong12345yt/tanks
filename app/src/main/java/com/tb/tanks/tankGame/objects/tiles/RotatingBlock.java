package com.tb.tanks.tankGame.objects.tiles;


import android.graphics.Bitmap;

import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.core.tile.GameTile;


public class RotatingBlock extends GameTile {
	
	private Animation rotate;
	private Animation idle;
	
	public RotatingBlock(int pixelX, int pixelY) {
		
		// int pixelX, int pixelY, Animation anim, Image img, boolean isUpdateable
		super(pixelX, pixelY, null, null);
		
	}
	
	public void doAction() {
		setAnimation(rotate);
		setIsCollidable(false);
	}
}
