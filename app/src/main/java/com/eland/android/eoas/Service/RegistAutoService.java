package com.eland.android.eoas.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

/**
 * Created by liu.wenbin on 15/12/18.
 */
public class RegistAutoService extends Service implements RegistPushIDService.IOnRegistListener{

    private String pushId,imei = "";
    private TelephonyManager telephonyManager;
    int registCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null != intent) {
            pushId = intent.getStringExtra("PUSHID");
        }
        imei = telephonyManager.getDeviceId();

        if(!pushId.isEmpty() && !imei.isEmpty()) {
            startRegistService();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startRegistService() {
        RegistPushIDService registPushIDService = new RegistPushIDService();
        registPushIDService.registPush(pushId, imei, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRegistSuccess() {
        stopSelf();
    }

    @Override
    public void onRegistFailure() {
        //retry
        if(registCount > 20) {
            stopSelf();
        }
        else {
            registCount++;
            startRegistService();
        }
    }
}
