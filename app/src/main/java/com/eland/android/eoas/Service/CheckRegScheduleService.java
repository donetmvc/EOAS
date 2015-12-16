package com.eland.android.eoas.Service;

import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 15/12/9.
 */
public class CheckRegScheduleService {

    private String TAG = "EOAS";
    private IOnCheckListener iOnCheckListener;

    public void check(String imei, final IOnCheckListener iOnCheckListener) {
        String uri = "api/adm";

        RequestParams params = new RequestParams();
        params.put("userName", imei);
        params.put("type", "01");
        params.put("app", "1");

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ConsoleUtil.i(TAG, "--------ACTION:------------" + "server error.");
                iOnCheckListener.onCheckFailure();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ConsoleUtil.i(TAG, "--------ACTION:------------" + "request success.");

                if(responseString.equals("\"EMPTY\"")) {
                    iOnCheckListener.onCheckSuccess("EMPTY");
                }
                else {
                    iOnCheckListener.onCheckSuccess("NOTEMPTY");
                }
            }

        });
    }

    public interface IOnCheckListener {
        void onCheckSuccess(String msg);
        void onCheckFailure();
    }
}
