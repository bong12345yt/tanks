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
    public static Bitmap you_win;
    public static Bitmap you_lose;
    public static Bitmap bg_setting;
    public static Bitmap bg_free;
    public static Bitmap menu_normal;
    public static Bitmap menu_focus;
    public static Bitmap btn_on;
    public static Bitmap btn_off;
    public static Bitmap volume_none;
    public static Bitmap volume_value_left;
    public static Bitmap volume_value_mid;
    public static Bitmap volume_value_right;
    public static Bitmap btn_add_normal;
    public static Bitmap btn_sub_normal;
    public static Bitmap btn_add_focus;
    public static Bitmap btn_sub_focus;
    public static Bitmap btn_about_normal;
    public static Bitmap btn_about_focus;
    public static Bitmap btn_free_normal;
    public static Bitmap btn_free_focus;
    public static Bitmap[] loadings;
    public static Bitmap background_mini_map;
    public static Bitmap tank_mini_map;
    public static Bitmap tank_other_mini_map;
    public static Bitmap score_board_title;
    public static Bitmap score_board_bg;

    public GUIResourceManager(Activity pActivity) {
        activity = pActivity;
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        LoadResourceLoadings();
    }

    public void loadResouces() {
        you_win = loadImage("gui/bg_you_win.png");
        you_lose = loadImage("gui/bg_you_lose.png");
        bg_setting = loadImage("gui/bg_setting.png");
        bg_free = loadImage("gui/bg_free.png");
        menu_normal = loadImage("gui/menu_normal.png");
        menu_focus = loadImage("gui/menu_focus.png");
        btn_on = loadImage("gui/btn_on_small.png");
        btn_off = loadImage("gui/btn_off_small.png");
        volume_none = loadImage("gui/volume_none.png");
        volume_value_left = loadImage("gui/volume_value_left.png");
        volume_value_mid = loadImage("gui/volume_value_mid.png");
        volume_value_right = loadImage("gui/volume_value_right.png");
        btn_add_normal = loadImage("gui/btn_add_normal.png");
        btn_sub_normal = loadImage("gui/btn_sub_normal.png");
        btn_add_focus = loadImage("gui/btn_add_focus.png");
        btn_sub_focus = loadImage("gui/btn_sub_focus.png");
        btn_about_normal = loadImage("gui/btn_about_normal.png");
        btn_about_focus = loadImage("gui/btn_about_focus.png");
        btn_free_normal = loadImage("gui/btn_free_normal.png");
        btn_free_focus = loadImage("gui/btn_free_focus.png");
        background_mini_map = loadImage("gui/mini_map/mini_map_boder.png");
        tank_mini_map = loadImage("gui/mini_map/player_minimap_small_blur.png");
        tank_other_mini_map = loadImage("gui/mini_map/player_other_mini_map_small.png");
        score_board_bg = loadImage("gui/score_board/score_board_bg.png");
        score_board_title = loadImage("gui/score_board/Scoreboar_tille.png");
    }

    public void LoadResourceLoadings() {
        loadings = new Bitmap[11];
        for (int i = 0; i < 11; i++) {
            String fileName = "gui/loading/frame-" + i + ".png";
            Bitmap fireShotImp = loadImage(fileName);
            loadings[i] = fireShotImp;
        }
    }

    public static Bitmap loadImage(String fileName) {
        try {
            Log.i("resource", fileName);
            inputStream = activity.getAssets().open(fileName);

            return BitmapFactory.decodeStream(inputStream, null, new BitmapFactory.Options());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            Log.e("resource", e.getMessage() + fileName);
        }
        return null;
    }

    public static Typeface loadFont(String filename) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), filename);
        return tf;
    }
}
