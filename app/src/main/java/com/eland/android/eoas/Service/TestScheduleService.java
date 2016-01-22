package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 16/1/22.
 * test android-async-http cancel
 */
public class TestScheduleService {

    private Context context;

    public TestScheduleService(Context context) {
        this.context = context;
    }

    public void regSchedule(String imei, String isAM, final IOnTestScheduleListener iOnTestScheduleListener) {
        String uri = "api/adm";
        RequestParams params = new RequestParams();
        params.put("iemi", imei);
        params.put("registTime", "");
        params.put("isAM", isAM);

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (null != iOnTestScheduleListener) {
                    iOnTestScheduleListener.onFailuer(99999, "无法连接服务器.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (null != iOnTestScheduleListener) {
                    if (responseString.equals("\"OK\"")) {
                        iOnTestScheduleListener.onSuccess();
                    } else {
                        iOnTestScheduleListener.onFailuer(44444, responseString);
                    }
                }

            }
        });
    }

    public void cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true);
    }

    public interface IOnTestScheduleListener {
        void onSuccess();
        void onFailuer(int code, String msg);
    }
}
