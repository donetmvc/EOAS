package com.eland.android.eoas.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Service.RegistAutoService;
import com.eland.android.eoas.Util.ActivityManager;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.SystemMethodUtil;
import com.pgyersdk.crash.PgyCrashManager;

import cn.jpush.android.api.JPushInterface;
import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by liu.wenbin on 15/11/10.
 */
public class EOASApplication extends Application {

    public String TAG = "EOAS";
    public static EOASApplication mInstance;

    public String photoUri = "http://182.92.65.253:30001/Eland.EOAS/Images/";

//    public String photoUri = "http://10.202.101.11:30002/Eland.EOAS/Images/";
    public String apiUri;

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

        //初始化JPush 正式发布是，需要把注释打开
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);

        initApplicantion();
    }

    private void initApplicantion() {
        //check is push id regist success or failure?
        //the first time launcher app it does't work
        String isOk = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_PUSHID);//"FAILURE");
        if(isOk.equals("FAILURE")) {
            String pushId = JPushInterface.getRegistrationID(this);
            //retry to regist push id
            startRegistPushId(pushId);
        }
    }

    private void startRegistPushId(String pushId) {
        Intent intents = new Intent(this, RegistAutoService.class);
        intents.putExtra("PUSHID", pushId);
        startService(intents);
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void addActivity(Activity ac){
        ActivityManager.getInstance().addActivity(ac);
    }

    public void existApp() {
        ActivityManager.getInstance().removeAllActivity();
        System.exit(0);
    }
}
