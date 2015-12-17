package com.eland.android.eoas.Application;

import android.app.Activity;
import android.app.Application;

import com.eland.android.eoas.Util.ActivityManager;
import com.pgyersdk.crash.PgyCrashManager;


/**
 * Created by liu.wenbin on 15/11/10.
 */
public class EOASApplication extends Application {

    public String TAG = "EOAS";
    public static EOASApplication mInstance;
//    public String apiUri = "http://222.126.161.59:8081/api/BDTransfor/Login/";
//    public String apiUri = "http://222.126.161.59:8081/";//api/BDTransfor/LoginGet";

    public String photoUri = "http://182.92.65.253:30001/Eland.EOAS/Images/";

    public static synchronized EOASApplication getInstance() {
        if(mInstance == null) {
            mInstance = new EOASApplication();
        }

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //蒲公英捕捉crash
        PgyCrashManager.register(this);
    }

    public void addActivity(Activity ac){
        ActivityManager.getInstance().addActivity(ac);
    }

    public void existApp() {
        ActivityManager.getInstance().removeAllActivity();
        System.exit(0);
    }
}
