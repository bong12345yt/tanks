package com.tb.tanks.tankGame.core;


import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;

import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.physic.RecBody2D;
import com.tb.tanks.tankGame.core.tile.GameTile;
import com.tb.tanks.tankGame.core.tile.TileMap;
import com.tb.tanks.tankGame.objects.base.Creature;
import com.tb.tanks.tankGame.util.SpriteMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * map loader class
 * @author mahesh
 *
 */
public class GameLoader {
	
	private ArrayList<Bitmap> plain;
	private Bitmap[] plainTiles;
	

	private Bitmap block_wall;
	private Bitmap block_256x128;
	private Bitmap block_128x128;
	private Bitmap rock_02_128x128;
	private Bitmap well_128x128;
	private Bitmap building_A_512x512;
	
	private AndroidGame gameActivity;
	private boolean togglePlatform_velocity=false;
	private int backGroundImageIndex=0;
    private ArrayList<String> infoPanels;
    
	public GameLoader(AndroidGame activity) {
		this.gameActivity=activity;

		block_wall = TankResourceManager.loadImage("tiles/Block_A_02.png");
		block_256x128 = TankResourceManager.loadImage("tiles/Block_C_01.png");
		block_128x128 = TankResourceManager.loadImage("tiles/Block_C_02.png");
		rock_02_128x128 = TankResourceManager.loadImage("tiles/Rock_02_128x128.png");
		well_128x128 = TankResourceManager.loadImage("tiles/Well_128x128.png");
		building_A_512x512 = TankResourceManager.loadImage("tiles/Building_A_512x512.png");
		infoPanels =new ArrayList<String>();
		

	}

    // Use this to load the main map
	public TileMap loadMap(String filename, TankSoundManager soundManager) throws IOException {
		// lines is a list of strings, each element is a row of the map
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<Rect> rects = new ArrayList<Rect>();

		int width = 0;
		int height = 0;
		
		int pipeOffsetIndex=(Settings.level==2)?30:96;
		// read in each line of the map into lines
		Scanner reader = new Scanner(gameActivity.getAssets().open(filename));
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			line="."+line;
			line=line.trim();
			line=line.substring(1,line.length());
			if(!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}else{
				line=line.substring(1).trim();
				if (line.startsWith("background")) {
					setBackGroundImageIndex(Integer.parseInt(line
							.substring(line.length() - 1)));
				} else if (line.startsWith("waterzone")) {
					line=line.substring(10);
					String[] pts = line.split(",");
					{
						int[] x = new int[4];
						for (int i = 0; i <= 3; i++) {
							x[i] = Integer.parseInt(pts[i]);
						}
						rects.add(new Rect(x[0], x[1], x[0] + x[2], x[1] + x[3]));
					}
				}
			}
		}
		height = lines.size(); // number of elements in lines is the height
		
		TileMap newMap = new TileMap(width, height);
		for (int y=0; y < height; y++) {
			String line = lines.get(y);
			for (int x=0; x < line.length(); x++) {
				char ch = line.charAt(x);
				
				int pixelX = GameRenderer.tilesToPixels(x, 7);
				int pixelY = GameRenderer.tilesToPixels(y, 7);
				// enumerate the possible tiles...
				if (ch == 'G') {

				} else if (ch=='b') {
					GameTile b = new GameTile(pixelX, pixelY, block_wall);
					PointF parent = new PointF(pixelX, pixelY);
					PointF points[] = new PointF[4];
					points[0] = new PointF(3, 3);	points[1] = new PointF(124, 3);

					points[3] = new PointF(3,124);	points[2] = new PointF(124, 124);
					RecBody2D bd = new RecBody2D(points, parent, 0);
					b.setBody2D(bd);
					newMap.setTile(x, y, b);
				} else if(ch == 'c') {
					GameTile c = new GameTile(pixelX, pixelY, block_256x128);
					PointF parent = new PointF(pixelX, pixelY);
					PointF points[] = new PointF[4];
					points[0] = new PointF(3, 3);	points[1] = new PointF(255, 3);

					points[3] = new PointF(3,124);	points[2] = new PointF(255, 124);
					RecBody2D bd = new RecBody2D(points, parent, 0);
					c.setBody2D(bd);
					newMap.setTile(x, y, c);
				} else if(ch == 'd') {
					GameTile d = new GameTile(pixelX, pixelY, block_128x128);
					PointF parent = new PointF(pixelX, pixelY);
					PointF points[] = new PointF[4];
					points[0] = new PointF(3, 3);	points[1] = new PointF(124, 3);

					points[3] = new PointF(3,124);	points[2] = new PointF(124, 124);
					RecBody2D bd = new RecBody2D(points, parent, 0);
					d.setBody2D(bd);
					newMap.setTile(x, y, d);
				}
				else if(ch == 'r') {
					GameTile r = new GameTile(pixelX, pixelY, rock_02_128x128);
					PointF parent = new PointF(pixelX, pixelY);
					PointF points[] = new PointF[4];
					points[0] = new PointF(16, 16);	points[1] = new PointF(124, 3);

					points[3] = new PointF(3,124);	points[2] = new PointF(110, 110);
					RecBody2D bd = new RecBody2D(points, parent, 0);
					r.setBody2D(bd);
					newMap.setTile(x, y, r);
				}else if(ch == 'w') {
					GameTile w = new GameTile(pixelX, pixelY, well_128x128);
					PointF parent = new PointF(pixelX, pixelY);
					PointF points[] = new PointF[4];
					points[0] = new PointF(3, 3);	points[1] = new PointF(124, 3);

					points[3] = new PointF(3,124);	points[2] = new PointF(124, 124);
					RecBody2D bd = new RecBody2D(points, parent, 0);
					w.setBody2D(bd);
					newMap.setTile(x, y, w);
				}else if(ch == 'x') {
					GameTile xx = new GameTile(pixelX, pixelY, building_A_512x512);
					PointF parent = new PointF(pixelX, pixelY);
					PointF points[] = new PointF[4];
					points[0] = new PointF(20, 20);	points[1] = new PointF(492, 20);

					points[3] = new PointF(20,492);	points[2] = new PointF(492, 492);
					RecBody2D bd = new RecBody2D(points, parent, 0);
					xx.setBody2D(bd);
					newMap.setTile(x, y, xx);
				}
			}
		}
		for (Rect r:rects){
			newMap.addWaterZone(r);
		}
		Creature.map=newMap;
		return newMap;	
	}


	public int getBackGroundImageIndex() {
		return backGroundImageIndex;
	}


	public void setBackGroundImageIndex(int backGroundImageIndex) {
		if (backGroundImageIndex<0 ||backGroundImageIndex>10)return;
		this.backGroundImageIndex = backGroundImageIndex;
	}
	
}
