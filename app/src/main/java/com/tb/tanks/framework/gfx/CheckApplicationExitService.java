package com.tb.tanks.framework.gfx;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class CheckApplicationExitService extends Service{
    private static CheckApplicationExitServiceListener checkApplicationExitServiceListener = null;

    public static void setCheckApplicationExitServiceListener(CheckApplicationExitServiceListener checkApplicationExitServiceListener1) {
        checkApplicationExitServiceListener = checkApplicationExitServiceListener1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Log.d(getClass().getName(), "App just got removed from Recents!");
        if(checkApplicationExitServiceListener != null){
            checkApplicationExitServiceListener.onAppExit();
        }
    }
}
