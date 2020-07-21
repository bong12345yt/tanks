package com.tb.tanks.tankGame.core.tile;


import android.graphics.Bitmap;

import com.tb.tanks.physic.RecBody2D;
import com.tb.tanks.tankGame.core.animation.Animation;
import com.tb.tanks.tankGame.objects.base.Creature;

import java.util.LinkedList;
import java.util.List;


public class GameTile extends Tile {
	
	// fields
	private boolean isCollidable = true;
	private boolean isSloped = false;
	private List<Creature> collidingCreatures;

	private RecBody2D body2D = null;
	
	/**
	 * Constructs a new GameTile at the pixel (x,y) position with the Animation anim
	 * and Image img.
	 */
	public GameTile(float pixelX, float pixelY, Animation anim, Bitmap img) {
		super(pixelX, pixelY, anim, img);
		collidingCreatures = new LinkedList<Creature>();
	}

	public void setBody2D(RecBody2D body2D) {
		this.body2D = body2D;
	}

	public RecBody2D getBody2D(){
		return body2D;
	}

	/**
	 * Constructs a new GameTile at the pixel (x,y) position with no Animation
	 * and the constant Image img.
	 */
	public GameTile(float pixelX, float pixelY, Bitmap img) {
		this(pixelX, pixelY, null, img);
	}
	
	/**
	 * Override to add action to this GameTile.
	 */
	public void doAction() { }
	
	/**
	 * @return true if this GameTile is collidable, else false.
	 */
	public boolean isCollidable() {
		return isCollidable;
	}
	
	/**
	 * @effects sets isCollidable to true or false.
	 */
	public void setIsCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}
	
	public boolean isSloped() {
		return isSloped;
	}
	
	public void setIsSloped(boolean isSloped) {
		this.isSloped = isSloped;
	}

	/**
	 * @return a list of Creatures who are currently colliding with this GameTile.
	 */
	public List<Creature> collidingCreatures() {
		return collidingCreatures;
	}
}
