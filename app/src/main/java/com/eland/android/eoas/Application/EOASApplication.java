package com.eland.android.eoas.Application;

import android.app.Activity;
import android.app.Application;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Util.ActivityManager;
import com.eland.android.eoas.Util.CacheInfoUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.pgyersdk.crash.PgyCrashManager;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by liu.wenbin on 15/11/10.
 */
public class EOASApplication extends Application {

    public String TAG = "EOAS";
    public static EOASApplication mInstance;
//    public String apiUri = "http://222.126.161.59:8081/api/BDTransfor/Login/";
//    public String apiUri = "http://222.126.161.59:8081/";//api/BDTransfor/LoginGet";

    public String photoUri = "http://182.92.65.253:30001/Eland.EOAS/Images/";
    public boolean isReceivePush = false;

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

        //初始化JPush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        initApplicantion();

        //判断是否需要暂停接受push
        if(!isReceivePush) {
            JPushInterface.stopPush(getApplicationContext());
        }
        else {
            JPushInterface.resumePush(getApplicationContext());
        }
    }

    private void initApplicantion() {
        String loginId = SharedReferenceHelper.getInstance(getApplicationContext()).getValue(Constant.LOGINID);
        if(null == loginId || loginId.isEmpty()) {
            //还未登录时暂时不接受push消息
            isReceivePush = false;
        }
        else {
            //如果登录了的话是可以接收消息的
            //判断用户接收消息与否
            //登录系统后开启接收push,当然需要判断下用户是否设置了接收与否
            if(CacheInfoUtil.loadIsReceive(this, loginId)) {
                isReceivePush = true;
            } else {
                isReceivePush = false;
            }

        }
    }

    public void addActivity(Activity ac){
        ActivityManager.getInstance().addActivity(ac);
    }

    public void existApp() {
        ActivityManager.getInstance().removeAllActivity();
        System.exit(0);
    }
}
