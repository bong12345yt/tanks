package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.tb.tanks.framework.Input;

public class AndroidImageButton extends Component{
    protected int textSize = 18;
    private Bitmap backgroundNormal;
    private Bitmap backgroundFocused;

    public AndroidImageButton(String text, int x, int y, int w, int h) {
        super(x, y, w, h);
        this.text = text;
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
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bg;
        int i=focused ? 0 : -1;
        if(backgroundNormal!= null &&  backgroundFocused != null){
            bg=focused ? backgroundFocused : backgroundNormal;
            if(focused){
                Matrix matrix = new Matrix();
                // RESIZE THE BIT MAP
                matrix.postScale(0.98f, 0.98f);

                // "RECREATE" THE NEW BITMAP
                Bitmap resizedBg = Bitmap.createBitmap(
                        bg, 0, 0, bg.getWidth(), bg.getHeight(), matrix, false);

                g.drawBitmap(resizedBg, X + x + 0.01f*resizedBg.getWidth(), Y + y + 0.01f*resizedBg.getHeight(), null);
            }
            else{
                g.drawBitmap(bg, X + x, Y + y, null);
            }
        }

        if(text != ""){
            g.drawText(text, X+x + width / 2, Y+y + height-(height-textSize)/2-2+i, paint);
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
