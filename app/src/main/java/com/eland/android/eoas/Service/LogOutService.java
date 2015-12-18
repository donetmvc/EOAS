package com.eland.android.eoas.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 15/12/18.
 */
public class LogOutService extends Service{

    private String imei, userId = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null != intent) {
            userId = intent.getStringExtra("USERID");
            imei = intent.getStringExtra("IMEI");

            startLogOut(userId, imei);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startLogOut(final String userId, final String imei) {
        String uri = "api/NewLogin";
        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("imei", imei);
        params.add("type", "123");
        params.add("tips", "321");


        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                stopSelf();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
