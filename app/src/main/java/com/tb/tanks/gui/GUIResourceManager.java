package com.tb.tanks.gui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class GUIResourceManager {

    public static Activity activity;
    public static InputStream inputStream;
    public static BitmapFactory.Options options;

    public GUIResourceManager(Activity pActivity)
    {
        activity=pActivity;
        options=new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public static Bitmap loadImage(String fileName)
    {
        try {
            Log.i("resource", fileName);
            inputStream = activity.getAssets().open(fileName);

            return BitmapFactory.decodeStream(inputStream, null, new BitmapFactory.Options());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            Log.e("resource",e.getMessage()+fileName);
        }
        return null;
    }

    public static Typeface loadFont(String filename){
        Typeface tf =Typeface.createFromAsset(activity.getAssets(), filename);
        return tf;
    }
}
