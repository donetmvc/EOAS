package com.eland.android.eoas.Util;

import android.content.Context;

import java.util.ArrayList;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

/**
 * Created by liu.wenbin on 15/12/9.
 */
public class SystemMethodUtil {

    public static Boolean isWorked(Context context, String serviceName) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningServiceInfos = (ArrayList<RunningServiceInfo>) activityManager.getRunningServices(30);
        for(int i=0; i < runningServiceInfos.size(); i++) {
            if(runningServiceInfos.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }

        return false;
    }
}
