package com.eland.android.eoas.Util;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class ActivityManager {

    private static ActivityManager activityManager;
    private ArrayList<Activity> activityList = new ArrayList<Activity>();

    private ActivityManager() {

    }

    public static ActivityManager getInstance() {
        if (null == activityManager) {
            activityManager = new ActivityManager();
        }
        return activityManager;
    }

    public Activity getTopActivity() {
        return activityList.get(activityList.size() - 1);
    }

    public void addActivity(Activity ac) {
        activityList.add(ac);
    }

    /**
     * 结束所有activity
     */
    public void removeAllActivity() {
        for (Activity ac : activityList) {
            if (null != ac) {
                if (!ac.isFinishing()) {
                    ac.finish();
                }
                ac = null;
            }
        }
        activityList.clear();
    }
}
