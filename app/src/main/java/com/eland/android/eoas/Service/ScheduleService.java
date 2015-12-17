package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liuw.wenbin on 15/12/1.
 */
public class ScheduleService {

    private Context context;
    private IScheduleListener iScheduleListener;

    public void regSchedulePM(String imei, final String isAM) {
        String uri = "api/adm";
        RequestParams params = new RequestParams();
        params.put("iemi", imei);
        params.put("registTime", "");
        params.put("isAM", isAM);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (null != iScheduleListener) {
                    iScheduleListener.onScheduleFailure(99999, "无法连接服务器.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (null != iScheduleListener) {
                    if (responseString.equals("\"OK\"")) {
                        iScheduleListener.onScheduleSuccess();
                    } else {
                        iScheduleListener.onScheduleFailure(44444, responseString);
                    }
                }

            }
        });
    }

    public void regScheduleAM(String imei, String isAM) {
        String uri = "api/adm";
        RequestParams params = new RequestParams();
        params.put("iemi", imei);
        params.put("registTime", "");
        params.put("isAM", isAM);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (null != iScheduleListener) {
                    iScheduleListener.onScheduleFailure(99999, "无法连接服务器.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if(null != iScheduleListener) {
                    if(responseString.equals("\"OK\"")) {
                        iScheduleListener.onScheduleSuccess();
                    }
                    else {
                        iScheduleListener.onScheduleFailure(44444, responseString);
                    }
                }

            }
        });
    }

    public void setOnScheduleListener(IScheduleListener iScheduleListener) {
        this.iScheduleListener = iScheduleListener;
    }

    public interface IScheduleListener {
        void onScheduleSuccess();
        void onScheduleFailure(int code, String msg);
    }
}
