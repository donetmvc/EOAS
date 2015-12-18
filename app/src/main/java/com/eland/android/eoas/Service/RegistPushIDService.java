package com.eland.android.eoas.Service;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 15/12/18.
 */
public class RegistPushIDService {

    public void registPush(String pushId, String imei, final IOnRegistListener iOnRegistListener) {
        String uri = "api/NewLogin";
        RequestParams params = new RequestParams();
        params.add("pushId", pushId);
        params.add("imei", imei);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    String message = response.getString("message");

                    if(message.equals("OK")) {
                        iOnRegistListener.onRegistSuccess();
                    }
                    else {
                        iOnRegistListener.onRegistFailure();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnRegistListener.onRegistFailure();
            }

        });
    }

    public interface IOnRegistListener {
        void onRegistSuccess();
        void onRegistFailure();
    }

}
