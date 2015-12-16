package com.eland.android.eoas.Service;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 15/12/8.
 */
public class UpdatePhoneNmService {

    private IOnUpdateListener iOnUpdateListener;

    public void updateCellNo(String userId, final String cellNo, final IOnUpdateListener iOnUpdateListener) {

        String uri = "api/Contact/";

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("cellNo", cellNo);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(null != iOnUpdateListener) {
                    iOnUpdateListener.onUpdateFailure(99999, "");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if(null != iOnUpdateListener) {

                    if(responseString.equals("\"OK\"")) {
                        iOnUpdateListener.onUpdateSuccess(cellNo);
                    }
                    else {
                        iOnUpdateListener.onUpdateFailure(1000, responseString);
                    }
                }
            }
        });

    }

    public interface IOnUpdateListener {
        void onUpdateSuccess(String number);
        void onUpdateFailure(int code, String msg);
    }

}
