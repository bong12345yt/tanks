package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.tb.tanks.framework.Input;

public class AndroidListView extends Component  {
    private Bitmap background;
    private int width;
    private int height;
    private String[] listText;
    private Paint paintText;
    private Bitmap[] icons;
    private float paddingTop = 120;
    private float paddingLeft = 120;
    private float alignLine = 30;
    private int touchY = 0;
    private boolean isSelected = false;
    private int maxItemShow = 4;
    private int currentItemIndex = 0;
    private long oldTime;

    Bitmap test;

    public AndroidListView(int x, int y, int w, int h) {
        super(x, y, w, h);

        Typeface tf = GUIResourceManager.loadFont("fonts/DancingScript-Bold.ttf");
        paintText = new Paint();
        paintText.setTypeface(tf);
        paintText.setTextSize(40f);
        test = GUIResourceManager.loadImage("gui/face.png");
        background = GUIResourceManager.loadImage("gui/bg.png");
        int cwidth = background.getWidth();
        int cheight = background.getHeight();
        float scaleWidth = ((float) w) / cwidth;
        float scaleHeight = ((float) h) / cheight;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        background = Bitmap.createBitmap(background, 0, 0, cwidth, cheight, matrix, false);

    }

    public void setTextSize(float textSize) {
        paintText.setTextSize(textSize);
    }

    public void setFontTypeFromFile(String filename){
        Typeface tf = GUIResourceManager.loadFont(filename);
        paintText.setTypeface(tf);
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setListText(String[] listText) {
        this.listText = listText;
        icons = new Bitmap[listText.length];
        for(int i = 0; i < listText.length; i++){
            icons[i] = GUIResourceManager.loadImage("gui/face.png");
        }
    }

    public float getTextSize(){
        return paintText.getTextSize();
    }

    private void drawItemString(Canvas g, int X, int Y){
        float beginX = X + paddingLeft;
        float beginY = Y + paddingTop;
        int size = maxItemShow;
        if(listText.length < size){
            size = listText.length;
        }
        for(int i = currentItemIndex; i < currentItemIndex + size; i++){
            if(listText.length - (currentItemIndex + 1)< maxItemShow){
                size = listText.length - currentItemIndex;
            }
            if(listText.length - currentItemIndex + size < 0){
                break;
            }
            g.drawBitmap(icons[i], beginX, beginY,null);
            g.drawText(listText[i], beginX + test.getWidth() + 30 , beginY + test.getHeight()/2 + getTextSize()/2, paintText);
            beginY += alignLine + icons[i].getHeight();
        }
    }


    @Override
    public void draw(Canvas g, int X, int Y) {
        g.drawBitmap(background, X, Y, null);
        drawItemString(g, X, Y);
    }

    @Override
    public void processEvent(Input.TouchEvent event) {
        if (event.type == Input.TouchEvent.TOUCH_UP) {
            focused = false;
            isSelected = false;
        }
        if (event.type == Input.TouchEvent.TOUCH_DOWN) {
            touchY = (int) event.y;
            if (inBounds(event)) { // for control
                isSelected = true;
                focused = true;
            }
        }
        if (event.type == Input.TouchEvent.TOUCH_DRAGGED){
            int dy = 0;
            dy = (int) event.y - touchY;
            int deltaTime = (int)(System.currentTimeMillis() - oldTime);
            if (isSelected && deltaTime > 40) {
                if(dy > 20){
                    currentItemIndex--;
                } else if(dy < -20){
                    currentItemIndex++;
                }
                if(currentItemIndex >= maxItemShow ){
                    currentItemIndex = maxItemShow;
                }
                if(currentItemIndex < 0){
                    currentItemIndex = 0;
                }
            }
            touchY = (int) event.y;
            oldTime = System.currentTimeMillis();
        }
    }
}
