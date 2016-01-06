package com.eland.android.eoas.Service;

import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 15/12/30.
 */
public class ApplyService {

    private static String TAG = "EOAS";

    public static void searchVacationType(String userId, final IOnApplyListener iOnApplyListener) {
        String uri = "api/Apply";

        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("type", "02");

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                iOnApplyListener.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApplyListener.onFailure(1, 99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnApplyListener.onFailure(1, 99999, "连接服务器超时");
            }

        });

    }

    public static void searchVacationDays(String userId, final IOnApplyListener iOnApplyListener) {
        String uri = "api/Apply";

        RequestParams params = new RequestParams();
        params.add("userId", userId);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                iOnApplyListener.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApplyListener.onFailure(2, 99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnApplyListener.onFailure(2, 99999, "连接服务器超时");
            }
        });
    }

    public static void searchDiffDays(String userId, String startDate, String endDate,
                                      String startTime, String endTime, String vacationType,
                                      final IOnApplyListener iOnApplyListener) {
        String uri = "api/Time";
        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("startDate", startDate);
        params.add("endDate", endDate);
        params.add("startTime", startTime);
        params.add("endTime", endTime);
        params.add("vacationType", vacationType);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApplyListener.onFailure(3, 99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnApplyListener.onSuccess(responseString);
            }
        });
    }

    public static void saveApply(String userId, String startDate, String endDate, String startTime, String endTime,
                                 String vacationType, String vacationDate, String remark,
                                 final IOnApplyListener iOnApplyListener) {
        String uri = "api/Apply";
        RequestParams params = new RequestParams();
        params.add("s1", startDate);
        params.add("s2", endDate);
        params.add("s3", startTime);
        params.add("s4", endTime);
        params.add("s5", vacationType);
        params.add("s6", vacationDate);
        params.add("s7", userId);
        params.add("s8", remark);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApplyListener.onFailure(4, 99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnApplyListener.onSuccess(4, responseString);
            }

        });

    }

    public static void searchApprogressList(String userId, String applyId, final IOnApplyListener iOnApplyListener) {
        String uri = "api/ADM";
        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("applyId", applyId);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                iOnApplyListener.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApplyListener.onFailure(1, 99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnApplyListener.onFailure(1, 99999, "连接服务器超时");
            }
        });
    }

    public interface IOnApplyListener {
        void onSuccess(JSONObject obj);
        void onSuccess(JSONArray array);
        void onSuccess(String diffDays);
        void onSuccess(int type, String msg);
        void onFailure(int type, int code, String msg);
    }

}
