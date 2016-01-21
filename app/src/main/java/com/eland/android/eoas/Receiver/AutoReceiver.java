package com.eland.android.eoas.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.eland.android.eoas.Service.CheckRegScheduleService;
import com.eland.android.eoas.Service.RegAutoService;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.SystemMethodUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by liu.wenbin on 15/12/9.
 */
public class AutoReceiver extends BroadcastReceiver implements CheckRegScheduleService.IOnCheckListener{

    private String action = "";
    private Context context;
    private String TAG = "EOAS";
    private Intent intents;
    private TelephonyManager telephonyManager;
    private String imei;
    private String isAm;
    private String recievedBroadcast = "false";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        action = intent.getAction();

        init();

        ConsoleUtil.i(TAG, "--------ACTION:------------" + action);

        if(!action.isEmpty() && action.equals("REG_AUTO")) {
            //check is it right time to start service
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date currentDate = new Date(System.currentTimeMillis());
            String formatDate = format.format(currentDate);
            int hour = Integer.valueOf(formatDate.substring(0, 5).replace(":", ""));

            recievedBroadcast = SharedReferenceHelper.getInstance(context).getValue("REG_AUTO");
            ConsoleUtil.i(TAG, "--------Received:------------" + recievedBroadcast);
            if(!recievedBroadcast.equals("true")) {
                if((hour >= 650 && hour <= 830) || hour > 1705 && hour < 1800) {
                    //check are you reg schedule
                    startCheck();
                }
            }
        }

    }

    private void init() {
        intents = new Intent(context, RegAutoService.class);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        int am_pm = gregorianCalendar.get(GregorianCalendar.AM_PM);
        if (am_pm == 0) {
            isAm = "AM";
        } else {
            isAm = "PM";
        }
    }

    private void startCheck() {
        SharedReferenceHelper.getInstance(context).setValue("REG_AUTO", "true");
        if(!SystemMethodUtil.isWorked(context, "RegAutoService")) {
            CheckRegScheduleService checkService = new CheckRegScheduleService();
            checkService.check(imei, this);
        }
    }

    private void startRegService() {
        ConsoleUtil.i(TAG, "--------Service start------------");
        intents.putExtra("IMEI", imei);
        intents.putExtra("ISAM", isAm);
        intents.putExtra("TYPE", "AUTO");
        context.startService(intents);
    }

    private void stopRegService() {
        ConsoleUtil.i(TAG, "--------Service stop------------");
        context.stopService(intents);
    }

    @Override
    public void onCheckSuccess(String msg) {
//        startRegService();
        ConsoleUtil.i(TAG, "--------Message:------------" + msg);
        SharedReferenceHelper.getInstance(context).setValue("REG_AUTO", "false");
        if(msg.equals("EMPTY")) {
            startRegService();
        }
        else {
            stopRegService();
        }
    }

    @Override
    public void onCheckFailure() {
        //如果失败，继续check
        startCheck();
    }
}
