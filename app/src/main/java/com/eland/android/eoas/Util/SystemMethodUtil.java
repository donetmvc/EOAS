package com.eland.android.eoas.Util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;


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

    public static Boolean isInnerNetwork() {
        Process p;
        try {
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + "10.202.101.23");
            int status = p.waitFor();
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null){
                buffer.append(line);
            }
            System.out.println("Return ============" + buffer.toString());
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
