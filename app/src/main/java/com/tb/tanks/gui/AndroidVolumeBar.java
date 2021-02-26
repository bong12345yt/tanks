package com.tb.tanks.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tb.tanks.framework.Input;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.preferences.PreferenceConstants;

import java.util.ArrayList;

public class AndroidVolumeBar extends Component {
    protected int textSize = 60;
    private Bitmap backgroundVolumeNone;
    private Bitmap backgroundVolumeValueLeft;
    private ArrayList<Bitmap> backgroundVolumeValueMids;
    private Bitmap backgroundVolumeValueRight;
    private int volumeMaxVal = 100;
    private int volumeVal = 0;
    final private int volStride = 5;
    private boolean isOn = false;
    private Paint paint;
    private Rect boundsOfText;
    private AndroidImageButton btn_add;
    private AndroidImageButton btn_sub;
    private VolumeButtonClickListener volumeButtonClickListener;

    public AndroidVolumeBar(String text, int x, int y, int w, int h) {
        super(x, y, w, h);

        this.text = text;
        backgroundVolumeNone = GUIResourceManager.volume_none;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(GUIResourceManager.loadFont("fonts/DancingScript-Bold.ttf"));
        paint.setTextSize(textSize);
        boundsOfText = new Rect();
        paint.getTextBounds(text, 0, text.length(), boundsOfText);

        backgroundVolumeValueLeft = GUIResourceManager.volume_value_left;
        backgroundVolumeValueRight = GUIResourceManager.volume_value_right;

        backgroundVolumeValueMids = new ArrayList<Bitmap>();

        btn_add = new AndroidImageButton("", width + GUIResourceManager.btn_add_normal.getWidth()/2, (height - GUIResourceManager.btn_add_normal.getHeight())/2, GUIResourceManager.btn_add_normal.getWidth(), GUIResourceManager.btn_add_normal.getHeight());
        btn_add.setBackgroundNormal(GUIResourceManager.btn_add_normal);
        btn_add.setBackgroundFocused(GUIResourceManager.btn_add_focus);
        btn_sub = new AndroidImageButton("",GUIResourceManager.btn_add_normal.getWidth()/2 , (height - GUIResourceManager.btn_sub_normal.getHeight())/2, GUIResourceManager.btn_sub_normal.getWidth(), GUIResourceManager.btn_sub_normal.getHeight());
        btn_sub.setBackgroundNormal(GUIResourceManager.btn_sub_normal);
        btn_sub.setBackgroundFocused(GUIResourceManager.btn_sub_focus);
        btn_add.setParent(this);
        btn_sub.setParent(this);
        btn_add.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                if(volumeVal < volumeMaxVal){
                    increaseVol(volStride);
                    backgroundVolumeValueMids.add(Bitmap.createBitmap(GUIResourceManager.volume_value_mid));
                    //TankSoundManager.setMusicVolume(volumeVal/100.0f);
                    volumeButtonClickListener.onVolumeButtonClick(volumeVal);
                }
            }
        });

        btn_sub.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                if(volumeVal > 0){
                    increaseVol(-volStride);
                    //TankSoundManager.setMusicVolume(volumeVal/100.0f);
                    volumeButtonClickListener.onVolumeButtonClick(volumeVal);
                    if(backgroundVolumeValueMids.size() > 0)
                        backgroundVolumeValueMids.remove(backgroundVolumeValueMids.size() - 1);
                }
            }
        });
    };

    public VolumeButtonClickListener getVolumeButtonClickListener() {
        return volumeButtonClickListener;
    }

    public void setVolumeButtonClickListener(VolumeButtonClickListener volumeButtonClickListener) {
        this.volumeButtonClickListener = volumeButtonClickListener;
    }

    public int getVolumeVal() {
        return volumeVal;
    }

    public void setVolumeVal(int volumeVal) {
        //volume val must mode on 5
        this.volumeVal = volumeVal;
        if(backgroundVolumeValueMids.size() >0) backgroundVolumeValueMids.clear();
        for(int i = 0; i < volumeVal/volStride; i++){
            backgroundVolumeValueMids.add(Bitmap.createBitmap(GUIResourceManager.volume_value_mid));
        }
    }

    @Override
    public void draw(Canvas g, int X, int Y) {
        Bitmap bg;
        if(backgroundVolumeNone != null){
            g.drawBitmap(backgroundVolumeNone, X + x + btn_sub.getWidth(), Y + y , null);
            g.drawBitmap(backgroundVolumeValueLeft, X + x + btn_sub.getWidth(), Y + y, null);
            int increaseWidth = btn_sub.getWidth() + backgroundVolumeValueLeft.getWidth();
            for(Bitmap bm:backgroundVolumeValueMids){
                g.drawBitmap(bm, X + x + increaseWidth, Y+ y,null);
                increaseWidth += bm.getWidth();
            }
            g.drawBitmap(backgroundVolumeValueRight, X + x + increaseWidth, Y+ y, null);
            btn_add.draw(g, X + x, Y + y);
            btn_sub.draw(g, X + x, Y + y);
        }

        if(text != ""){
            g.drawText(text, X + x + (width - boundsOfText.width())/2 + btn_sub.getWidth() , Y + y - 20, paint);
        }

    }

    public void increaseVol(int val){
        volumeVal += val;
        //Settings.SetSetting(PreferenceConstants.PREFERENCE_MUSIC_VOLUME, volumeVal);
        //TankSoundManager.setMusicVolume(volumeVal/100.0f);
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
        btn_add.processEvent(event);
        btn_sub.processEvent(event);

        if (event.type == Input.TouchEvent.TOUCH_UP) {
            focused=false;
            if (inBounds(event)) {
                onTouchUp();
                isOn = !isOn;
            }

        }
        if (event.type == Input.TouchEvent.TOUCH_DOWN) {

            if (inBounds(event)) {
                onTouchDown();
            }

        }
    }
}
