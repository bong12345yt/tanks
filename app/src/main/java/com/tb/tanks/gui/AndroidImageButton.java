package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.tb.tanks.framework.Input;

public class AndroidImageButton extends Component{
    protected int textSize = 18;
    private Bitmap backgroundNormal;
    private Bitmap backgroundFocused;
    private Paint paintText = null;
    private Rect textBound = null;
    private int textY = 0;
    private int textX = 0;

    public AndroidImageButton(String text, int x, int y, int w, int h) {
        super(x, y, w, h);
        this.text = text;
        textBound = new Rect();
        paintText = new Paint();
        paintText.setTextAlign(Paint.Align.LEFT);
        paintText.setAntiAlias(true);
        paintText.getTextBounds(text,0, text.length(), textBound);
    };

    public Bitmap getBackgroundNormal() {
        return backgroundNormal;
    }

    public void setBackgroundNormal(Bitmap backgroundNormal) {
        this.backgroundNormal = backgroundNormal;
    }

    public Bitmap getBackgroundFocused() {
        return backgroundFocused;
    }

    public void setBackgroundFocused(Bitmap backgroundFocused) {
        this.backgroundFocused = backgroundFocused;
    }


    @Override
    public void draw(Canvas g, int X, int Y) {
        if(isVisible) {

            Bitmap bg;
            int i = focused ? 0 : -3;
            if (backgroundNormal != null && backgroundFocused != null) {
                bg = focused ? backgroundFocused : backgroundNormal;
                if (focused) {
                    Matrix matrix = new Matrix();
                    // RESIZE THE BIT MAP
                    matrix.postScale(0.98f, 0.98f);

                    // "RECREATE" THE NEW BITMAP
                    Bitmap resizedBg = Bitmap.createBitmap(
                            bg, 0, 0, bg.getWidth(), bg.getHeight(), matrix, false);

                    g.drawBitmap(resizedBg, X + x + 0.01f * resizedBg.getWidth(), Y + y + 0.01f * resizedBg.getHeight(), null);
                } else {
                    g.drawBitmap(bg, X + x, Y + y, null);
                }
            }

            if (text != "") {
                int val = (int) ((width - textBound.width() ) / 2 + 0.5f);
                g.drawText(text, X + x + val + textX, Y + y + textY + height - (height - textBound.height()) / 2 + i, paintText);
            }
        }
    }

    public int getTextSize() {
        return textSize;
    }

    public int getTextY() {
        return textY;
    }

    public void setTextY(int textY) {
        this.textY = textY;
    }

    public int getTextX() {
        return textX;
    }

    public void setTextX(int textX) {
        this.textX = textX;
    }

    public void setTextSize(int textSize) {
        if (textSize > 0){
            paintText.setTextSize(textSize);
            this.textSize = textSize;
            paintText.getTextBounds(text,0, text.length(), textBound);
        }
    }

    public void setTextColor(int color){
        paintText.setColor(color);
    }

    @Override
    public void processEvent(Input.TouchEvent event) {
        if(!isVisible) return;
        if (event.type == Input.TouchEvent.TOUCH_UP) {
            focused=false;
            if (inBounds(event)) {
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
