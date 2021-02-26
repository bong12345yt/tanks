package com.tb.tanks.tankGame.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Html;
import android.util.Log;

import com.tb.tanks.ConnectionP2P.P2PConnectionListener;
import com.tb.tanks.ConnectionP2P.PeersAvailableListener;
import com.tb.tanks.ConnectionP2P.WifiManagerP2P;
import com.tb.tanks.R;
import com.tb.tanks.framework.Game;
import com.tb.tanks.framework.Graphics;
import com.tb.tanks.framework.Input.TouchEvent;
import com.tb.tanks.framework.Screen;
import com.tb.tanks.framework.gfx.AndroidGame;
import com.tb.tanks.framework.input.AndroidInput;
import com.tb.tanks.gui.AndroidAbout;
import com.tb.tanks.gui.AndroidButtonOnOff;
import com.tb.tanks.gui.AndroidDialog;
import com.tb.tanks.gui.AndroidImageButton;
import com.tb.tanks.gui.AndroidListView;
import com.tb.tanks.gui.AndroidPanel;
import com.tb.tanks.gui.AndroidVolumeBar;
import com.tb.tanks.gui.Component;
import com.tb.tanks.gui.ComponentClickListener;
import com.tb.tanks.gui.ComponentItemClickListener;
import com.tb.tanks.gui.GUIResourceManager;
import com.tb.tanks.gui.VolumeButtonClickListener;
import com.tb.tanks.tankGame.core.GameLoader;
import com.tb.tanks.tankGame.core.Settings;
import com.tb.tanks.tankGame.core.TankGame;
import com.tb.tanks.tankGame.core.TankResourceManager;
import com.tb.tanks.tankGame.core.TankSoundManager;
import com.tb.tanks.tankGame.preferences.PreferenceConstants;
import com.tb.tanks.tankGame.preferences.SetPreferencesActivity;

import java.io.IOException;
import java.util.List;

public class GuiMenuScreen extends Screen implements PeersAvailableListener {

    AndroidPanel panel;
    AndroidImageButton btnSearch;
    AndroidImageButton btnSetting;
    AndroidImageButton btnAbout;

    public GameLoader gameLoader;
    private AndroidListView lstPlayer;
    private boolean beforeListPlayerVisible;

    private int width, height;
    private String mSelectedControlsString;
    private Canvas gameCanvas;
    private Bitmap background;
    private AndroidDialog dlgSetting;
    private AndroidAbout androidAbout;

    Bitmap frameBuffer;

    public GuiMenuScreen(final Game game) {
        super(game);
        gameLoader = new GameLoader((AndroidGame) game);
        width = game.getScreenWidth();
        height = game.getScreenHeight();
        frameBuffer = ((AndroidGame) game).getBuffer();
        gameCanvas = new Canvas(frameBuffer);
        background = TankResourceManager.loadImage("backgrounds/tank_menu_bg.png");
        panel = new AndroidPanel(" SUPERMARIO ", 0, 0, width, height);
        panel.setTitleBarheight(36);
        panel.setForeColor(Color.WHITE);
        int tbh = panel.getTitleBarheight() - 10;

        Bitmap imgSearchNormal = TankResourceManager.loadImage("gui/btn_search_normal.png");
        Bitmap imgSearchFocus = TankResourceManager.loadImage("gui/btn_search_focus.png");
        btnSearch = new AndroidImageButton("", width / 2 - imgSearchNormal.getWidth() / 2, height / 2 - imgSearchNormal.getHeight() / 2, imgSearchNormal.getWidth(), imgSearchNormal.getHeight());
        btnSearch.setBackgroundNormal(imgSearchNormal);
        btnSearch.setBackgroundFocused(imgSearchFocus);

        Bitmap imgSettingNormal = TankResourceManager.loadImage("gui/btn_settings_normal.png");
        Bitmap imgSettingFocus = TankResourceManager.loadImage("gui/btn_settings_focus.png");
        btnSetting = new AndroidImageButton("", width - imgSettingNormal.getWidth() - 20, 20, imgSettingNormal.getWidth(), imgSettingNormal.getHeight());
        btnSetting.setBackgroundNormal(imgSettingNormal);
        btnSetting.setBackgroundFocused(imgSettingFocus);

        lstPlayer = new AndroidListView(width / 2 - 500, height / 2 - 350, 1000, 700);
        String[] strs = {"Bong 1", "Bong 2", "Danh 1", "Ban 1", "Dat 1", "Bao 1", "Anh 1"};
        lstPlayer.setListText(strs);
        lstPlayer.setVisible(false);

        androidAbout = new AndroidAbout(0, 0, 0, 0);
        androidAbout.setX((width - androidAbout.getWidth()) / 2);
        androidAbout.setY((height - androidAbout.getHeight()) / 2 + 30);
        androidAbout.setVisible(false);
        androidAbout.setStrings(gameLoader.loadAbout("gui/about.txt"));


        btnAbout = new AndroidImageButton("", 20, 20, GUIResourceManager.btn_about_normal.getWidth(), GUIResourceManager.btn_about_normal.getHeight());
        btnAbout.setBackgroundNormal(GUIResourceManager.btn_about_normal);
        btnAbout.setBackgroundFocused(GUIResourceManager.btn_about_focus);
        if(((AndroidGame) game).hasCutout()){
            Point standard = ((AndroidGame) game).m_cutoutHelper.standardizeToSafeX(btnAbout.getX(), btnAbout.getY());
            btnAbout.setX(standard.x);
            btnAbout.setY(standard.y);
        }
        Bitmap imgCloseNormal = GUIResourceManager.loadImage("gui/btn_close_2_normal.png");
        Bitmap imgCloseFocus = GUIResourceManager.loadImage("gui/btn_close_2_focus.png");
        AndroidImageButton btnCloseAbout = new AndroidImageButton("", androidAbout.getWidth() - imgCloseNormal.getWidth(), -imgCloseNormal.getHeight() / 2, imgCloseNormal.getWidth(), imgCloseNormal.getHeight());
        btnCloseAbout.setBackgroundNormal(imgCloseNormal);
        btnCloseAbout.setBackgroundFocused(imgCloseFocus);
        btnCloseAbout.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                btnSearch.setVisible(true);
                btnSetting.setVisible(true);
                btnAbout.setVisible(true);
                androidAbout.setVisible(false);
            }
        });
        androidAbout.addComponent(btnCloseAbout);

        dlgSetting = new AndroidDialog((game.getScreenWidth() - GUIResourceManager.bg_setting.getWidth()) / 2, (game.getScreenHeight() - GUIResourceManager.bg_setting.getHeight()) / 2, GUIResourceManager.bg_setting.getWidth(), GUIResourceManager.bg_setting.getHeight());
        dlgSetting.setBackgroundNormal(GUIResourceManager.bg_setting);
        dlgSetting.setVisible(false);

        final AndroidButtonOnOff androidButtonOnOffMusic = new AndroidButtonOnOff("Music", 340, 320, GUIResourceManager.btn_on.getWidth(), GUIResourceManager.btn_on.getHeight());
        final AndroidButtonOnOff androidButtonOnOffSound = new AndroidButtonOnOff("Sound", 340, androidButtonOnOffMusic.getY() + GUIResourceManager.btn_on.getHeight() + 30, GUIResourceManager.btn_on.getWidth(), GUIResourceManager.btn_on.getHeight());
        AndroidVolumeBar androidVolumeBarMusic = new AndroidVolumeBar("Music Volume", 350, androidButtonOnOffSound.getY() + GUIResourceManager.btn_on.getHeight() + 100, GUIResourceManager.volume_none.getWidth(), GUIResourceManager.volume_none.getHeight());
        AndroidVolumeBar androidVolumeBarSound = new AndroidVolumeBar("Sound Volume", 350, androidVolumeBarMusic.getY() + GUIResourceManager.volume_none.getHeight() + 100, GUIResourceManager.volume_none.getWidth(), GUIResourceManager.volume_none.getHeight());


        AndroidImageButton btnClose = new AndroidImageButton("", dlgSetting.getWidth() - imgCloseNormal.getWidth() - 220, -imgCloseNormal.getHeight() / 2 + 180, imgCloseNormal.getWidth(), imgCloseNormal.getHeight());
        btnClose.setBackgroundNormal(imgCloseNormal);
        btnClose.setBackgroundFocused(imgCloseFocus);
        btnClose.setParent(dlgSetting);
        btnClose.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                btnSearch.setVisible(true);
                btnSetting.setVisible(true);
                btnAbout.setVisible(true);
                dlgSetting.setVisible(false);
            }
        });

        dlgSetting.addComponent(androidButtonOnOffMusic);
        dlgSetting.addComponent(androidButtonOnOffSound);
        dlgSetting.addComponent(androidVolumeBarMusic);
        dlgSetting.addComponent(androidVolumeBarSound);
        dlgSetting.addComponent(btnClose);

        androidButtonOnOffMusic.setOn(Settings.musicEnabled);
        androidVolumeBarMusic.setVolumeVal(Settings.musicVolume);

        androidButtonOnOffSound.setOn(Settings.soundEnabled);
        androidVolumeBarSound.setVolumeVal(Settings.soundVolume);

        androidButtonOnOffMusic.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                if (androidButtonOnOffMusic.isOn()) {
                    TankSoundManager.playMusic();
                    Settings.SetSetting(PreferenceConstants.PREFERENCE_MUSIC_ENABLED, true);
                } else {
                    TankSoundManager.stopMusic();
                    Settings.SetSetting(PreferenceConstants.PREFERENCE_MUSIC_ENABLED, false);
                }
            }
        });

        androidButtonOnOffSound.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                if (androidButtonOnOffSound.isOn()) {
                    TankSoundManager.setSoundEnabled(true);
                    Settings.SetSetting(PreferenceConstants.PREFERENCE_SOUND_ENABLED, true);
                } else {
                    TankSoundManager.setSoundEnabled(false);
                    Settings.SetSetting(PreferenceConstants.PREFERENCE_SOUND_ENABLED, false);
                }
            }
        });

        androidVolumeBarMusic.setVolumeButtonClickListener(new VolumeButtonClickListener() {
            @Override
            public void onVolumeButtonClick(int volVal) {
                TankSoundManager.setMusicVolume(volVal / 100.0f);
                Settings.SetSetting(PreferenceConstants.PREFERENCE_MUSIC_VOLUME, volVal);
            }
        });

        androidVolumeBarSound.setVolumeButtonClickListener(new VolumeButtonClickListener() {
            @Override
            public void onVolumeButtonClick(int volVal) {
                TankSoundManager.setSoundVolume(volVal / 100);
                Settings.SetSetting(PreferenceConstants.PREFERENCE_SOUND_VOLUME, volVal);
            }
        });

        panel.addComponent(btnSearch);
        panel.addComponent(lstPlayer);
        panel.addComponent(btnSetting);
        panel.addComponent(btnAbout);
        panel.addComponent(androidAbout);

        ((TankGame) game).soundManager.loadMenuMusic();

        game.getWifiManagerP2P().SetEnableWifi(true);
        game.getWifiManagerP2P().setPeersAvailableListener(this);
        if (game.getWifiManagerP2P().getClient() != null) {
            boolean isClose = true;
            if(game.getWifiManagerP2P().getClient().getSocket() != null){
                isClose = game.getWifiManagerP2P().getClient().getSocket().isClosed();
            }
            if (!isClose) {
                try {
                    game.getWifiManagerP2P().getClient().getSocket().close();
                    game.getWifiManagerP2P().getClient().setSocket(null);
                    if(game.getWifiManagerP2P().getClient().getSendReceive() != null){
                        game.getWifiManagerP2P().getClient().getSendReceive().setSocket(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(game.getWifiManagerP2P().isServer()){
            try {
                if(game.getWifiManagerP2P().getServer().getServerSocket() != null && !game.getWifiManagerP2P().getServer().getServerSocket().isClosed()){
                    game.getWifiManagerP2P().getServer().getServerSocket().close();
                }
                game.getWifiManagerP2P().getServer().setServerSocket(null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        game.getWifiManagerP2P().setP2PConnectionListener(new P2PConnectionListener() {
            @Override
            public void onConnect(boolean isError) {
                if (isError) {
                    Log.e("Tanks", "Connection failed!");
                }
                WorldScreen worldScreen = new WorldScreen(game);
                worldScreen.loadGame();
                ((AndroidGame) game).setScreenWithFade(worldScreen);
                worldScreen.affterLoadGame();

            }

            @Override
            public void onDisconnect() {

            }
        });

        btnSearch.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                btnSetting.setVisible(false);
				btnAbout.setVisible(false);
                btnSearch.setVisible(false);
                game.getWifiManagerP2P().disconnect();
                game.getWifiManagerP2P().discover();
                lstPlayer.setVisible(true);
                lstPlayer.setDrawDiscovering(true);
            }
        });

        btnSetting.addListener(new ComponentClickListener() {
            @Override
            public void onClick(Component source) {
                btnSearch.setVisible(false);
                btnSetting.setVisible(false);
				btnAbout.setVisible(false);
                dlgSetting.setVisible(true);
            }
        });

        btnAbout.addListener(new ComponentClickListener() {
			@Override
			public void onClick(Component source) {
				btnSearch.setVisible(false);
				btnSetting.setVisible(false);
				btnAbout.setVisible(false);
				androidAbout.setVisible(true);
			}
		});

        lstPlayer.setComponentItemClickListener(new ComponentItemClickListener() {
            @Override
            public void onItemClick(Component source, int index) {
                game.getWifiManagerP2P().connect(index);
            }
        });

    }

    @Override
    public void update(float deltaTime) {
        if (((AndroidGame) game).isScreenTransitionActive()) return;

        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        if (!lstPlayer.isVisible() && beforeListPlayerVisible) {
            btnSetting.setVisible(true);
            btnSearch.setVisible(true);
            btnAbout.setVisible(true);
        }
        beforeListPlayerVisible = lstPlayer.isVisible();

        if (touchEvents == null || touchEvents.size() == 0) return;
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            lstPlayer.processEvent(event);
            btnSearch.processEvent(event);
            btnSetting.processEvent(event);
			btnAbout.processEvent(event);
            dlgSetting.processEvent(event);
            androidAbout.processEvent(event);
        }
    }

    @Override
    public void paint(float deltaTime) {
        gameCanvas.drawRGB(Color.BLACK, Color.BLACK, Color.BLACK);
        gameCanvas.drawBitmap(background, null, new Rect(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight()), null);
        panel.draw(gameCanvas, 0, -10);
        dlgSetting.draw(gameCanvas, 0, 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void dispose() {
        game.getWifiManagerP2P().setP2PConnectionListener(null);
    }

    @Override
    public void onBackPressed() {
        if(dlgSetting.isVisible()){
            dlgSetting.setVisible(false);
            btnSearch.setVisible(true);
            btnSetting.setVisible(true);
            btnAbout.setVisible(true);
        }
        else if(androidAbout.isVisible()){
            androidAbout.setVisible(false);
            btnSearch.setVisible(true);
            btnSetting.setVisible(true);
            btnAbout.setVisible(true);
        }
        else if(lstPlayer.isVisible()){
            lstPlayer.setVisible(false);
            btnSearch.setVisible(true);
            btnSetting.setVisible(true);
            btnAbout.setVisible(true);
        }
        else{
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void configureInputs() {
        SharedPreferences prefs = ((AndroidGame) game).getSharedPreferences(PreferenceConstants.PREFERENCE_MAIN_NAME, 0);
        final int lastVersion = prefs.getInt(PreferenceConstants.PREFERENCE_LAST_VERSION, 0);
        if (lastVersion == 0) {
            // This is the first time the game has been run.
            // Pre-configure the control options to match the device.
            // The resource system can tell us what this device has.
            // TODO: is there a better way to do this?  Seems like a kind of neat
            // way to do custom device profiles.
            final String navType = ((AndroidGame) game).getString(R.string.nav_type);
            mSelectedControlsString = ((AndroidGame) game).getString(R.string.control_setup_dialog_trackball);
            Log.i("Mario", "navType=" + navType);
            if (navType != null) {
                if (navType.equalsIgnoreCase("DPad")) {
                    // Turn off the click-to-attack pref on devices that have a dpad.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PreferenceConstants.PREFERENCE_CLICK_ATTACK, false);
                    editor.commit();
                    mSelectedControlsString = ((AndroidGame) game).getString(R.string.control_setup_dialog_dpad);
                } else if (navType.equalsIgnoreCase("None")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    //SensorManager manager = (SensorManager) ((AndroidGame)game).getSystemService(Context.SENSOR_SERVICE);
                    if (((AndroidInput) game.getInput()).hasAccelerometer()) {
                        //Log.i("Mario", "Accelerometer:"+manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size());
                        // Turn on tilt controls if available
                        editor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, true);
                        editor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, false);
                        mSelectedControlsString = ((AndroidGame) game).getString(R.string.control_setup_dialog_tilt);
                    } else {
                        //Use on ScreenControl if there's nothing else.
                        editor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, true);
                        editor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false);
                        mSelectedControlsString = ((AndroidGame) game).getString(R.string.control_setup_dialog_screen);
                    }
                    editor.commit();

                }
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PreferenceConstants.PREFERENCE_LAST_VERSION, TankGame.VERSION);
            editor.commit();
            Settings.loadPreferences((AndroidGame) game);
            ((AndroidGame) game).runOnUiThread(new Runnable() {
                public void run() {
                    showControls_SetUp_Dialog();
                }
            });

        }
    }

    private void showControls_SetUp_Dialog() {
        String messageFormat = ((AndroidGame) game).getResources().getString(R.string.control_setup_dialog_message);
        String message = String.format(messageFormat, mSelectedControlsString);
        CharSequence sytledMessage = Html.fromHtml(message);  // lame.
        AlertDialog dialog = new AlertDialog.Builder((AndroidGame) game)
                .setTitle(R.string.control_setup_dialog_title)
                .setPositiveButton(R.string.control_setup_dialog_ok, null)
                .setNegativeButton(R.string.control_setup_dialog_change, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(((AndroidGame) game).getBaseContext(), SetPreferencesActivity.class);
                        i.putExtra("controlConfig", true);
                        ((AndroidGame) game).startActivity(i);
                    }
                })
                .setMessage(sytledMessage)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    @Override
    public void onPeersAvailable(WifiManagerP2P wifiManagerP2P) {
        lstPlayer.setDrawDiscovering(false);
        lstPlayer.setListText(wifiManagerP2P.getDeviceNameArray());
    }
}
