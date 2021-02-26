package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tb.tanks.framework.Input;

public class AndroidButtonOnOff extends Component {

    protected int textSize = 80;
    private Bitmap backgroundOn;
    private Bitmap backgroundOFF;
    private boolean isOn = false;
    private Paint paint;
    private int AllignmentHorizontal = 580;
    private Rect boundsOfText;

    public AndroidButtonOnOff(String text, int x, int y, int w, int h) {
        super(x, y, w, h);

        this.text = text;
        backgroundOn = GUIResourceManager.btn_on;
        backgroundOFF = GUIResourceManager.btn_off;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(GUIResourceManager.loadFont("fonts/DancingScript-Bold.ttf"));
        paint.setTextSize(textSize);
        boundsOfText = new Rect();
        paint.getTextBounds(text, 0, text.length(), boundsOfText);
        this.setX( x + AllignmentHorizontal);
    };

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getAllignmentHorizontal() {
        return AllignmentHorizontal;
    }

    public void setAllignmentHorizontal(int allignmentHorizontal) {
        AllignmentHorizontal = allignmentHorizontal;
    }

    public Bitmap getBackgroundOn() {
        return backgroundOn;
    }

    public void setBackgroundOn(Bitmap backgroundOn) {
        this.backgroundOn = backgroundOn;
    }

    public Bitmap getBackgroundOFF() {
        return backgroundOFF;
    }

    public void setBackgroundOFF(Bitmap backgroundOFF) {
        this.backgroundOFF = backgroundOFF;
    }

    @Override
    public void draw(Canvas g, int X, int Y) {

        Bitmap bg;
        if(backgroundOn != null &&  backgroundOFF != null){
            if(isOn){
                //Matrix matrix = new Matrix();
                // RESIZE THE BIT MAP
                //matrix.postScale(0.98f, 0.98f);

                // "RECREATE" THE NEW BITMAP
                //Bitmap resizedBg = Bitmap.createBitmap(
                        //bg, 0, 0, bg.getWidth(), bg.getHeight(), matrix, false);

                g.drawBitmap(backgroundOn, X + x , Y + y , null);
            }
            else{
                g.drawBitmap(backgroundOFF, X + x, Y + y, null);
            }
        }

        if(text != ""){
            g.drawText(text, X + x - AllignmentHorizontal, Y + y  + height-(height-boundsOfText.height())/2 - 5, paint);
        }

    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        if (textSize > 0)
            this.textSize = textSize;
    }

    @Override
    public void processEvent(Input.TouchEvent event) {
        if(!isVisible) return;
        if (event.type == Input.TouchEvent.TOUCH_UP) {
            focused=false;
            if (inBounds(event)) {
                isOn = !isOn;
                onTouchUp();
            }

        }
        if (event.type == Input.TouchEvent.TOUCH_DOWN) {

            if (inBounds(event)) {
                onTouchDown();
            }

        }
    }

}
