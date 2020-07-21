package com.tb.tanks.tankGame.core;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.tb.tanks.tankGame.core.animation.Sprite;
import com.tb.tanks.tankGame.core.tile.GameTile;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.tank.Tank;

import java.util.ArrayList;


/**
 The GameRenderer class is responsible for all the drawing onto the screen.
 Also contains useful static methods for converting tiles->pixels, pixels->tiles
 and a method for locating which tile a sprite has collided with.
*/
public class GameRenderer {
    private boolean drawHudEnabled=true;
    private Bitmap background;
    public static int xOffset=0;
	public static int yOffset=0;
    // Converts a pixel position to a tile position.

    // Converts a pixel position to a tile position.
    public static int pixelsToTiles(int pixels, int bit_size) {
        // use shifting to get correct values for negative pixels
        return pixels >> bit_size;
        // or, for tile sizes that aren't a power of two,
        // use the floor function: return (int)Math.floor((float)pixels / TILE_SIZE);
    }

    // Converts a tile position to a pixel position.
    public static int tilesToPixels(int numTiles, int bit_size) {
        // no real reason to use shifting here. it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << bit_size;
        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }

    // Sets the background to draw.
    public void setBackground(Bitmap background) {
        this.background = background;
    }
    
	// Returns the tile that a Sprite has collided with. Returns null if no 
	// collision was detected. The last parameter, right, is used to check if multiple blocks
	// are hit when a sprite jumps.
	public static Point getTileCollision(TileMap map, Sprite sprite, float currX, float currY, float newX, float newY) {


	    // no collision found, return null
	    return null;
	}
	
	/**
	 * @return A List of Points, where each Point corresponds to the location of a tile the sprite is 
	 * colliding with in map.tiles().
	 */
	public static ArrayList<Point> getTileCollisionAll(TileMap map, Sprite sprite, float currX, float currY, float newX, float newY) {
		

	    // no collision found, return null
	    return null;
	}
    
	/**
	 * Draws all game elements. I did the best I can to separate all updating
	 * from drawing. However, it seems its much more efficient to do some
	 * updating here where I have all the information I need to make important
	 * decisions. So calling draw() DOES change the game state.
	 */
	public void draw(Canvas g, TileMap mainMap, TileMap backgroundMap,
                     TileMap foregroundMap, int screenWidth, int screenHeight) {
		// add the three maps to the list of maps to draw, only mainMap is
		// interactive
		g.drawRGB(Color.BLACK, Color.BLACK,Color.BLACK);

		Tank player = mainMap.getPlayer();
		Tank playerOther = mainMap.getPlayerOther();

		if (background != null) {
			g.drawBitmap(background, screenWidth/2 - player.getX(), screenHeight/2 - player.getY(), null);
		}

		((Tank) player).draw(g, (screenWidth / 2)
						,(screenHeight / 2) ,
				player.getOffsetX(), player.getOffsetY());



		((Tank) playerOther).draw(g, (screenWidth / 2 + playerOther.getX() - player.getX())
				,(screenHeight / 2 + playerOther.getY() - player.getY()) ,
				playerOther.getOffsetX(), playerOther.getOffsetY());


		GameTile[][] tiles = mainMap.getTiles();
		int sizei = tiles.length;
		for(int i = 0; i < sizei; i++){
			int sizej = tiles[i].length;
			for(int j = 0; j < sizej; j++){
				GameTile tile = mainMap.getTiles()[i][j];
				if (tile != null){
					float px = tile.getPixelX();
					float py = tile.getPixelY();
					tile.draw(g, screenWidth/2 + px,screenHeight/2 + py,  -player.getX(), -player.getY());
					if(tile.getBody2D() != null){
						tile.getBody2D().draw(g, screenWidth/2 + px - player.getX(), screenHeight/2 + py -(int)player.getY() );
					}
				}

			}
		}
		((Tank) playerOther).drawFireShotFlames(g,(screenWidth / 2)- player.getX(),(screenHeight / 2) - player.getY());

		((Tank) playerOther).drawBullets(g,(screenWidth / 2)- player.getX(),(screenHeight / 2) - player.getY());

		((Tank) player).drawFireShotFlames(g, (screenWidth / 2)- player.getX(),(screenHeight / 2) - player.getY());

		((Tank) player).drawBullets(g, (screenWidth / 2)- player.getX(),(screenHeight / 2) - player.getY());

		if(((Tank) playerOther).isAlive()){
			((Tank) playerOther).getHealthBar().draw(g,(screenWidth / 2)- player.getX() + ((Tank) playerOther).getHealthBar().getX() , (screenHeight / 2) - player.getY() + ((Tank) playerOther).getHealthBar().getY());
		}

		if(((Tank) player).isAlive()) {
			((Tank) player).getHealthBar().draw(g, (screenWidth / 2) - player.getX() + ((Tank) player).getHealthBar().getX(), (screenHeight / 2) - player.getY() + ((Tank) player).getHealthBar().getY());
		}

		((Tank) playerOther).getExplosion().draw(g, (screenWidth / 2) - player.getX() + ((Tank) playerOther).getExplosion().getX(), (screenHeight / 2) - player.getY() + ((Tank) playerOther).getExplosion().getY());

		((Tank) player).getExplosion().draw(g, (screenWidth / 2) - player.getX() + ((Tank) player).getExplosion().getX(), (screenHeight / 2) - player.getY() + ((Tank) player).getExplosion().getY());


    }
    
    
    private void drawHud(Canvas g){

        //g.drawText("-"+Settings.getLives(), MarioResourceManager.Mario_Big_Crouch_Right.getWidth()+3, 5,paint);
    }
    
	public void drawText(Canvas g, String line, int x, int y) {
		int len = line.length();
		Rect srcRect=new Rect(0,0,x+11,11);
		Rect dstRect=new Rect(x,y,x+11,y+11);
		int pos;
		for (int i = 0; i < len; i++) {
			char character = line.charAt(i);
			int srcX = 0;
			pos=(character - '0') * 11;
			
			if (pos>10 || pos <0) {
				pos=10;
			}
			srcX = pos * 11;
			srcRect.left=srcX;
			srcRect.right=srcX+11;
			
			dstRect.left=x;
			dstRect.right=x+11;
			
			//g.drawBitmap(MarioResourceManager.digits[pos],srcRect,dstRect,null);
			x += 11;
		}
	}
	
	public static void drawStringDropShadowAsEntity(Canvas g, String text,
                                                    int x, int y, int type, int alignmrnt) {
		drawString(g, text, x, y, xOffset, type,alignmrnt);
	}

	public static void drawStringDropShadowAsHud(Canvas g, String text, int x,
                                                 int y, int type, int alignment) {
		drawString(g, text, x, y, 0,type,alignment);
	}

	public static void drawStringDropShadow(Canvas g, String text, int x,
                                            int y, int type, int alignment) {
		drawString(g, text, x, y, 0,type,alignment);
	}
	
	/**
	 * 
	 * @param g
	 * @param text
	 * @param x
	 * @param y
	 * @param offset
	 * @param type
	 * @param alignment  -1==left, 0 ==centre, 1=right
	 */
	private static void drawString(Canvas g, String text, int x, int y, int offset, int type, int alignment) {
		if (alignment==0){
			if (type==1)
				x-=text.length()/2*12;
			else if (type==2)			
				x-=text.length()/2*16;
			else
				x-=text.length()/2*8;
		}else if(alignment==-1){
			if (type==1)
				x-=text.length()*12;
			else if (type==2)			
				x-=text.length()*16;
			else
				x-=text.length()*8;
		}
		
		x = x + offset;
		char[] ch = text.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (type==1)
				g.drawBitmap(TankResourceManager.fontMedium[ch[i] - 32], x + i
						* 8, y, null);
			else if(type==2)
				g.drawBitmap(TankResourceManager.fontLarge[ch[i] - 32], x + i
						* 16, y, null);
			else
				g.drawBitmap(TankResourceManager.fontSmall[ch[i] - 32], x + i
						* 8, y, null);
			
		}
	}

	public boolean isDrawHudEnabled() {
		return drawHudEnabled;
	}

	public void setDrawHudEnabled(boolean drawHudEnabled) {
		this.drawHudEnabled = drawHudEnabled;
	}

}
