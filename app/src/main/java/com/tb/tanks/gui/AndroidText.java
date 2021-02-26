package com.tb.tanks.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.tb.tanks.framework.Input.TouchEvent;

public class AndroidText extends Component {
    protected int textSize = 18;
    protected int align = 0;
    protected int color = Color.BLACK;
    protected int strokeWidth = 1;
    Paint paint = new Paint();

    public AndroidText(String text, int x, int y, int w, int h) {
        super(x, y, w, h);
        this.text = text;
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setTextAlign(Align.CENTER);
        paint.setColor(foreColor);
        paint.setShadowLayer(2, 1, 1, Color.GRAY);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        if (textSize > 0)
            this.textSize = textSize;
        paint.setTextSize(textSize);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    public int getAlign() {
        return align;
    }

    /**
     * sets alignment of the text
     *
     * @param align 0=left, 1=centre, 2=right
     */
    public void setAlign(int align) {
        this.align = align;
    }

    @Override
    public void processEvent(TouchEvent event) {

    }

    @Override
    public void draw(Canvas g, int X, int Y) {
		int cacheY = y;
        for (String line : text.split("\n")) {
            switch (align) {
                case 0:
                    paint.setTextAlign(Align.LEFT);
                    g.drawText(line, X + x + offsetX, Y + cacheY + height - (height - textSize) / 2, paint);
                    break;
                case 1:
                    paint.setTextAlign(Align.CENTER);
                    g.drawText(line, X + x + width / 2, Y + cacheY + 3 * height / 4, paint);
                    break;
                case 2:
                    paint.setTextAlign(Align.RIGHT);
                    g.drawText(line, X + width - offsetX, Y + cacheY + 3 * height / 4, paint);
                    break;
            }

			cacheY += paint.descent() - paint.ascent();
        }
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

}
