package com.eland.android.eoas.Util;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.util.Log;

import com.eland.android.eoas.Model.LoginInfo;

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

    public static Boolean isContains(ArrayList<LoginInfo> list, String userId) {
        boolean isContains = false;

        for(LoginInfo loginInfo : list) {

            if(null != loginInfo) {
                if(loginInfo.userId.equals(userId)) {
                    isContains = true;
                    break;
                }
            }
        }

        return isContains;
    }
}
