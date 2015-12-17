package com.eland.android.eoas.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Util.SharedReferenceHelper;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class LauncherActivity extends Activity{

    private String TAG = "EOAS";
    private Timer timer = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        context = this;

        initTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private void initTimer() {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                goLogin();
            }
        }, 3000);
    }

    private void goLogin() {

        String isLogin = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGIN_SUCCESS);
        Intent intent;

        if(isLogin.equals("TRUE")) {
            intent = new Intent(LauncherActivity.this, MainActivity.class);
        }
        else {
            intent = new Intent(LauncherActivity.this, LoginActivity.class);
        }

        startActivity(intent);

        finish();
    }
}
