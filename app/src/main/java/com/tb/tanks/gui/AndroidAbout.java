package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.tb.tanks.framework.Input;

import java.util.ArrayList;

public class AndroidAbout extends Component {
    ArrayList<Component> components;
    private ArrayList<String> strings = null;
    private int drawX;
    private int drawY;
    private int stringX;
    private int stringY;
    private int headerX = 152;
    private int headerY = 108;
    private int bottomX = 114;
    private int bottomY = 683;
    private int topOfStrings = 0;
    private int botOfStrings = 0;
    private int touchY = 0;
    private boolean isSelected = false;
    private boolean isAutoScroll = true;
    private long oldTime;
    private Bitmap background;
    private Bitmap headerAbout;
    private Bitmap bottomAbout;
    private Paint paintText;
    private Paint paintHeader;

    public AndroidAbout(int x, int y, int w, int h) {
        super(x, y, w, h);
        stringX = x;
        stringY = y;
        background = GUIResourceManager.loadImage("gui/bg_about.png");
        headerAbout = GUIResourceManager.loadImage("gui/header_about.png");
        bottomAbout = GUIResourceManager.loadImage("gui/bottom_about.png");
        this.width = background.getWidth();
        this.height = background.getHeight();
        Typeface tf = GUIResourceManager.loadFont("fonts/UVNBachDang_R.TTF");
        paintText = new Paint();
        paintText.setTypeface(tf);
        paintText.setTextSize(40f);
        paintText.setAntiAlias(true);

        paintHeader = new Paint();
        paintHeader.setTypeface(tf);
        paintHeader.setTextSize(51f);
        paintHeader.setAntiAlias(true);
        paintHeader.setColor(Color.parseColor("#26A8FF"));
        components = new ArrayList<Component>();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible){
            isAutoScroll = true;
            stringX = x;
            stringY = y + 300;
        }
    }

    public void addComponent(Component c){
        components.add(c);
        c.setParent(this);
    }

    public void removeComponent(Component c){
        components.remove(c);
        c.setParent(null);
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        stringX = x;
        headerX += x;
        bottomX += x;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        stringY = y + 300;
        headerY += y;
        bottomY += y;
    }

    @Override
    public void draw(Canvas g, int X, int Y) {
        drawX = X;
        drawY = Y;
        if (isVisible) {
            int beginY = stringY;
            topOfStrings = Y + beginY;
            g.drawBitmap(background, X + x, Y + y, null);
            if (strings != null) {
                for (String str : strings) {
                    Rect bounds = new Rect();
                    if (str.indexOf("<header>") >= 0) {
                        String strR = str.replace("<header>", "");
                        paintHeader.getTextBounds(strR, 0, strR.length(), bounds);
                    } else {
                        paintText.getTextBounds(str, 0, str.length(), bounds);
                    }
                    if (beginY > headerY + 60 && beginY < bottomY + 100) {
                        if (str.indexOf("<header>") >= 0) {
                            g.drawText(str.replace("<header>", ""), X + stringX + (width - bounds.width() - 40) / 2, Y + beginY, paintHeader);
                        } else {
                            g.drawText(str, X + stringX + (width - bounds.width() - 40) / 2, Y + beginY, paintText);
                        }
                    }
                    beginY += bounds.height() + 20;
                }
                botOfStrings = beginY;
                if (beginY > bottomY && isAutoScroll)
                    stringY--;
                if (topOfStrings > headerY + headerAbout.getHeight() + 50) {
                    int deltaY = stringY - (headerY + headerAbout.getHeight() + 50);
                    stringY -= deltaY;
                }else if(botOfStrings < bottomY){
                    int deltaY = bottomY - botOfStrings;
                    stringY += deltaY;
                }
            }
            g.drawBitmap(headerAbout, X + headerX, Y + headerY, null);
            g.drawBitmap(bottomAbout, X + bottomX, Y + bottomY, null);
            for(Component c:components){
                c.draw(g,X + x, Y + y);
            }
        }
    }

    @Override
    public void processEvent(Input.TouchEvent event) {
        if(!isVisible) return;
        for(Component c:components){
            c.processEvent(event);
        }
        if (event.type == Input.TouchEvent.TOUCH_UP) {
            focused = false;
            isSelected = false;
        }
        if (event.type == Input.TouchEvent.TOUCH_DOWN) {
            touchY = (int) event.y;
            if (inBounds(event)) { // for control
                isSelected = true;
                focused = true;
                isAutoScroll = false;
            }
        }
        if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
            int dy = 0;
            dy = (int) event.y - touchY;
            int deltaTime = (int) (System.currentTimeMillis() - oldTime);
            if (isSelected && deltaTime > 1) {
                if (dy > 0) {
                    stringY += dy;
                } else if (dy < 0) {
                    stringY += dy;
                }

                touchY = (int) event.y;
            }

            oldTime = System.currentTimeMillis();
        }
    }
}
