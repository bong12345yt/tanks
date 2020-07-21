package com.tb.tanks.tankGame.core.tile;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tb.tanks.tankGame.core.animation.Animatible;
import com.tb.tanks.tankGame.core.animation.Animation;


public class Tile extends Animatible {
	
	// fields
	private int tileX;
	private int tileY;
	private float pixelX;
	private float pixelY;
	protected Bitmap img;
	
	public Tile(float pixelX, float pixelY, Animation anim, Bitmap img) {

		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.img = img;
		setAnimation(anim);
	}
	
	public Tile(int pixelX, int pixelY, Bitmap img) {
		this(pixelX, pixelY, null, img);
	}
	
	public void draw(Canvas g, float pixelX, float pixelY) {
		g.drawBitmap(getImage(), pixelX, pixelY, null);
	}
	
	public void draw(Canvas g, float pixelX, float pixelY, float offsetX, float offsetY) {
		draw(g, pixelX + offsetX, pixelY + offsetY);
	}
	
	public Bitmap getImage() {
		return (currentAnimation() == null) ? img : currentAnimation().getImage();
	}
	
	public float getPixelX() {
		return pixelX;
	}
	
	public float getPixelY() {
		return pixelY;
	}
	
	public int getWidth() {
		return getImage().getWidth();
	}
	
	public int getHeight() {
		return getImage().getHeight();
	}

	public int getTileX() {
		return tileX;
	}

	public void setTileX(int tileX) {
		this.tileX = tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public void setTileY(int tileY) {
		this.tileY = tileY;
	}
} // Tile
