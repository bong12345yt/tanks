package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tb.tanks.framework.Input;

import java.util.ArrayList;

public class AndroidDialog extends Component {

    ArrayList<Component> components;
    private Bitmap backgroundNormal;

    private int endX;
    private int endY;
    private int SPEED = 30;

    public AndroidDialog(int x, int y, int w, int h) {
        super(x, y, w, h);
        endX = x;
        endY = y;
        components = new ArrayList<Component>();
    }

    public Bitmap getBackgroundNormal() {
        return backgroundNormal;
    }

    public void setBackgroundNormal(Bitmap backgroundNormal) {
        this.backgroundNormal = backgroundNormal;
    }

    public void addComponent(Component c){
        components.add(c);
        c.setParent(this);
    }

    public void removeComponent(Component c){
        components.remove(c);
        c.setParent(null);
    }

    public void updateShowVertical(){
        if(y < endY){
            y+=SPEED;
        }
    }

    @Override
    public void draw(Canvas g, int X, int Y) {
        if(isVisible){
            g.drawBitmap(backgroundNormal, X + x, Y + y, null);
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
    }
}
