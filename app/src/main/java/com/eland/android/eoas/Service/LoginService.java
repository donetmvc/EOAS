package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by liu.wenbin on 15/11/10.
 */
public class LoginService {

    private Context context;
    public ISignInListener isignInListener;

    public LoginService(Context context) {
        this.context = context;
    }

    public void signIn(String loginId, String password, String username, String imei) {
        String uri = "api/NewLogin";

        RequestParams params = new RequestParams();
        params.put("userId", loginId);
        params.put("password", password);
        params.put("imei", imei);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if(null != isignInListener) {
                    try {
                        int code = Integer.valueOf(response.getString("code"));
                        String cellNo = response.getString("cellNo");
                        String message = response.getString("message");
                        String userName = response.getString("userName");
                        String email = response.getString("email");
                        if(code == 1000) {
                            LoginInfo info = new LoginInfo();
                            info.cellNo = cellNo;
                            info.userName = userName;
                            info.email = email;
                            isignInListener.onSignInSuccess(info);
                        }
                        else {
                            isignInListener.onSignInFailure(code, message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(null != isignInListener) {
                    isignInListener.onSignInFailure(99999, "");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if(null != isignInListener) {
                    isignInListener.onSignInFailure(99999, "");
                }

            }
        });
    }

    public void setOnSignInListener(ISignInListener isignInListener) {
        this.isignInListener = isignInListener;
    }

    public interface ISignInListener {
        void onSignInSuccess(LoginInfo info);
        void onSignInFailure(int code, String msg);
    }
}
