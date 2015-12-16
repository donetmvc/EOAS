package com.eland.android.eoas.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.eland.android.eoas.Util.CacheInfoUtil;
import com.eland.android.eoas.Util.ConsoleUtil;

/**
 * Created by liu.wenbin on 15/12/14.
 */
public class AppRebootReceiver extends BroadcastReceiver{

    private String action = "";
    private Context context;
    private String TAG = "EOAS";
    private TelephonyManager telephonyManager;
    private String imei;

    @Override
    public void onReceive(Context context, Intent intent) {

        action = intent.getAction();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();

        ConsoleUtil.i(TAG, "----------自启动:-------------" + imei);

        if(!action.isEmpty() && action.equals(Intent.ACTION_BOOT_COMPLETED)) {

            ConsoleUtil.i(TAG, "----------自启动:-------------" + String.valueOf(CacheInfoUtil.loadIsRegAuto(context, imei)));

            if(CacheInfoUtil.loadIsRegAuto(context, imei)) {
                ConsoleUtil.i(TAG, "----------自启动:-------------" + "start");
                Intent clockIntent = new Intent(context, AutoReceiver.class);
                clockIntent.setAction("REG_AUTO");
                AlarmManager alar = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, clockIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alar.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10*1000, pi);
                ConsoleUtil.i(TAG, "----------自启动:-------------" + "finished");
            }
        }
    }
}
