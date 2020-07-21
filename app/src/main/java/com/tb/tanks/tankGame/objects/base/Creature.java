package com.tb.tanks.tankGame.objects.base;

import android.graphics.Point;
import android.graphics.RectF;

import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.core.animation.CollidableObject;
import com.tb.tanks.tankGame.core.animation.Sprite;
import com.tb.tanks.tankGame.core.tile.GameTile;
import com.tb.tanks.tankGame.core.tile.TileMap;


public class Creature extends CollidableObject {
	
	protected static int xCollideOffset = 2; 
	protected static int offMapOffset = 15;
	protected static float GRAVITY = .0007f; //0.0008f
	protected float gravityEffect = .20f; 
	
	// Wake up values are constants based on the number of tiles on the screen
	// that are used to determine when mario comes within range of a creature.
	// Used exclusively within GameRender.java.
	public static int WAKE_UP_VALUE_DOWN_RIGHT = 30;
	public static int WAKE_UP_VALUE_UP_LEFT = -3;
	
	/* 
	 * Creature Attributes:
	 * 
	 * Relevant:  A creature that is always relevant must be updated every frame. By default, no creature
	 * 			  is always relevant. 
	 * Alive:     A creature that is on the map is alive. All creatures start alive and can be killed using
	 * 			  the kill() method.
	 * Sleeping:  A creature that is sleeping has yet to be seen by the player. All creatures start out
	 * 			  sleeping, and can be woken up using wakeUp(). They cannot be put back to sleep.
	 * Flipped:   isFlipped is a flag used to determine when to change the animation of a creature to death.
	 * 			  For example, a goomba that is hopped on is 'flipped', then removed from the game.
	 * Item:      A creature that is an item represents an item the player can interact with.
	 * Platform:  A creature is a platform if it is a non-aligned moving object the player
	 * 			  and creatures can interact with. 
	 * Invisible: When a creature is invisible, it isn't drawn.
	 * 
	 * Gravityfactor: g=g*gravityFactor
	 */
	private boolean isAlwaysRelevant; 
	private boolean isAlive; 
	private boolean isSleeping; 
	private boolean isFlipped;
	private boolean isItem;
	private boolean isPlatform;
	private boolean isInvisible;
	protected float gravityFactor=1;
	protected boolean inWater=false;
	public static TileMap map;
	
	public Creature() { 
		this(0, 0, null);
	}
	
	public Creature(int pixelX, int pixelY) {
		this(pixelX, pixelY, null);
	}
	
	/**
	 * @effects Creates a new Creature at the given pixelX, pixelY position that is capable
	 * of producing sounds from the soundManager. 
	 * 
	 * True: Collidable, Alive, Sleeping, Flipped.
	 * False: OnScreen, Item, Platform, Relevant.
	 */
	public Creature(int pixelX, int pixelY, TankSoundManager soundManager) {
		super(pixelX, pixelY, soundManager);	
		setIsCollidable(true);
		isAlive = true;
		isSleeping = true;
		isFlipped = false;
		setIsOnScreen(false);
		isItem = false;
		isPlatform = false;
		isAlwaysRelevant = false;
	}
	
	/**
	 * @return true if this creature is a Platform, false otherwise.
	 */
	public boolean isPlatform() {
		return isPlatform;
	}
	
	/**
	 * @modifies the platform status of this Creature.
	 */
	public void setIsPlatform(boolean isPlatform) {
		this.isPlatform = isPlatform;
	}
	
	/**
	 * @return true if this creature is an Item, false otherwise.
	 */
	public boolean isItem() {
		return isItem;
	}
	
	/**
	 * @modifies the item status of this Creature (items do not collide with other items/ creatures ex. mushroom).
	 */
	public void setIsItem(boolean isItem) {
		this.isItem = isItem;
	}
	
	/**
	 * @return true if this creature is flipped, false otherwise.
	 */
	public boolean isFlipped() {
		return isFlipped;
	}
	
	/**
	 * @modifies the flipped status of this Creature.
	 */
	public void setIsFlipped(boolean isFlipped) {
		this.isFlipped = isFlipped;
	}
	
	/**
	 * @return true if this creature is sleeping, false otherwise.
	 */
	public boolean isSleeping() {
		return isSleeping;
	}
	
	/**
	 * @modifies the sleeping status of this creature to false.
	 */
	public void wakeUp() { 
		isSleeping = false;
	}
	
	/**
	 * @modifies the sleeping status of this creature to false.
	 * @param isLeft true if creative should begin moving left
	 */
	public void wakeUp(boolean isLeft) { 
		isSleeping = false;
	}
	
	/**
	 * @return true if this creature is alive, false otherwise.
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * @modifies the life state of this creature (alive or dead) to dead.
	 */
    public void kill() {
    	isAlive = false;
    }
	
	/**
	 * @return true if this creature is a Platform, false otherwise.
	 */
	public boolean isAlwaysRelevant() {
		return isAlwaysRelevant;
	}
	
	/**
	 * @modifies the platform status of this Creature.
	 */
	public void setIsAlwaysRelevant(boolean isAlwaysRelevant) {
		this.isAlwaysRelevant = isAlwaysRelevant;
	}
	
	/**
	 * @return true if this creature is invisible, false otherwise.
	 */
	public boolean isInvisible() {
		return isInvisible;
	}
	
	/**
	 * @modifies the invisible status of this Creature.
	 */
	public void setIsInvisible(boolean isInvisible) {
		this.isInvisible = isInvisible;
	}
    
	
	public void jumpedOn() { }
	public void flip() { }
	
	// for tile collisions
	public void xCollide(Point p) {
		if(dx > 0) {
			x = x - xCollideOffset;
		} else {
			x = x + xCollideOffset;
		}
		dx = -dx;
	}
	
	// for creature collisions
	public void creatureXCollide() {
		if(dx > 0) {
			x = x - xCollideOffset;
		} else {
			x = x + xCollideOffset;
		}
		dx = -dx;
	}
	
	/**
	 * Calculates the type of collision in the X direction between a Tile 
	 * and a Sprite given the Sprite is currentely colliding with the tile. 
	 * This method relies on the general heuristic that if two 
	 * rectangular objects are colliding, then one object is not completely
	 * contained in the other. Because the colliding objects stick out, we
	 * know the direction of the collision. 
	 * 
	 * pre-condition: sprite is colliding with tile.
	 * @return Collision.WEST if sprite is colliding with the tile from the west or
	 * Collision.EAST if sprite is colliding with the tile from the east.
	 */
	public static Collision tileCollisionX(GameTile tile, Sprite s) {
		if(s.getX() > tile.getPixelX()) {
			return Collision.WEST;
		} else {
			return Collision.EAST;
		}
	}
	
	/**
	 * Calculates the type of collision in the Y direction between a Tile 
	 * and a Sprite given the Sprite is currentely colliding with the tile. 
	 * This method relies on the general heuristic that if two 
	 * rectangular objects are colliding, that one object is not completely
	 * contained in the other. Because the colliding objects stick out, we
	 * know the direction of the collision. 
	 * 
	 * pre-condition: sprite is colliding with tile.
	 * @return Collision.NORTH if sprite is colliding with the tile from the north or
	 * Collision.SOUTH if sprite is colliding with the tile from the south.
	 */
	public static Collision tileCollisionY(GameTile tile, Sprite s) {
		if(s.getY() < tile.getPixelY()) {
			return Collision.NORTH;
		} else {
			return Collision.SOUTH;
		}
	}
	
	/**
	 * updates creature position and velocity, also updates animation
	 * @param map
	 * @param time  DeltaTime in millisecond
	 */
	public void updateCreature(TileMap map, int time) {

	}
	
	/**
	 * This is called when creature is walking on tiles(not in air)so that
	 * creature can do some intelligent work by overriding it
	 */
	protected void useAI(TileMap map) {
		/* don't let it go beyond mapfall
		if (x <= 0 || x > map.getWidth() * 16) {
			this.xCollide(null);
		}
		*/
	}

	// Determines what happens when two different creatures collide.
	// Uncommenting the onSreen condition makes this more efficient, but more buggy
	public void creatureCollision(Creature creature) {

	}

	/**
	 * checks if two overlapping creatures actually collide or not (if their
	 * velocity of approach is positive they are said to be collided else they are separating from each other)
	 * @param c
	 * @return
	 */
	private boolean xCollideWithCreature(Creature c){
		if (x<c.getX()){
			if (dx-c.dx>0) return true;
		}else{
			if (c.dx-dx>0) return true;
		}
		return false;
		
	}
	
	public float getGravityFactor() {
		return gravityFactor;
	}

	public void setGravityFactor(float gravityFactor) {
		this.gravityFactor = gravityFactor;
	}
	
	private String getBonusInfo(int creatureHitCount){
		  return "INCREDIBLE !!!";

	}

	void coll(){
		RectF a = new RectF (0,0,3,3);
		RectF  b = new RectF (1,0,3,3);
		a.intersect(b);
	}

	
}
