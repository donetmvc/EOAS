package com.eland.android.eoas.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eland.android.eoas.Util.ConsoleUtil;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by liu.wenbin on 16/1/8.
 */
public class DeskCountChangeReceiver extends BroadcastReceiver{

    private String TAG = "EOAS";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(null != intent) {
            String action = intent.getAction();
            if(null != action && action.equals("EOAS_COUNT_CHANGED")) {
                ConsoleUtil.i(TAG, "========received count change============");
                int count = Integer.valueOf(intent.getStringExtra("COUNT"));
                ConsoleUtil.i(TAG, "========received count change============" + count);
                if(count > 0) {
                    ShortcutBadger.with(context.getApplicationContext()).count(count); //for 1.1.3
                }
                else {
                    ShortcutBadger.with(context.getApplicationContext()).remove();  //for 1.1.3
                }
            }
        }
    }
}
